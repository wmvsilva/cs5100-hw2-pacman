package entrants.pacman.silvaw;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class MyPacManMiniMax extends PacmanController
{
    private static final int DEPTH = 5;

    public MOVE getMove(Game game, long timeDue)
    {
        return determineMoveFromMiniMax(game);
    }

    private MOVE determineMoveFromMiniMax(Game game)
    {
        Tree miniMaxTree = createMiniMaxTree(game, DEPTH, true);
        return MOVE.NEUTRAL;
        // TODO return bestMoveFromTree(miniMaxTree);
    }

    private Tree createMiniMaxTree(Game game, int depth, boolean isPacman)
    {
        if (depth == 0 || endGameState(game))
        {
            return new Leaf(hValue(game));
        }

        if (isPacman) {
            Game leftGame = stateAfterPacMove(MOVE.LEFT, game);
            Game rightGame = stateAfterPacMove(MOVE.RIGHT, game);
            Game upGame = stateAfterPacMove(MOVE.UP, game);
            Game downGame = stateAfterPacMove(MOVE.DOWN, game);

            Map<MOVE, Tree> branches = new HashMap<>();
            branches.put(MOVE.LEFT, createMiniMaxTree(leftGame, depth - 1, false));
            branches.put(MOVE.RIGHT, createMiniMaxTree(rightGame, depth - 1, false));
            branches.put(MOVE.UP, createMiniMaxTree(upGame, depth - 1, false));
            branches.put(MOVE.DOWN, createMiniMaxTree(downGame, depth - 1, false));

            return new Node(branches);
        } else {
            // Ghosts turn
            Set<MOVE> possibleBlinkyMoves = getPossibleGhostMoves(game, GHOST.BLINKY);
            Set<MOVE> possibleInkyMoves = getPossibleGhostMoves(game, GHOST.INKY);
            Set<MOVE> possiblePinkyMoves = getPossibleGhostMoves(game, GHOST.PINKY);
            Set<MOVE> possibleSueMoves = getPossibleGhostMoves(game, GHOST.SUE);

            Set<Map<GHOST, MOVE>> possibleGhostCombinations = calculateGhostCombinations(possibleBlinkyMoves,
                    possibleInkyMoves,
                    possiblePinkyMoves,
                    possibleSueMoves);

            Set<Tree> ghostBranches = new HashSet<>();
            for (Map<GHOST, MOVE> possibleGhostMoves : possibleGhostCombinations) {
                Game gameStateAfterGhosts = gameStateAfterGhosts(game, possibleGhostMoves);
                ghostBranches.add(createMiniMaxTree(gameStateAfterGhosts, depth - 1, true));
            }

            return new GhostNode(ghostBranches);
        }
    }

    private boolean endGameState(Game game)
    {
        return game.wasPacManEaten() ||
                (game.getNumberOfActivePills() == 0 && game.getNumberOfActivePowerPills() == 0) ||
                game.gameOver();
    }

    private int hValue(Game game)
    {
        if (game.wasPacManEaten()) {
            return Integer.MIN_VALUE;
        }

        int numberOfPills = game.getNumberOfActivePills();
        int numberOfPowerPills = game.getNumberOfActivePowerPills();
        int totalPills = numberOfPills + numberOfPowerPills;
        int score = game.getScore();

        int distanceToBlinky = shortestPathDistanceToGhost(game, GHOST.BLINKY);
        int distanceToInky = shortestPathDistanceToGhost(game, GHOST.INKY);
        int distanceToPinky = shortestPathDistanceToGhost(game, GHOST.PINKY);
        int distanceToSue = shortestPathDistanceToGhost(game, GHOST.SUE);

        int distanceToNearestGhost = Collections.min(Lists.newArrayList(distanceToBlinky, distanceToInky, distanceToPinky, distanceToSue));

        List<Integer> activePillIndices = new ArrayList<>();
        activePillIndices.addAll(Ints.asList(game.getActivePillsIndices()));
        activePillIndices.addAll(Ints.asList(game.getActivePowerPillsIndices()));

        List<Integer> distancesToPills = new ArrayList<>();
        for (int pillIndice : activePillIndices) {
            distancesToPills.add(game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), pillIndice));
        }
        int distanceToNearestPill = Collections.min(distancesToPills);

        return -1 * totalPills + distanceToNearestGhost + -1 * distanceToNearestPill;
    }

    private int shortestPathDistanceToGhost(Game game, GHOST ghost)
    {
        return game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
    }

    private Game stateAfterPacMove(MOVE pacMove, Game curGame)
    {
        Game copyOfGame = curGame.copy();
        copyOfGame.updatePacMan(pacMove);
        return copyOfGame;
    }

    private Set<Map<GHOST, MOVE>> calculateGhostCombinations(Set<MOVE> possibleBlinkyMoves,
                                                             Set<MOVE> possibleInkyMoves,
                                                             Set<MOVE> possiblePinkyMoves,
                                                             Set<MOVE> possibleSueMoves)
    {
        Set<Map<GHOST, MOVE>> result = new HashSet<>();
        for (MOVE blinkyMove : possibleBlinkyMoves) {
            for (MOVE inkyMove : possibleInkyMoves) {
                for (MOVE pinkyMove : possiblePinkyMoves) {
                    for (MOVE sueMove : possibleSueMoves) {
                        Map<GHOST, MOVE> possibleMoveSet = new HashMap<>();
                        possibleMoveSet.put(GHOST.BLINKY, blinkyMove);
                        possibleMoveSet.put(GHOST.INKY, inkyMove);
                        possibleMoveSet.put(GHOST.PINKY, pinkyMove);
                        possibleMoveSet.put(GHOST.SUE, sueMove);

                        result.add(possibleMoveSet);
                    }
                }
            }
        }

        return result;
    }

    private Game gameStateAfterGhosts(Game game, Map<GHOST, MOVE> ghostMoves)
    {
        Game copyOfGame = game.copy();
        EnumMap<GHOST, MOVE> enumMap = new EnumMap<>(GHOST.class);
        for (Map.Entry<GHOST, MOVE> ghostMove : ghostMoves.entrySet()) {
            enumMap.put(ghostMove.getKey(), ghostMove.getValue());
        }
        copyOfGame.updateGhosts(enumMap);

        return copyOfGame;
    }

    private Set<MOVE> getPossibleGhostMoves(Game game, GHOST ghost)
    {
        return Sets.newHashSet(game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost),
                game.getGhostLastMoveMade(ghost)));
    }

    interface Tree
    {

    }

    private static class Node implements Tree
    {
        private Map<MOVE, Tree> branches;

        Node(Map<MOVE, Tree> branches)
        {
            this.branches = checkNotNull(branches);
        }
    }

    private static class GhostNode implements Tree
    {
        private Set<Tree> ghostBranches;

        GhostNode(Set<Tree> ghostBranches)
        {
            this.ghostBranches = checkNotNull(ghostBranches);
        }
    }

    private static class Leaf implements Tree
    {
        int heuristic;

        Leaf(int heuristic)
        {
            this.heuristic = checkNotNull(heuristic);
        }
    }


}