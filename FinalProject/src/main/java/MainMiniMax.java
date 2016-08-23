import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import examples.commGhosts.POCommGhosts;
import pacman.Executor;
import minimax.FileSettableHeuristic;

/**
 * Runs a visible Ms. Pac-Man game with some pre-set settings
 */
public class MainMiniMax
{
    private static final String PAC_MAN_FILE = "pacman_2016-32-16_11-32-14.csv";
    private static final int PAC_MAN_FILE_LINE = 1;

    private static final String GHOST_FILE = "ghost_2016-32-16_11-32-14.csv";
    private static final int GHOST_FILE_LINE = 1;

    /**
     * @param args ignored arguments
     */
    public static void main(String[] args)
    {
        Executor executor = new Executor(false, true);
        executor.runGameTimed(
                new MyPacManMiniMax(new FileSettableHeuristic(PAC_MAN_FILE, PAC_MAN_FILE_LINE)),
                new MyGhostsMiniMax(new FileSettableHeuristic(GHOST_FILE, GHOST_FILE_LINE)),
                true);
    }
}
