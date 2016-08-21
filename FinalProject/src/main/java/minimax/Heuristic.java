package minimax;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.Queue;

/**
 * Evaluation function to be provided to Minimax which takes the current state of the game and identifies how well
 * a current player is doing with higher numbers being good and lower numbers being better for the opposing player
 */
public interface Heuristic
{
    /**
     * @param game the game to evaluate
     * @param pacManMoveHistory the history of moves performed by PacMan in the game
     * @return value which is higher if maximizing player is winning and lower if maximizing player is losing
     */
    int heuristicVal(Game game, Queue<MOVE> pacManMoveHistory);
}
