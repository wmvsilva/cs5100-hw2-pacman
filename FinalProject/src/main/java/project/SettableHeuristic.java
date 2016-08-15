package project;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by William on 8/15/2016.
 */
public class SettableHeuristic implements Heuristic
{
    private Map<String, Integer> fieldToWeights;

    public SettableHeuristic(Map<String, Integer> fieldToWeights)
    {
        this.fieldToWeights = checkNotNull(fieldToWeights);
    }

    @Override
    public int heuristicVal(Game game)
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

        return fieldToWeights.get("pacManEaten") * boolToNum(game.wasPacManEaten()) +
                fieldToWeights.get("numActivePills") * game.getNumberOfActivePills() +
                fieldToWeights.get("numActivePowerPills") * game.getNumberOfActivePowerPills() +
                fieldToWeights.get("score") * game.getScore() +
                fieldToWeights.get("pacManDistanceToBlinky") * shortestPathDistanceToGhost(game, Constants.GHOST.BLINKY) +
                fieldToWeights.get("pacManDistanceToInky") * shortestPathDistanceToGhost(game, Constants.GHOST.INKY) +
                fieldToWeights.get("pacManDistanceToPinky") * shortestPathDistanceToGhost(game, Constants.GHOST.PINKY) +
                fieldToWeights.get("pacManDistanceToSue") * shortestPathDistanceToGhost(game, Constants.GHOST.SUE) +
                fieldToWeights.get("pacManDistanceToNearestGhostIfNotEdible") * distanceToNearestGhostIfNotEdible +
                fieldToWeights.get("pacManNearestGhostEdible") * boolToNum(game.isGhostEdible(nearestGhost)) +
                fieldToWeights.get("pacManDistanceToNearestGhostUnder20") * boolToNum(distanceToNearestGhost < 20) +
                fieldToWeights.get("pacManNearestGhostEdibleAndUnder40") * boolToNum(game.isGhostEdible(nearestGhost) && distanceToNearestGhost <= 40) +
                fieldToWeights.get("pacManDistanceToNearestPill") * distanceToNearestPill +

                fieldToWeights.get("numTotalActivePills") * game.getNumberOfActivePills() + game.getNumberOfActivePowerPills() +
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
                fieldToWeights.get("pacManLastMoveDown") * boolToNum(game.getPacmanLastMoveMade() == Constants.MOVE.DOWN);
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
