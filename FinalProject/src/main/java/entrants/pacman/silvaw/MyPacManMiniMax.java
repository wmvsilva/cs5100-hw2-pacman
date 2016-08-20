package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import project.Heuristic;
import project.MiniMax;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A controller for pacman which uses Minimax to pick a move at every step. The evaluation function for the Minimax
 * algorithm is set in the constructor of this class.
 */
public class MyPacManMiniMax extends PacmanController
{
    /**
     * The depth of the MiniMax tree to create
     */
    private static final int MINIMAX_DEPTH = 6;

    /**
     * Minimax implementation to pick next move
     */
    private MiniMax miniMax;

    /**
     * @param heuristic evaluation function to use in Minimax algorithm when picking move
     */
    public MyPacManMiniMax(Heuristic heuristic)
    {
        checkNotNull(heuristic);
        this.miniMax = new MiniMax(heuristic);
    }

    /**
     * @param game a copy of the current game
     * @param timeDue how long this turn has to complete
     * @return the best move according to a minimax search algorithm
     */
    public MOVE getMove(Game game, long timeDue)
    {
        return miniMax.createMiniMaxTreeAndGetBestMove(game, MINIMAX_DEPTH, true,
                Optional.<Integer>absent(), Optional.<Integer>absent()).move;
    }
}