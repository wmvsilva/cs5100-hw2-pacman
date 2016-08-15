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
import project.Heuristic;
import project.MiniMax;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

public class MyPacManMiniMax extends PacmanController
{
    /**
     * The depth of the MiniMax tree to create
     */
    private static final int MINIMAX_DEPTH = 4;

    private MiniMax miniMax;

    public MyPacManMiniMax(Heuristic heuristic)
    {
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