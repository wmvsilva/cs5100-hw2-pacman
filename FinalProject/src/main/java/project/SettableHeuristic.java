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

        return fieldToWeights.get("pacManEaten") * boolToNum(game.wasPacManEaten()) +
                fieldToWeights.get("numActivePills") * game.getNumberOfActivePills() +
                fieldToWeights.get("numActivePowerPills") * game.getNumberOfActivePowerPills() +
                fieldToWeights.get("score") * game.getScore() +
                fieldToWeights.get("pacManDistanceToBlinky") * shortestPathDistanceToGhost(game, Constants.GHOST.BLINKY) +
                fieldToWeights.get("pacManDistanceToInky") * shortestPathDistanceToGhost(game, Constants.GHOST.INKY) +
                fieldToWeights.get("pacManDistanceToPinky") * shortestPathDistanceToGhost(game, Constants.GHOST.PINKY) +
                fieldToWeights.get("pacManDistanceToSue") * shortestPathDistanceToGhost(game, Constants.GHOST.SUE) +
                fieldToWeights.get("pacManDistanceToNearestGhost") * distanceToNearestGhost +
                fieldToWeights.get("pacManNearestGhostEdible") * boolToNum(game.isGhostEdible(nearestGhost)) +
                fieldToWeights.get("pacManDistanceToNearestGhostUnder20") * boolToNum(distanceToNearestGhost < 20) +
                fieldToWeights.get("pacManNearestGhostEdibleAndUnder40") * boolToNum(game.isGhostEdible(nearestGhost) && distanceToNearestGhost <= 40) +
                fieldToWeights.get("pacManDistanceToNearestPill") * distanceToNearestPill;
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
