import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import minimax.FileSettableHeuristic;
import minimax.SettableHeuristic;
import pacman.Executor;
import pacman.controllers.Controller;
import pacman.game.Constants;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Determines how the score between two co-evolving populations of Pac-Men and ghosts change over generations. Using
 * the files obtained from running {@link Evolutionizer}, for each generation some number of games are run using the
 * genes from Pac-Man and ghosts and the average score is recorded. After all generations have been iterated through,
 * a CSV file is created with the first column being the generation number and the second column being the average
 * score.
 */
public class CoevolutionResultEvaluator
{
    private static final String PAC_MAN_FILE = "pacman_sample_100generations.csv";
    private static final String GHOST_FILE = "ghosts_sample_100generations.csv";
    /**
     * The number of games to run per generation (and then get the average score of)
     */
    private static final int NUM_OF_GAMES_TO_RUN_PER_GENERATION = 10;

    /**
     * Runs each generation of Pac-Men and ghosts from the specified files. The populations of each generation compete
     * against one another a set amount and the average score is recorded. After all generations have been run, the
     * results are recorded to a file named score_yyyy-MM-dd_hh-mm-ss.csv
     *
     * @param args ignored args
     */
    public static void main(String[] args)
    {
        // Read files into memory
        List<String> pacManFileLines = readFile(PAC_MAN_FILE);
        List<String> ghostFileLines = readFile(GHOST_FILE);

        // Determine the ordering of the features
        String header = pacManFileLines.get(0);
        List<String> columns = Arrays.asList(header.split(","));

        // For each generation, run some number of games and record the average score.
        List<Integer> scores = Lists.newArrayList();
        for (int i = 1; i < pacManFileLines.size(); i++) {
            Map<String, Integer> pacManGenes = FileSettableHeuristic.fileLineToGeneMap(columns, pacManFileLines.get(i));
            Map<String, Integer> ghostGenes = FileSettableHeuristic.fileLineToGeneMap(columns, ghostFileLines.get(i));

            int score = (int) determineAverageScore(
                    new MyPacManMiniMax(new SettableHeuristic(pacManGenes)),
                    new MyGhostsMiniMax(new SettableHeuristic(ghostGenes)));
            System.out.println("Generation-" + i + " AVG SCORE: " + score);
            scores.add(score);
        }

        saveScoresToFile(scores);
    }

    /**
     * @param filename the name of the file to read
     * @return the contents of the file as a list of strings
     */
    static List<String> readFile(String filename)
    {
        Path filePath = Paths.get(filename);

        List<String> fileLines = null;
        try {
            fileLines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return fileLines;
    }

    /**
     * @param pacManController pacman controller to run games with
     * @param ghostController ghost controller to run games with
     * @return runs some number of Ms. Pac-Man games using the given controllers and returns the average score
     */
    static double determineAverageScore(Controller<Constants.MOVE> pacManController,
                                        Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController)
    {
        double totalScore = 0;
        for (int i = 0; i < NUM_OF_GAMES_TO_RUN_PER_GENERATION; i++) {
            Executor executor = new Executor(false, true);
            double score = (double) executor.runGame(pacManController, ghostController, false, 0);
            totalScore += score;
        }
        return totalScore / 10.0;
    }

    /**
     * @param scores a list of integer scores with the index representing the generation number
     *
     * Saves the scores as a CSV named score_yyyy-MM-dd_hh-mm-ss.csv with the first column being the generation number
     *               and the second column being the average score
     */
    private static void saveScoresToFile(List<Integer> scores)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String strDate = dateFormat.format(new Date());

        List<String> scoreStrings = Lists.newArrayList();
        for (int i = 0; i < scores.size(); i++) {
            scoreStrings.add(i + "," + scores.get(i));
        }

        // Save file
        Path file = Paths.get("score_" + strDate + ".csv");
        try {
            Files.write(file, scoreStrings, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }
}
