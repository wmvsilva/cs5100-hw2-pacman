package project;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;

/**
 * Created by William on 8/15/2016.
 */
public class PacManHeuristic implements Heuristic
{
    public int heuristicVal(Game game, Queue<Constants.MOVE> ignored)
    {
        if (game.wasPacManEaten()) {
            // PacMan dying should have lowest possible value
            return Integer.MIN_VALUE;
        }

        int totalPills = game.getNumberOfActivePills() + game.getNumberOfActivePowerPills();
        int score = game.getScore();

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

        // If 20 or closer, ghosts are very bad!
        int weightedGhostScore = -500 * (20 - distanceToNearestGhost);
        if (distanceToNearestGhost >= 20 || game.isGhostEdible(nearestGhost)) {
            weightedGhostScore = 0;
        }
        // If 40 or closer and ghosts are edible, PacMan wants to get near the ghost to eat it!
        int weightedEatingGhostScore = 0;
        if (game.isGhostEdible(nearestGhost) && distanceToNearestGhost <= 40) {
            weightedEatingGhostScore = 50 * (40 - distanceToNearestGhost);
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

        return -1 * totalPills + -1 * distanceToNearestPill + 100 * score + weightedGhostScore +
                weightedEatingGhostScore;
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
