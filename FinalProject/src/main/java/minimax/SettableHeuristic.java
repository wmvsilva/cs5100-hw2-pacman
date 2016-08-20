package minimax;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public class SettableHeuristic implements Heuristic
{
    private Map<String, Integer> fieldToWeights;
    private static final Map<Constants.MOVE, Constants.MOVE> moveToOppositeMoves = moveToOppositeMovesMap();

    public SettableHeuristic(Map<String, Integer> fieldToWeights)
    {
        this.fieldToWeights = checkNotNull(fieldToWeights);
    }

    private static Map<Constants.MOVE, Constants.MOVE> moveToOppositeMovesMap()
    {
        Map<Constants.MOVE, Constants.MOVE> moveOppositeMoveMap = new HashMap<>();
        moveOppositeMoveMap.put(Constants.MOVE.DOWN, Constants.MOVE.UP);
        moveOppositeMoveMap.put(Constants.MOVE.UP, Constants.MOVE.DOWN);
        moveOppositeMoveMap.put(Constants.MOVE.RIGHT, Constants.MOVE.LEFT);
        moveOppositeMoveMap.put(Constants.MOVE.LEFT, Constants.MOVE.RIGHT);
        return moveOppositeMoveMap;
    }

    @Override
    public int heuristicVal(Game game, Queue<Constants.MOVE> moveHistory)
    {
        // Determine distance to nearest ghost
        int distanceToBlinky = shortestPathDistanceToGhost(game, Constants.GHOST.BLINKY);
        int distanceToInky = shortestPathDistanceToGhost(game, Constants.GHOST.INKY);
        int distanceToPinky = shortestPathDistanceToGhost(game, Constants.GHOST.PINKY);
        int distanceToSue = shortestPathDistanceToGhost(game, Constants.GHOST.SUE);
        Map<Constants.GHOST, Integer> ghostsToDistance = new HashMap<>();
        ghostsToDistance.put(Constants.GHOST.BLINKY, distanceToBlinky);
        ghostsToDistance.put(Constants.GHOST.INKY, distanceToInky);
        ghostsToDistance.put(Constants.GHOST.PINKY, distanceToPinky);
        ghostsToDistance.put(Constants.GHOST.SUE, distanceToSue);

        int distanceToNearestGhost = Collections.min(Lists.newArrayList(distanceToBlinky, distanceToInky,
                distanceToPinky, distanceToSue));
        Constants.GHOST nearestGhost = null;
        // Determine the nearest ghost
        for (Map.Entry<Constants.GHOST, Integer> ghostDistance : ghostsToDistance.entrySet()) {
            if (ghostDistance.getValue() == distanceToNearestGhost) {
                nearestGhost = ghostDistance.getKey();
            }
        }
        boolean isNearestGhostEdible = game.isGhostEdible(nearestGhost);
        int distanceToNearestGhostIfNotEdible = distanceToNearestGhost;
        if (game.isGhostEdible(nearestGhost)) {
            distanceToNearestGhostIfNotEdible = 0;
        }
        // Determine all active pill indices to be used to find closest pill
        List<Integer> activePillIndices = new ArrayList<>();
        activePillIndices.addAll(Ints.asList(game.getActivePillsIndices()));
        activePillIndices.addAll(Ints.asList(game.getActivePowerPillsIndices()));

        List<Integer> distancesToPills = new ArrayList<>();
        for (int pillIndice : activePillIndices) {
            distancesToPills.add(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), pillIndice));
        }
        int distanceToNearestPill = 0;
        if (!distancesToPills.isEmpty()) {
            // If there are no pills at the end of the level, Collections.min throws an exception
            distanceToNearestPill = Collections.min(distancesToPills);
        }

        Set<Constants.MOVE> possibleBlinkyMoves = MiniMax.getPossibleGhostMoves(game, Constants.GHOST.BLINKY);
        Set<Constants.MOVE> possibleInkyMoves = MiniMax.getPossibleGhostMoves(game, Constants.GHOST.INKY);
        Set<Constants.MOVE> possiblePinkyMoves = MiniMax.getPossibleGhostMoves(game, Constants.GHOST.PINKY);
        Set<Constants.MOVE> possibleSueMoves = MiniMax.getPossibleGhostMoves(game, Constants.GHOST.SUE);

        int distanceToNextNearestPillMOreThan10IfPillJustEaten = 0;
        if (game.wasPillEaten() || distanceToNearestPill > 10) {
            distanceToNextNearestPillMOreThan10IfPillJustEaten = distanceToNearestPill;
        }

        Set<Constants.MOVE> moveHistorySet = new HashSet<>(moveHistory);
        int unvariedMoves = 0;
        if (moveHistorySet.size() == 2) {
            Constants.MOVE move = moveHistorySet.iterator().next();
            int occurrences = Collections.frequency(moveHistory, move);
            double percentage = (double) occurrences / (double) moveHistory.size();
            unvariedMoves = (int) (Math.abs(0.5 - percentage) * 100);
        }

        boolean reversedMove = false;
        if (moveHistory.size() >= 2) {
            List<Constants.MOVE> moveList = new ArrayList<>(moveHistory);
            Constants.MOVE lastMove = moveList.get(moveHistory.size() - 1);
            Constants.MOVE secondToLastMove = moveList.get(moveHistory.size() - 2);
            if (moveToOppositeMoves.get(lastMove) == secondToLastMove) {
                reversedMove = true;
            }
        }

        return fieldToWeights.get("pacManEaten") * boolToNum(game.wasPacManEaten()) +
                fieldToWeights.get("numActivePills") * game.getNumberOfActivePills() +
                fieldToWeights.get("numActivePowerPills") * game.getNumberOfActivePowerPills() +
                fieldToWeights.get("score") * game.getScore() +
                fieldToWeights.get("pacManDistanceToUnedibleBlinky") * (!game.isGhostEdible(Constants.GHOST.BLINKY) ? shortestPathDistanceToGhost(game, Constants.GHOST.BLINKY) : 0) +
                fieldToWeights.get("pacManDistanceToUnedibleInky") * (!game.isGhostEdible(Constants.GHOST.INKY) ? shortestPathDistanceToGhost(game, Constants.GHOST.INKY) : 0)  +
                fieldToWeights.get("pacManDistanceToUnediblePinky") * (!game.isGhostEdible(Constants.GHOST.PINKY) ? shortestPathDistanceToGhost(game, Constants.GHOST.PINKY) : 0) +
                fieldToWeights.get("pacManDistanceToUnedibleSue") * (!game.isGhostEdible(Constants.GHOST.SUE) ? shortestPathDistanceToGhost(game, Constants.GHOST.SUE) : 0) +
                //fieldToWeights.get("pacManDistanceToNearestGhostIfNotEdible") * distanceToNearestGhostIfNotEdible +
                fieldToWeights.get("pacManNearestGhostEdible") * boolToNum(game.isGhostEdible(nearestGhost)) +
                fieldToWeights.get("pacManDistanceToNearestGhostUnder20") * ((distanceToNearestGhost < 20 && !isNearestGhostEdible) ? distanceToNearestGhost : 0) +
                fieldToWeights.get("pacManNearestGhostEdibleAndUnder40") * boolToNum(game.isGhostEdible(nearestGhost) && distanceToNearestGhost <= 40) +
                fieldToWeights.get("pacManDistanceToNearestPill") * distanceToNearestPill +

                fieldToWeights.get("numTotalActivePills") * (game.getNumberOfActivePills() + game.getNumberOfActivePowerPills()) +
                fieldToWeights.get("numLevel") * game.getCurrentLevel() +
                fieldToWeights.get("levelTime") * game.getCurrentLevelTime() +
                fieldToWeights.get("totalGameTime") * game.getTotalTime() +
                fieldToWeights.get("numGhostsEaten") * game.getNumGhostsEaten() +
                fieldToWeights.get("livesRemaining") * game.getPacmanNumberOfLivesRemaining() +
                fieldToWeights.get("gameOver") * boolToNum(game.gameOver()) +
                fieldToWeights.get("wasPillEaten") * boolToNum(game.wasPillEaten()) +
                fieldToWeights.get("wasPowerPillEaten") * boolToNum(game.wasPowerPillEaten()) +

                fieldToWeights.get("pacManNumPossibleMoves") * game.getPossibleMoves(game.getPacmanCurrentNodeIndex()).length +
                fieldToWeights.get("blinkyNumPossibleMoves") * possibleBlinkyMoves.size() +
                fieldToWeights.get("inkyNumPossibleMoves") * possibleInkyMoves.size() +
                fieldToWeights.get("pinkyNumPossibleMoves") * possiblePinkyMoves.size() +
                fieldToWeights.get("sueNumPossibleMoves") * possibleSueMoves.size() +
                fieldToWeights.get("distanceToNextNearestPillMOreThan10IfPillJustEaten") * distanceToNextNearestPillMOreThan10IfPillJustEaten +
                fieldToWeights.get("distanceToNearestPillAboveFive") * boolToNum(distanceToNearestPill >= 5) +

                fieldToWeights.get("pacManLastMoveLeft") * boolToNum(game.getPacmanLastMoveMade() == Constants.MOVE.LEFT) +
                fieldToWeights.get("pacManLastMoveDown") * boolToNum(game.getPacmanLastMoveMade() == Constants.MOVE.DOWN) +

                fieldToWeights.get("pacManDistanceToNearestGhostIfUnder10") * ((distanceToNearestGhost < 10 && !isNearestGhostEdible) ? distanceToNearestGhost : 0) +
                fieldToWeights.get("pacManDistanceToNearestGhostIfUnder5") * ((distanceToNearestGhost < 5 && !isNearestGhostEdible) ? distanceToNearestGhost : 0) +

                fieldToWeights.get("likelyNotStuck") * unvariedMoves +
                fieldToWeights.get("reversedDirection") * boolToNum(reversedMove) +

                fieldToWeights.get("pacManDistanceToNearestGhostEdibleIfUnder10") * ((distanceToNearestGhost < 10 && isNearestGhostEdible) ? distanceToNearestGhost : 0) +
                fieldToWeights.get("pacManDistanceToNearestGhostEdibleIfUnder5") * ((distanceToNearestGhost < 5 && isNearestGhostEdible) ? distanceToNearestGhost : 0);
    }

    private static int boolToNum(boolean bool)
    {
        if (bool) {
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * @param game copy of a game
     * @param ghost one of the four ghosts
     * @return the shortest distance from PacMan to the given ghost
     */
    private static int shortestPathDistanceToGhost(Game game, Constants.GHOST ghost)
    {
        return game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
    }
}
