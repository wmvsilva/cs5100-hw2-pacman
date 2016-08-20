package minimax;

import pacman.game.Constants;
import pacman.game.Game;

import java.util.Queue;

public interface Heuristic
{
    int heuristicVal(Game game, Queue<Constants.MOVE> moveHistory);
}
