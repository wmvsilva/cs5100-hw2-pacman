package minimax;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
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
import static pacman.game.Constants.GHOST.BLINKY;
import static pacman.game.Constants.GHOST.INKY;
import static pacman.game.Constants.GHOST.PINKY;
import static pacman.game.Constants.GHOST.SUE;
import static pacman.game.Constants.MOVE.DOWN;
import static pacman.game.Constants.MOVE.LEFT;
import static pacman.game.Constants.MOVE.UP;

/**
 * Evaluation function to evaluate the state of the game. It takes in feature weights for all of its features.
 */
public class SettableHeuristic implements Heuristic
{
    /**
     * Map of feature names to the feature weights for that feature
     */
    private Map<String, Integer> fieldToWeights;
    /**
     * Map of possible moves to the moves in the opposite direction
     */
    private static final Map<MOVE, MOVE> MOVE_TO_OPPOSITE_MOVE = moveToOppositeMovesMap();

    /**
     * @param fieldToWeights feature weights to use in this evaluation function
     */
    public SettableHeuristic(Map<String, Integer> fieldToWeights)
    {
        this.fieldToWeights = checkNotNull(fieldToWeights);
    }

    /**
     * @return map of moves in Pac-Man to the move in the opposite direction
     */
    private static Map<MOVE, MOVE> moveToOppositeMovesMap()
    {
        Map<MOVE, MOVE> moveOppositeMoveMap = new HashMap<>();
        moveOppositeMoveMap.put(DOWN, UP);
        moveOppositeMoveMap.put(UP, MOVE.DOWN);
        moveOppositeMoveMap.put(MOVE.RIGHT, MOVE.LEFT);
        moveOppositeMoveMap.put(MOVE.LEFT, MOVE.RIGHT);
        return moveOppositeMoveMap;
    }

    @Override
    public int heuristicVal(Game game, Queue<MOVE> pacManMoveHistory)
    {
        // Determine distance to nearest ghost
        int distanceToBlinky = shortestPathDistanceToGhost(game, BLINKY);
        int distanceToInky = shortestPathDistanceToGhost(game, INKY);
        int distanceToPinky = shortestPathDistanceToGhost(game, PINKY);
        int distanceToSue = shortestPathDistanceToGhost(game, SUE);
        Map<GHOST, Integer> ghostsToDistance = new HashMap<>();
        ghostsToDistance.put(BLINKY, distanceToBlinky);
        ghostsToDistance.put(INKY, distanceToInky);
        ghostsToDistance.put(PINKY, distanceToPinky);
        ghostsToDistance.put(SUE, distanceToSue);

        int distanceToNearestGhost = Collections.min(Lists.newArrayList(distanceToBlinky, distanceToInky,
                distanceToPinky, distanceToSue));
        GHOST nearestGhost = null;
        // Determine the nearest ghost
        for (Map.Entry<GHOST, Integer> ghostDistance : ghostsToDistance.entrySet()) {
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

        Set<MOVE> possibleBlinkyMoves = MinimaxAlgorithm.getPossibleGhostMoves(game, BLINKY);
        Set<MOVE> possibleInkyMoves = MinimaxAlgorithm.getPossibleGhostMoves(game, INKY);
        Set<MOVE> possiblePinkyMoves = MinimaxAlgorithm.getPossibleGhostMoves(game, PINKY);
        Set<MOVE> possibleSueMoves = MinimaxAlgorithm.getPossibleGhostMoves(game, SUE);

        int distanceToNextNearestPillMOreThan10IfPillJustEaten = 0;
        if (game.wasPillEaten() || distanceToNearestPill > 10) {
            distanceToNextNearestPillMOreThan10IfPillJustEaten = distanceToNearestPill;
        }

        Set<MOVE> moveHistorySet = new HashSet<>(pacManMoveHistory);
        int unvariedMoves = 0;
        if (moveHistorySet.size() == 2) {
            MOVE move = moveHistorySet.iterator().next();
            int occurrences = Collections.frequency(pacManMoveHistory, move);
            double percentage = (double) occurrences / (double) pacManMoveHistory.size();
            unvariedMoves = (int) (Math.abs(0.5 - percentage) * 100);
        }

        boolean reversedMove = false;
        if (pacManMoveHistory.size() >= 2) {
            List<MOVE> moveList = new ArrayList<>(pacManMoveHistory);
            MOVE lastMove = moveList.get(pacManMoveHistory.size() - 1);
            MOVE secondToLastMove = moveList.get(pacManMoveHistory.size() - 2);
            if (MOVE_TO_OPPOSITE_MOVE.get(lastMove) == secondToLastMove) {
                reversedMove = true;
            }
        }

        return fieldToWeights.get("pacManEaten") * boolToNum(game.wasPacManEaten()) +
                fieldToWeights.get("numActivePills") * game.getNumberOfActivePills() +
                fieldToWeights.get("numActivePowerPills") * game.getNumberOfActivePowerPills() +
                fieldToWeights.get("score") * game.getScore() +
                fieldToWeights.get("pacManDistanceToUnedibleBlinky") * (!game.isGhostEdible(BLINKY) ? shortestPathDistanceToGhost(game, BLINKY) : 0) +
                fieldToWeights.get("pacManDistanceToUnedibleInky") * (!game.isGhostEdible(INKY) ? shortestPathDistanceToGhost(game, INKY) : 0)  +
                fieldToWeights.get("pacManDistanceToUnediblePinky") * (!game.isGhostEdible(PINKY) ? shortestPathDistanceToGhost(game, PINKY) : 0) +
                fieldToWeights.get("pacManDistanceToUnedibleSue") * (!game.isGhostEdible(SUE) ? shortestPathDistanceToGhost(game, SUE) : 0) +
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

                fieldToWeights.get("pacManLastMoveLeft") * boolToNum(game.getPacmanLastMoveMade() == LEFT) +
                fieldToWeights.get("pacManLastMoveDown") * boolToNum(game.getPacmanLastMoveMade() == DOWN) +

                fieldToWeights.get("pacManDistanceToNearestGhostIfUnder10") * ((distanceToNearestGhost < 10 && !isNearestGhostEdible) ? distanceToNearestGhost : 0) +
                fieldToWeights.get("pacManDistanceToNearestGhostIfUnder5") * ((distanceToNearestGhost < 5 && !isNearestGhostEdible) ? distanceToNearestGhost : 0) +

                fieldToWeights.get("likelyNotStuck") * unvariedMoves +
                fieldToWeights.get("reversedDirection") * boolToNum(reversedMove) +

                fieldToWeights.get("pacManDistanceToNearestGhostEdibleIfUnder10") * ((distanceToNearestGhost < 10 && isNearestGhostEdible) ? distanceToNearestGhost : 0) +
                fieldToWeights.get("pacManDistanceToNearestGhostEdibleIfUnder5") * ((distanceToNearestGhost < 5 && isNearestGhostEdible) ? distanceToNearestGhost : 0);
    }

    /**
     * @param bool some boolean value
     * @return 1 if bool is true, 0 if bool is false
     */
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
    private static int shortestPathDistanceToGhost(Game game, GHOST ghost)
    {
        return game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
    }
}
