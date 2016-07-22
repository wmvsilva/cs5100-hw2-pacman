import entrants.pacman.silvaw.MyPacManDFS;
import entrants.pacman.silvaw.MyPacManMiniMax;
import pacman.Executor;
import pacman.controllers.examples.RandomGhosts;


/**
 * Created by pwillic on 06/05/2016.
 */
public class MainMiniMax
{
    public static void main(String[] args) {

        Executor executor = new Executor(false, true);
        executor.runGameTimed(new MyPacManMiniMax(), new RandomGhosts(), true);
    }
}
