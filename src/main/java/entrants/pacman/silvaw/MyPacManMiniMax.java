package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.sun.istack.internal.Nullable;
import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * PacMan controller which uses MiniMax search algorithm to determine the best path to go. The heuristic is created by
 * looking at the total pills left, the distance to the nearest pill, the score, the distance to the nearest ghost if
 * it is 20 nodes or fewer away, and the distance of an edible ghost if it is 40 nodes or fewer away.
 *
 * Time Complexity:
 * My code has the following steps.
 *
 * 1. Create MiniMax tree
 * 1a. For every other depth for PacMan, calculate game states for possible PacMan moves and create branches for each.
 *     Four game states are calculated. At a maximum, there could be 4 branches from this node.
 *
 * 1b. For every other depth for ghosts, calculate game states for possible ghost moves and create branches for each.
 *     At a maximum, there could be 3^4 branches. The game state may be calculated for each.
 *
 * 1c. For each leaf, calculate heuristic. This involves getting the score, total active pills, distance to nearest
 *     ghost, and determining the distance to the nearest pill. Getting the score and total active pills are constant
 *     time operations. Getting the distance to the nearest ghost takes 4 constant time operations since shortest path
 *     distances are pre-computed. Getting the distance to the nearest pill takes at most |P| constant time operations
 *     where P is the set of all pills.
 *
 *     The branching factor alternates at every other depth. The first branching factor is a maximum of 4 while the
 *     second is a maximum of 3^4.
 *     Depth 0 will have 1 node, 1 will have 1*4 nodes, 2 will have (1*4)*3^4 nodes, 3 will have (1*4*3^4)* 4 nodes and
 *     so on... The total number of nodes will be 1 + 4 + (3^4)*4 + 4*(3^4)*4 + (3^4)*4*(3^4)*4 + ...
 *
 *     If m is even, then we will have a maximum of ((3^4)^(m/2))*4^(m/2) leaves in the MiniMax tree.
 *     This is equal to (3^2)^m * 2^m = 18^m leaves at level m.
 *     If m is odd, then we will have a maximum of 4^(m/2 + 0.5)*(3^4)^(m/2 - 0.5) leaves in the MiniMax tree.
 *     This is equal to (4^(m/2)*4^0.5)*((3^4)^(m/2)*(3^4)^(-0.5)) = 2 * 2^m * 1/9 * (3^2)^m = (2/9)*18^m leaves at
 *     level m.
 *
 *     At each leaf, the heuristic is calculated which uses P operations as well as calculating game states for each
 *     leaf. For each of the nodes at a lower depth, new game states will need to be calculated which will require
 *     (1 + 4 + (3^4)*4 + 4*(3^4)*4 + ...) operations.
 *     At most, 3^4 game states will need to be calculated in the case of ghosts.
 *
 *     In the even case, there is a max of (3^4)P*18^m + (4)(2/9)18^(m-1) + (3^4)18^(m-2) + ... operations.
 *     The big O of this is O(P*18^m).
 *     In the odd case, there is a max of (4)P*(2/9)*18^m + (3^4)18^(m-1) + ... operations which gives us a big O of
 *     O(P*18^m).
 *     In both cases, the big O is the same.
 *
 *     The time complexity for this step is O(P*18^m) which is exponential.
 *
 * 2. Select best move using MiniMax tree that has (1 + 4 + (3^4)*4 + 4*(3^4)*4 + ...) nodes total.
 * 2a. If leaf, return a value.
 *     This is a simple constant time operation that must be completed 18^m times total.
 * 2b. If node, find max value among children.
 *     If m is even, nodes could have a max of (3^4)P*18^m, (4)(2/9)18^(m-1), (3^4)18^(m-2),... children.
 *     If m is odd, nodes could have a max of (4)(2/9)P*18^m, (3^4)18^(m-1), (4)(2/9)18^(m-2),... children.
 *     Finding the max of the children requires looking at all the children for
 *     ((3^4)*18^m + (4)(2/9)18^(m-1) + (3^4)18^(m-2) + ...) operations total in the even case or
 *     ((4)*(2/9)*18^m + (3^4)18^(m-1) + ...) operations total in the odd case.
 *
 *     Overall, this is a total of ((3^4)*18^m + (4)(2/9)18^(m-1) + (3^4)18^(m-2) + ...) = O(18^m) in the even case
 *     and ((4)*(2/9)*18^m + (3^4)18^(m-1) + ...) = O(18^m) in the odd case.
 *
 * The total time complexity is O(P(18)^m) + O(18^m)
 * = O(P(18)^m) where m is the depth of the MiniMax tree and P is the number of pills on a given maze. This is
 * exponential runtime. In practice, however, the branching factor will actually be much less as PacMan and the ghosts
 * are normally stuck in corridors which will greatly limit the number of moves available.
 *
 * =================
 *
 * Space Complexity:
 * My code has the following steps:
 * 1. Create MiniMax tree
 *    For each branch, the current game with previous moves by ghosts and PacMan is passed down. However, only one
 *    branch is created at a time and it goes down 1 depth each time until it reaching the leaf and creates all of the
 *    other nodes at that level. It is very similar to DFS. At each node, the game states on the nodes of the current
 *    search path must be maintained. Additionally, at each DFS node, possible ghost moves and pacman moves may be made.
 *    The game state and possible moves are no longer required after the DFS has returned from the search and will be
 *    garbage collected.
 *    Overall, for a maximum of m nodes, the game state and possible move set of ghosts or pacman must be maintained
 *    for G*3^4*c*m nodes where G is the size of the game, 3^4*C is the space contained by possible ghost moves, and
 *    m is the depth. Assuming constant game size, this gives us a O(m) space complexity. However, we end up with
 *    the tree in memory which has 18^m + (2/9)18^(m-1) + 18^(m-2) + ... total nodes giving us a complexity of O(18^m).
 *
 * 2. Select best move from MiniMax tree
 *    This also operates in a DFS-like fashion. All leaves will return a value. All nodes will compare the heuristic
 *    values produced by their children. At most, m MoveNumber objects will be required in memory. This is O(m) space
 *    complexity.
 *
 *    Overall, O(18^m) + O(m)
 *    = O(18^m)
 *
 *    The space complexity is O(18^m) where m is the depth of the MiniMax tree. This is exponential. However, it would
 *    be possible to change this program to generate the tree as the DFS searches meaning the entire tree would not
 *    have to be in memory. This would change the space complexity to O(m) which is linear.
 *
 */
public class MyPacManMiniMax extends PacmanController
{
    /**
     * The depth of the MiniMax tree to create
     */
    private static final int MINIMAX_DEPTH = 4;

    /**
     * @param game a copy of the current game
     * @param timeDue how long this turn has to complete
     * @return the best move according to a minimax search algorithm
     */
    public MOVE getMove(Game game, long timeDue)
    {
        Tree miniMaxTree = createMiniMaxTree(game, MINIMAX_DEPTH, true);
        return bestMoveFromTree(miniMaxTree);
    }

    /**
     * @param game copy of the current game
     * @param depth the depth of the tree to create
     * @param isPacMan are the next branches to create for PacMan? (Else, the ghosts)
     * @return a state-space search tree in which the leaves have been assigned values based on a heuristic. High
     * values represent PacMan winning while low values represent the ghosts winning.
     */
    private Tree createMiniMaxTree(Game game, int depth, boolean isPacMan)
    {
        // If there are no more branches to make or this is a terminal node
        if (depth == 0 || isEndGameState(game)) {
            return new Leaf(heuristicVal(game));
        }

        if (isPacMan) {
            // Create tree with branches for PacMan's moves at the top

            // Create branches depending on PacMan's possible moves
            Map<MOVE, Tree> branches = new HashMap<>();
            Game leftGame = stateAfterPacMove(MOVE.LEFT, game);
            branches.put(MOVE.LEFT, createMiniMaxTree(leftGame, depth - 1, false));
            Game rightGame = stateAfterPacMove(MOVE.RIGHT, game);
            branches.put(MOVE.RIGHT, createMiniMaxTree(rightGame, depth - 1, false));
            Game upGame = stateAfterPacMove(MOVE.UP, game);
            branches.put(MOVE.UP, createMiniMaxTree(upGame, depth - 1, false));
            Game downGame = stateAfterPacMove(MOVE.DOWN, game);
            branches.put(MOVE.DOWN, createMiniMaxTree(downGame, depth - 1, false));

            return new PacNode(branches);
        } else {
            // Create trees for possible ghost moves

            // Determine possible moves for each ghost
            Set<MOVE> possibleBlinkyMoves = getPossibleGhostMoves(game, GHOST.BLINKY);
            Set<MOVE> possibleInkyMoves = getPossibleGhostMoves(game, GHOST.INKY);
            Set<MOVE> possiblePinkyMoves = getPossibleGhostMoves(game, GHOST.PINKY);
            Set<MOVE> possibleSueMoves = getPossibleGhostMoves(game, GHOST.SUE);
            // Determine all possible combinations of ghost moves that can occur
            Set<Map<GHOST, MOVE>> possibleGhostCombinations = calculateGhostCombinations(possibleBlinkyMoves,
                    possibleInkyMoves,
                    possiblePinkyMoves,
                    possibleSueMoves);
            // For all possible move sets, calculate game state and create branch of tree
            Set<Tree> ghostBranches = new HashSet<>();
            for (Map<GHOST, MOVE> possibleGhostMoves : possibleGhostCombinations) {
                Game gameStateAfterGhosts = gameStateAfterGhosts(game, possibleGhostMoves);
                ghostBranches.add(createMiniMaxTree(gameStateAfterGhosts, depth - 1, true));
            }

            return new GhostNode(ghostBranches);
        }
    }

    /**
     * @param game a copy of the current game
     * @return is the game currently in an end game state?
     */
    private boolean isEndGameState(Game game)
    {
        return (game.getNumberOfActivePills() == 0 && game.getNumberOfActivePowerPills() == 0) ||
                game.wasPacManEaten() ||
                game.gameOver();
    }

    /**
     * @param game state of the game to determine heuristic of
     * @return a value determining how well the PacMan or the ghosts are doing. A larger value is better for PacMan
     * and a lower value is better for the ghosts
     */
    private int heuristicVal(Game game)
    {
        if (game.wasPacManEaten()) {
            // PacMan dying should have lowest possible value
            return Integer.MIN_VALUE;
        }

        int totalPills = game.getNumberOfActivePills() + game.getNumberOfActivePowerPills();
        int score = game.getScore();

        // Determine distance to nearest ghost
        int distanceToBlinky = shortestPathDistanceToGhost(game, GHOST.BLINKY);
        int distanceToInky = shortestPathDistanceToGhost(game, GHOST.INKY);
        int distanceToPinky = shortestPathDistanceToGhost(game, GHOST.PINKY);
        int distanceToSue = shortestPathDistanceToGhost(game, GHOST.SUE);
        Map<GHOST, Integer> ghostsToDistance = new HashMap<>();
        ghostsToDistance.put(GHOST.BLINKY, distanceToBlinky);
        ghostsToDistance.put(GHOST.INKY, distanceToInky);
        ghostsToDistance.put(GHOST.PINKY, distanceToPinky);
        ghostsToDistance.put(GHOST.SUE, distanceToSue);

        int distanceToNearestGhost = Collections.min(Lists.newArrayList(distanceToBlinky, distanceToInky,
                distanceToPinky, distanceToSue));
        GHOST nearestGhost = null;
        // Determine the nearest ghost
        for (Map.Entry<GHOST, Integer> ghostDistance : ghostsToDistance.entrySet()) {
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
    private int shortestPathDistanceToGhost(Game game, GHOST ghost)
    {
        return game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghost));
    }

    /**
     * @param pacMove the move which PacMan will take to create a new game state
     * @param curGame current game (which should not be modified in any way)
     * @return a copy of the current game except PacMan has taken the given move
     */
    private Game stateAfterPacMove(MOVE pacMove, Game curGame)
    {
        Game copyOfGame = curGame.copy();
        copyOfGame.updatePacMan(pacMove);
        return copyOfGame;
    }

    /**
     * @param possibleBlinkyMoves possible moves for Blinky ghost
     * @param possibleInkyMoves possible moves for Inky ghost
     * @param possiblePinkyMoves possible moves for Pinky ghost
     * @param possibleSueMoves possible moves for Sue ghost
     * @return a set containing a representation of all possible move combinations that the ghosts could make
     */
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

    /**
     * @param game current game (which should not be modified)
     * @param ghostMoves map of ghosts to the moves they should have performed in result
     * @return copy of the given game in which the ghosts have performed the given moves
     */
    private Game gameStateAfterGhosts(Game game, Map<GHOST, MOVE> ghostMoves)
    {
        Game copyOfGame = game.copy();
        // game's method updateGhosts takes in an EnumMap which we have to convert the given map to.
        EnumMap<GHOST, MOVE> enumMap = new EnumMap<>(GHOST.class);

        for (Map.Entry<GHOST, MOVE> ghostMove : ghostMoves.entrySet()) {
            enumMap.put(ghostMove.getKey(), ghostMove.getValue());
        }
        copyOfGame.updateGhosts(enumMap);

        return copyOfGame;
    }

    /**
     * @param game copy of a game
     * @param ghost one of the four ghosts
     * @return set of all possible moves that the given ghost can perform in the given game state
     */
    private Set<MOVE> getPossibleGhostMoves(Game game, GHOST ghost)
    {
        Set<MOVE> result = Sets.newHashSet(game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost),
                game.getGhostLastMoveMade(ghost)));

        if (result.isEmpty()) {
            // If ghosts can't perform any moves, they have to go in straight line (or the neutral move)
            return Sets.newHashSet(MOVE.NEUTRAL);
        }
        return result;
    }

    /**
     * @return using the MiniMax algorithm, produce the best move from the tree in which there is the lowest potential
     * loss for PacMan using the heuristics
     */
    private MOVE bestMoveFromTree(Tree miniMaxTree)
    {
        return bestMoveFromTreeHelper(miniMaxTree, true).move;
    }

    /**
     * Recursive helper function which bestMoveFromTree calls
     *
     * @param miniMaxTree minimax tree that can be either leaf or node
     * @param maximizingPlayer are we currently trying to maximize the hValue of the current depth?
     * @return a move number pair which describes the best move to perform and the heuristic value associated with that
     * number
     */
    private MoveNumber bestMoveFromTreeHelper(Tree miniMaxTree, boolean maximizingPlayer)
    {
        if (miniMaxTree.isLeaf()) {
            // There is no possible move for just a leaf so just leave as null
            return new MoveNumber(null, miniMaxTree.getHeuristic());
        } else if (maximizingPlayer) {
            // This should be a PacNode. Retrieve the children and moves and find the one with the largest heuristic val
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
            // This should be a ghost node. Retrieve the children and find the one with the lowest heuristic val
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

    /**
     * MiniMax tree representation which can be a leaf, PacNode, or GhostNode
     */
    interface Tree
    {
        /**
         * @return is this tree just a leaf?
         */
        boolean isLeaf();
        /**
         * @return if this is a leaf, provide the heuristic value for the game at that state
         */
        int getHeuristic();
        /**
         * @return if this is a PacNode, return all possible pacman moves and the trees associated with them
         */
        Map<MOVE, Tree> getChildrenAndMoves();

        /**
         * @return if this is a GhostNode, return all possible trees created with ghost move combinations
         */
        Set<Tree> getChildren();

    }

    /**
     * Node for a minimax tree containing possible PacMan moves (branches) mapped to the trees created by them
     */
    private static class PacNode implements Tree
    {
        /**
         * Possible pacman moves mapped to trees created by using these moves
         */
        private Map<MOVE, Tree> branches;

        PacNode(Map<MOVE, Tree> branches)
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

    /**
     * Node in a Minimax tree containing branches for possible ghost move combinations
     */
    private static class GhostNode implements Tree
    {
        /**
         * Set of all trees (Branches) for possible sets of ghosts moves
         */
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

    /**
     * The leaf of a minimax tree which holds the heuristic value for the game state if the moves before it were made
     */
    private static class Leaf implements Tree
    {
        /**
         * Heuristic value for the game state if the moves of the branches were followed
         */
        int heuristic;

        Leaf(int heuristic)
        {
            this.heuristic = checkNotNull(heuristic);
        }

        @Override
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

    /**
     * A move-number pair
     */
    private static class MoveNumber
    {
        /**
         * A move for PacMan to use
         */
        MOVE move;
        /**
         * Heuristic value
         */
        int hValue;

        MoveNumber(@Nullable MOVE move, int hValue)
        {
            this.move = move;
            this.hValue = checkNotNull(hValue);
        }

        public void setMove(MOVE move)
        {
            this.move = checkNotNull(move);
        }
    }
}