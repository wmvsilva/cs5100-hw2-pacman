package project;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.sun.istack.internal.Nullable;
import entrants.pacman.silvaw.MyPacManMiniMax;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by William on 8/15/2016.
 */
public class MiniMax
{

    /**
     * @param game copy of the current game
     * @param depth the depth of the tree to create
     * @param isPacMan are the next branches to create for PacMan? (Else, the ghosts)
     * @return a state-space search tree in which the leaves have been assigned values based on a heuristic. High
     * values represent PacMan winning while low values represent the ghosts winning.
     */
    public static MoveNumber createMiniMaxTreeAndGetBestMove(Game game, int depth, boolean isPacMan, Optional<Integer> alpha, Optional<Integer> beta)
    {
        // If there are no more branches to make or this is a terminal node
        if (depth == 0 || isEndGameState(game)) {
            return new MoveNumber(null, heuristicVal(game));
        }

        if (isPacMan) {
            Optional<MoveNumber> val = Optional.absent();
            // Create tree with branches for PacMan's moves at the top

            Set<Constants.MOVE> possiblePacManMoves =
                    new HashSet<>(Arrays.asList(game.getPossibleMoves(game.getPacmanCurrentNodeIndex())));

            for (Constants.MOVE move : possiblePacManMoves) {
                Game nextGameState = stateAfterPacMove(move, game);
                MoveNumber moveNumber = createMiniMaxTreeAndGetBestMove(nextGameState, depth - 1, false, alpha, beta);
                moveNumber.setMove(move);
                if (!val.isPresent() || moveNumber.hValue > val.get().hValue) {
                    val = Optional.of(moveNumber);
                }
                if (!alpha.isPresent() || val.get().hValue > alpha.get()) {
                    alpha = Optional.of(val.get().hValue);
                }
                if (beta.isPresent() && beta.get() <= alpha.get()) {
                    break;
                }
            }

            return val.get();
        } else {
            // Create trees for possible ghost moves

            // Determine possible moves for each ghost
            Set<Constants.MOVE> possibleBlinkyMoves = getPossibleGhostMoves(game, Constants.GHOST.BLINKY);
            Set<Constants.MOVE> possibleInkyMoves = getPossibleGhostMoves(game, Constants.GHOST.INKY);
            Set<Constants.MOVE> possiblePinkyMoves = getPossibleGhostMoves(game, Constants.GHOST.PINKY);
            Set<Constants.MOVE> possibleSueMoves = getPossibleGhostMoves(game, Constants.GHOST.SUE);
            // Determine all possible combinations of ghost moves that can occur
            Set<Map<Constants.GHOST, Constants.MOVE>> possibleGhostCombinations = calculateGhostCombinations(possibleBlinkyMoves,
                    possibleInkyMoves,
                    possiblePinkyMoves,
                    possibleSueMoves);

            Optional<MoveNumber> val = Optional.absent();
            for (Map<Constants.GHOST, Constants.MOVE> possibleGhostMoves : possibleGhostCombinations) {
                Game gameStateAfterGhosts = gameStateAfterGhosts(game, possibleGhostMoves);
                MoveNumber moveNumber = createMiniMaxTreeAndGetBestMove(gameStateAfterGhosts, depth - 1, true, alpha, beta);
                moveNumber.setGhostMoves(possibleGhostMoves);
                if (!val.isPresent() || moveNumber.hValue < val.get().hValue) {
                    val = Optional.of(moveNumber);
                }
                if (!beta.isPresent() || moveNumber.hValue < beta.get()) {
                    beta = Optional.of(moveNumber.hValue);
                }
                if (alpha.isPresent() && beta.get() <= alpha.get()) {
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
     * @param game state of the game to determine heuristic of
     * @return a value determining how well the PacMan or the ghosts are doing. A larger value is better for PacMan
     * and a lower value is better for the ghosts
     */
    private static int heuristicVal(Game game)
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

    /**
     * @param pacMove the move which PacMan will take to create a new game state
     * @param curGame current game (which should not be modified in any way)
     * @return a copy of the current game except PacMan has taken the given move
     */
    private static Game stateAfterPacMove(Constants.MOVE pacMove, Game curGame)
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
    private static Set<Map<Constants.GHOST, Constants.MOVE>> calculateGhostCombinations(Set<Constants.MOVE> possibleBlinkyMoves,
                                                                                 Set<Constants.MOVE> possibleInkyMoves,
                                                                                 Set<Constants.MOVE> possiblePinkyMoves,
                                                                                 Set<Constants.MOVE> possibleSueMoves)
    {
        Set<Map<Constants.GHOST, Constants.MOVE>> result = new HashSet<>();

        for (Constants.MOVE blinkyMove : possibleBlinkyMoves) {
            for (Constants.MOVE inkyMove : possibleInkyMoves) {
                for (Constants.MOVE pinkyMove : possiblePinkyMoves) {
                    for (Constants.MOVE sueMove : possibleSueMoves) {

                        Map<Constants.GHOST, Constants.MOVE> possibleMoveSet = new HashMap<>();
                        possibleMoveSet.put(Constants.GHOST.BLINKY, blinkyMove);
                        possibleMoveSet.put(Constants.GHOST.INKY, inkyMove);
                        possibleMoveSet.put(Constants.GHOST.PINKY, pinkyMove);
                        possibleMoveSet.put(Constants.GHOST.SUE, sueMove);

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
    private static Game gameStateAfterGhosts(Game game, Map<Constants.GHOST, Constants.MOVE> ghostMoves)
    {
        Game copyOfGame = game.copy();
        // game's method updateGhosts takes in an EnumMap which we have to convert the given map to.
        EnumMap<Constants.GHOST, Constants.MOVE> enumMap = new EnumMap<>(Constants.GHOST.class);

        for (Map.Entry<Constants.GHOST, Constants.MOVE> ghostMove : ghostMoves.entrySet()) {
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
    private static Set<Constants.MOVE> getPossibleGhostMoves(Game game, Constants.GHOST ghost)
    {
        Set<Constants.MOVE> result = Sets.newHashSet(game.getPossibleMoves(game.getGhostCurrentNodeIndex(ghost),
                game.getGhostLastMoveMade(ghost)));

        if (result.isEmpty()) {
            // If ghosts can't perform any moves, they have to go in straight line (or the neutral move)
            return Sets.newHashSet(Constants.MOVE.NEUTRAL);
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
        public Constants.MOVE move;
        public Map<Constants.GHOST, Constants.MOVE> ghostMoves;
        /**
         * Heuristic value
         */
        int hValue;

        MoveNumber(@Nullable Constants.MOVE move, int hValue)
        {
            this.move = move;
            this.hValue = checkNotNull(hValue);
        }

        public void setMove(Constants.MOVE move)
        {
            this.move = checkNotNull(move);
        }

        public void setGhostMoves(Map<Constants.GHOST, Constants.MOVE> ghostMoves)
        {
            this.ghostMoves = checkNotNull(ghostMoves);
        }
    }
}
