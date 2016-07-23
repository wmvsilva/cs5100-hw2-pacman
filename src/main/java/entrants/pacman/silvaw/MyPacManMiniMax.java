package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
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
        return bestMoveFromTree(miniMaxTree);
    }

    private MOVE bestMoveFromTree(Tree miniMaxTree)
    {
        return bestMoveFromTreeHelper(miniMaxTree, true).move;
    }

    private MoveNumber bestMoveFromTreeHelper(Tree miniMaxTree, boolean maximizingPlayer)
    {
        if (miniMaxTree.isLeaf()) {
            return new MoveNumber(null, miniMaxTree.getHeuristic());
        } else if (maximizingPlayer) {
            Optional<MoveNumber> moveNumberOptional = Optional.absent();
            for (Map.Entry<MOVE, Tree> entry : miniMaxTree.getChildrenAndMoves().entrySet()) {
                MoveNumber moveNumber = bestMoveFromTreeHelper(entry.getValue(), false);
                moveNumber.setMove(entry.getKey());
                if (!moveNumberOptional.isPresent()) {
                    moveNumberOptional = Optional.of(moveNumber);
                } else if (moveNumber.hValue > moveNumberOptional.get().hValue) {
                    moveNumberOptional = Optional.of(moveNumber);
                }
            }
            return moveNumberOptional.get();
        } else {
            Optional<MoveNumber> moveNumberOptional = Optional.absent();
            for (Tree tree : miniMaxTree.getChildren()) {
                MoveNumber moveNumber = bestMoveFromTreeHelper(tree, true);
                if (!moveNumberOptional.isPresent()) {
                    moveNumberOptional = Optional.of(moveNumber);
                } else if (moveNumber.hValue < moveNumberOptional.get().hValue) {
                    moveNumberOptional = Optional.of(moveNumber);
                }
            }
            return moveNumberOptional.get();
        }
    }

    private static class MoveNumber
    {
        MOVE move;
        int hValue;

        MoveNumber(MOVE move, int hValue)
        {
            this.move = move;
            this.hValue = hValue;
        }

        public void setMove(MOVE move)
        {
            this.move = move;
        }
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

        int ghostScore = -500 * (20 - distanceToNearestGhost);
        if (distanceToNearestGhost >= 20) {
            ghostScore = 0;
        }
        return -1 * totalPills + -1 * distanceToNearestPill + 100 * score + ghostScore;
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
        Set<MOVE> result = Sets.newHashSet(game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost),
                game.getGhostLastMoveMade(ghost)));
        if (result.isEmpty()) {
            return Sets.newHashSet(MOVE.NEUTRAL);
        }
        return result;
    }

    interface Tree
    {
        boolean isLeaf();
        int getHeuristic();
        Map<MOVE, Tree> getChildrenAndMoves();
        Set<Tree> getChildren();

    }

    private static class Node implements Tree
    {
        private Map<MOVE, Tree> branches;

        Node(Map<MOVE, Tree> branches)
        {
            this.branches = checkNotNull(branches);
        }

        @Override
        public boolean isLeaf()
        {
            return false;
        }

        @Override
        public int getHeuristic()
        {
            throw new RuntimeException();
        }

        @Override
        public Map<MOVE, Tree> getChildrenAndMoves()
        {
            return branches;
        }

        @Override
        public Set<Tree> getChildren()
        {
            throw new RuntimeException();
        }
    }

    private static class GhostNode implements Tree
    {
        private Set<Tree> ghostBranches;

        GhostNode(Set<Tree> ghostBranches)
        {
            this.ghostBranches = checkNotNull(ghostBranches);
        }

        @Override
        public boolean isLeaf()
        {
            return false;
        }

        @Override
        public int getHeuristic()
        {
            throw new RuntimeException();
        }

        @Override
        public Map<MOVE, Tree> getChildrenAndMoves()
        {
            throw new RuntimeException();
        }

        @Override
        public Set<Tree> getChildren()
        {
            return ghostBranches;
        }
    }

    private static class Leaf implements Tree
    {
        int heuristic;

        Leaf(int heuristic)
        {
            this.heuristic = checkNotNull(heuristic);
        }

        public boolean isLeaf()
        {
            return true;
        }

        @Override
        public int getHeuristic()
        {
            return heuristic;
        }

        @Override
        public Map<MOVE, Tree> getChildrenAndMoves()
        {
            throw new RuntimeException();
        }

        @Override
        public Set<Tree> getChildren()
        {
            throw new RuntimeException();
        }
    }


}