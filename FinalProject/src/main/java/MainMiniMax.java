import entrants.pacman.silvaw.MyPacManMiniMax;
import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import minimax.FileSettableHeuristic;

import java.io.IOException;

/**
 * Runs a visible Ms. Pac-Man game with some pre-set settings
 */
public class MainMiniMax
{
    /**
     * @param args ignored arguments
     */
    public static void main(String[] args)
    {
        Executor executor = new Executor(false, true);
        executor.runGameTimed(new MyPacManMiniMax(new FileSettableHeuristic("pacman_2016-32-16_11-32-14.csv", 1)),
                new POCommGhosts(50), true);
    }
}
