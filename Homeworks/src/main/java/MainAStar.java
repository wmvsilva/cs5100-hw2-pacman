import entrants.pacman.silvaw.MyPacManAStar;
import pacman.Executor;
import pacman.controllers.examples.RandomGhosts;


/**
 * Created by pwillic on 06/05/2016.
 */
public class MainAStar
{
    public static void main(String[] args) {

        Executor executor = new Executor(true, true);

        executor.runGameTimed(new MyPacManAStar(), new RandomGhosts(), true);
    }
}
