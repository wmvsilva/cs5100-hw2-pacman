package project;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.sun.istack.internal.Nullable;
import pacman.game.Constants.MOVE;
import pacman.game.Constants.GHOST;
import pacman.game.Game;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class MiniMax
{
    private Queue<MOVE> moveHistory;
    private Heuristic heuristicFunction;

    public MiniMax(Heuristic heuristic)
    {
        this.heuristicFunction = checkNotNull(heuristic);
        this.moveHistory = new LinkedList<>();
    }

    public MoveNumber createMiniMaxTreeAndGetBestMove(Game game, int depth, boolean isPacMan,
                                                      Optional<Integer> alpha, Optional<Integer> beta)
    {
        Queue<MOVE> copyOfMoveHistory = new LinkedList<>(moveHistory);
        MoveNumber moveNumber = createMiniMaxTreeAndGetBestMoveHelper(game, depth, isPacMan, alpha, beta,
                copyOfMoveHistory);
        if (isPacMan && moveNumber.move != null) {
            moveHistory.add(moveNumber.move);
        }
        if (moveHistory.size() > 20) {
            moveHistory.remove();
        }
        return moveNumber;
    }

    /**
     * @param game copy of the current game
     * @param depth the depth of the tree to create
     * @param isPacMan are the next branches to create for PacMan? (Else, the ghosts)
     * @return a state-space search tree in which the leaves have been assigned values based on a heuristic. High
     * values represent PacMan winning while low values represent the ghosts winning.
     */
    public MoveNumber createMiniMaxTreeAndGetBestMoveHelper(Game game, int depth, boolean isPacMan,
                                                      Optional<Integer> alpha, Optional<Integer> beta,
                                                            Queue<MOVE> moveHistoryCopy)
    {
        Optional<Integer> newAlpha = alpha;
        Optional<Integer> newBeta = beta;

        // If there are no more branches to make or this is a terminal node
        if (depth == 0 || isEndGameState(game)) {
            return new MoveNumber(null, heuristicFunction.heuristicVal(game, moveHistoryCopy));
        } else if (isPacMan) {
            Optional<MoveNumber> val = Optional.absent();
            // Create tree with branches for PacMan's moves at the top

            List<MOVE> possiblePacManMoves =
                    Arrays.asList(game.getPossibleMoves(game.getPacmanCurrentNodeIndex()));

            for (MOVE move : possiblePacManMoves) {
                Game nextGameState = stateAfterPacMove(move, game);
                Queue<MOVE> copyOfMoveHistory = new LinkedList<>(moveHistoryCopy);
                copyOfMoveHistory.add(move);
                MoveNumber moveNumber = createMiniMaxTreeAndGetBestMoveHelper(nextGameState, depth - 1, false,
                        newAlpha, newBeta, copyOfMoveHistory);
                moveNumber.setMove(move);
                if (!val.isPresent() || moveNumber.hValue > val.get().hValue) {
                    val = Optional.of(moveNumber);
                }
                if (!newAlpha.isPresent() || val.get().hValue > newAlpha.get()) {
                    newAlpha = Optional.of(val.get().hValue);
                }
                if (newBeta.isPresent() && newBeta.get() <= newAlpha.get()) {
                    break;
                }
            }

            return val.get();
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

            Optional<MoveNumber> val = Optional.absent();
            for (Map<GHOST, MOVE> possibleGhostMoves : possibleGhostCombinations) {
                Game gameStateAfterGhosts = gameStateAfterGhosts(game, possibleGhostMoves);
                MoveNumber moveNumber = createMiniMaxTreeAndGetBestMoveHelper(gameStateAfterGhosts, depth - 1, true,
                        newAlpha, newBeta, moveHistoryCopy);
                moveNumber.setGhostMoves(possibleGhostMoves);
                if (!val.isPresent() || moveNumber.hValue < val.get().hValue) {
                    val = Optional.of(moveNumber);
                }
                if (!newBeta.isPresent() || moveNumber.hValue < newBeta.get()) {
                    newBeta = Optional.of(moveNumber.hValue);
                }
                if (newAlpha.isPresent() && newBeta.get() <= newAlpha.get()) {
                    break;
                }
            }

            return val.get();
        }
    }

    /**
     * @param game a copy of the current game
     * @return is the game currently in an end game state?
     */
    private static boolean isEndGameState(Game game)
    {
        return (game.getNumberOfActivePills() == 0 && game.getNumberOfActivePowerPills() == 0) ||
                game.wasPacManEaten() ||
                game.gameOver();
    }

    /**
     * @param pacMove the move which PacMan will take to create a new game state
     * @param curGame current game (which should not be modified in any way)
     * @return a copy of the current game except PacMan has taken the given move
     */
    private static Game stateAfterPacMove(MOVE pacMove, Game curGame)
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
    private static Set<Map<GHOST, MOVE>> calculateGhostCombinations(Set<MOVE> possibleBlinkyMoves,
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
    private static Game gameStateAfterGhosts(Game game, Map<GHOST, MOVE> ghostMoves)
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
    public static Set<MOVE> getPossibleGhostMoves(Game game, GHOST ghost)
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
     * A move-number pair
     */
    public static class MoveNumber
    {
        /**
         * A move for PacMan to use
         */
        public MOVE move;
        public Map<GHOST, MOVE> ghostMoves;
        /**
         * Heuristic value
         */
        public int hValue;

        MoveNumber(@Nullable MOVE move, int hValue)
        {
            this.move = move;
            this.hValue = checkNotNull(hValue);
        }

        public void setMove(MOVE move)
        {
            this.move = checkNotNull(move);
        }

        public void setGhostMoves(Map<GHOST, MOVE> ghostMoves)
        {
            this.ghostMoves = checkNotNull(ghostMoves);
        }
    }
}
