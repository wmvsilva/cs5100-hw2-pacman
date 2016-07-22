package entrants.pacman.silvaw;

import com.google.common.collect.Sets;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
        return bestMoveFromTree(miniMaxTree);
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
                Game gameStateAfterGhosts = gameStateAfterGhosts(possibleGhostMoves);
                ghostBranches.add(createMiniMaxTree(gameStateAfterGhosts, depth - 1, true));
            }

            return new GhostNode(ghostBranches);
        }
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
    }


}