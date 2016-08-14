package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.sun.istack.internal.Nullable;
import pacman.controllers.PacmanController;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

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
        return createMiniMaxTreeAndGetBestMove(game, MINIMAX_DEPTH, true).move;
    }

    /**
     * @param game copy of the current game
     * @param depth the depth of the tree to create
     * @param isPacMan are the next branches to create for PacMan? (Else, the ghosts)
     * @return a state-space search tree in which the leaves have been assigned values based on a heuristic. High
     * values represent PacMan winning while low values represent the ghosts winning.
     */
    private MoveNumber createMiniMaxTreeAndGetBestMove(Game game, int depth, boolean isPacMan)
    {
        // If there are no more branches to make or this is a terminal node
        if (depth == 0 || isEndGameState(game)) {
            return new MoveNumber(null, heuristicVal(game));
        }

        if (isPacMan) {
            // Create tree with branches for PacMan's moves at the top

            // Create branches depending on PacMan's possible moves
            Map<MOVE, MoveNumber> branches = new HashMap<>();


            Game leftGame = stateAfterPacMove(MOVE.LEFT, game);
            branches.put(MOVE.LEFT, createMiniMaxTreeAndGetBestMove(leftGame, depth - 1, false));

            Game rightGame = stateAfterPacMove(MOVE.RIGHT, game);
            branches.put(MOVE.RIGHT, createMiniMaxTreeAndGetBestMove(rightGame, depth - 1, false));

            Game upGame = stateAfterPacMove(MOVE.UP, game);
            branches.put(MOVE.UP, createMiniMaxTreeAndGetBestMove(upGame, depth - 1, false));

            Game downGame = stateAfterPacMove(MOVE.DOWN, game);
            branches.put(MOVE.DOWN, createMiniMaxTreeAndGetBestMove(downGame, depth - 1, false));

            MoveNumber maximizing = null;
            for (Map.Entry<MOVE, MoveNumber> entry : branches.entrySet()) {
                if (maximizing == null || entry.getValue().hValue > maximizing.hValue) {
                    maximizing = entry.getValue();
                    maximizing.setMove(entry.getKey());
                }
            }
            return maximizing;
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
            Set<MoveNumber> ghostBranches = new HashSet<>();
            for (Map<GHOST, MOVE> possibleGhostMoves : possibleGhostCombinations) {
                Game gameStateAfterGhosts = gameStateAfterGhosts(game, possibleGhostMoves);
                ghostBranches.add(createMiniMaxTreeAndGetBestMove(gameStateAfterGhosts, depth - 1, true));
            }

            MoveNumber minimizing = null;
            for (MoveNumber moveNumber : ghostBranches) {
                if (minimizing == null || moveNumber.hValue < minimizing.hValue) {
                    minimizing = moveNumber;
                }
            }
            return minimizing;
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