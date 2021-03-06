package entrants.ghosts.silvaw;

import com.google.common.base.Optional;
import pacman.controllers.Controller;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import minimax.Heuristic;
import minimax.MinimaxAlgorithm;

import java.util.EnumMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A controller for ghosts which uses Minimax to pick a move at every step. The evaluation function for the Minimax
 * algorithm is set in the constructor of this class.
 */
public class MyGhostsMiniMax extends Controller<EnumMap<GHOST, MOVE>>
{
    /**
     * Th depth of the tree created by Minimax
     */
    private static final int MINIMAX_DEPTH = 6;

    /**
     * Minimax implementation to pick next move
     */
    private MinimaxAlgorithm minimaxAlgorithm;

    /**
     * @param heuristic evaluation function to use in Minimax algorithm when picking move
     */
    public MyGhostsMiniMax(Heuristic heuristic)
    {
        checkNotNull(heuristic);
        this.minimaxAlgorithm = new MinimaxAlgorithm(heuristic);
    }

    /**
     * @param game a copy of the current game
     * @param timeDue how long this turn has to complete
     * @return the best move according to a minimax search algorithm
     */
    @Override
    public EnumMap<GHOST, MOVE> getMove(Game game, long timeDue)
    {
        MinimaxAlgorithm.MoveNumber moveNumber = minimaxAlgorithm.createMiniMaxTreeAndGetBestMove(game, MINIMAX_DEPTH, false,
                Optional.<Integer>absent(), Optional.<Integer>absent());
        Map<GHOST, MOVE> ghostMoves = moveNumber.ghostMoves;

        // Depth was 0 which means no moves were returned
        if (ghostMoves == null) {
            return null;
        }

        // Convert Map<GHOST, MOVE> to necessary format of EnumMap<GHOST, MOVE>
        EnumMap<GHOST, MOVE> enumMap = new EnumMap<>(GHOST.class);
        for (Map.Entry<GHOST, MOVE> ghostMove : ghostMoves.entrySet()) {
            enumMap.put(ghostMove.getKey(), ghostMove.getValue());
        }
        return enumMap;
    }
}
