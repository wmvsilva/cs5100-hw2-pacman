import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import pacman.controllers.examples.RandomGhosts;
import project.FileSettableHeuristic;
import project.PacManHeuristic;
import project.SettableHeuristic;

import java.io.IOException;


/**
 * Created by pwillic on 06/05/2016.
 */
public class MainMiniMax
{
    public static void main(String[] args) throws IOException
    {

        Executor executor = new Executor(false, true);
        executor.runGameTimed(new MyPacManMiniMax(new FileSettableHeuristic("pacman_2016-32-16_11-32-14.csv", 1)),
                new POCommGhosts(50), true);
    }
}
