import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import pacman.Executor;
import minimax.FileSettableHeuristic;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Runs a visible Ms. Pac-Man game with some pre-set settings
 */
public class MainMinimax
{
    private static final String PAC_MAN_FILE = "pacman_sample_100generations.csv";
    private static final int PAC_MAN_FILE_LINE = 2;

    private static final String GHOST_FILE = "ghosts_sample_100generations.csv";
    private static final int GHOST_FILE_LINE = 2;

    /**
     * @param args ignored arguments
     */
    public static void main(String[] args)
    {
        checkArgument(PAC_MAN_FILE_LINE != 1, "Cannot use 0 as file line (header line of file)");
        checkArgument(GHOST_FILE_LINE != 1, "Cannot use 0 as file line (header line of file)");

        Executor executor = new Executor(false, true);
        executor.runGameTimed(
                new MyPacManMiniMax(new FileSettableHeuristic(PAC_MAN_FILE, PAC_MAN_FILE_LINE)),
                new MyGhostsMiniMax(new FileSettableHeuristic(GHOST_FILE, GHOST_FILE_LINE)),
                true);
    }
}
