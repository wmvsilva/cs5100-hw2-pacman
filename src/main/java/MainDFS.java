import entrants.pacman.silvaw.MyPacManDFS;
import pacman.Executor;
import pacman.controllers.examples.RandomGhosts;


/**
 * Created by pwillic on 06/05/2016.
 */
public class MainDFS
{
    public static void main(String[] args) {

        Executor executor = new Executor(true, true);

        executor.runGameTimed(new MyPacManDFS(), new RandomGhosts(), true);
    }
}
