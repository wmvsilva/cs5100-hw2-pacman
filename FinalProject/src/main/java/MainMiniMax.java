import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import pacman.controllers.examples.RandomGhosts;
import project.PacManHeuristic;


/**
 * Created by pwillic on 06/05/2016.
 */
public class MainMiniMax
{
    public static void main(String[] args) {

        Executor executor = new Executor(false, true);
        executor.runGameTimed(new MyPacManMiniMax(new PacManHeuristic()), new MyGhostsMiniMax(new PacManHeuristic()), true);
    }
}
