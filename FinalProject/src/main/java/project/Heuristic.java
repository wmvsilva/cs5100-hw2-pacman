package project;

import pacman.game.Constants;
import pacman.game.Game;

import java.util.Queue;

/**
 * Created by William on 8/15/2016.
 */
public interface Heuristic
{
    int heuristicVal(Game game, Queue<Constants.MOVE> moveHistory);
}
