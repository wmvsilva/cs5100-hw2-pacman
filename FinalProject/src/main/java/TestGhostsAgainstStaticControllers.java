import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import entrants.ghosts.silvaw.MyGhostsMiniMax;
import minimax.FileSettableHeuristic;
import minimax.SettableHeuristic;
import pacman.controllers.Controller;
import pacman.controllers.examples.NearestPillPacMan;
import pacman.controllers.examples.RandomNonRevPacMan;
import pacman.controllers.examples.RandomPacMan;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Constants;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class TestGhostsAgainstStaticControllers
{
    private static final String GHOST_FILE = "ghosts_sample_100generations.csv";

    public static void main(String[] ignored)
    {
        // Read files into memory
        List<String> ghostFilesLines = CoevolutionResultEvaluator.readFile(GHOST_FILE);

        // Get static controllers to test against
        List<Controller<Constants.MOVE>> pacManControllers = Lists.newArrayList();
        pacManControllers.add(new NearestPillPacMan());
        pacManControllers.add(new RandomPacMan());
        pacManControllers.add(new RandomNonRevPacMan());
        pacManControllers.add(new StarterPacMan());

        String newFileHeader = "Generation,Nearest Pill,Random,Random NonRev,Starter Pacman";

        // Determine the ordering of the features
        String header = ghostFilesLines.get(0);
        List<String> columns = Arrays.asList(header.split(","));

        // For each generation, run some number of games and record the average score.
        List<List<Integer>> scores = Lists.newArrayList();
        for (int i = 1; i < ghostFilesLines.size(); i++) {
            Map<String, Integer> ghostGenes = FileSettableHeuristic.fileLineToGeneMap(columns, ghostFilesLines.get(i));

            List<Integer> generationResults = Lists.newArrayList();
            int j = 0;
            for (Controller<Constants.MOVE> pacManController : pacManControllers) {
                String pacManControllerName = newFileHeader.split(",")[j + 1];

                int score = (int) CoevolutionResultEvaluator.determineAverageScore(
                        pacManController,
                        new MyGhostsMiniMax(new SettableHeuristic(ghostGenes)));
                System.out.println("Generation-" + i + "-" + pacManControllerName + "-" + " AVG SCORE: " + score);
                generationResults.add(score);
                j++;
            }
            scores.add(generationResults);
        }

        saveScoresToFile(newFileHeader, scores);
    }

    private static void saveScoresToFile(String headerLine, List<List<Integer>> scores)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String strDate = dateFormat.format(new Date());

        List<String> scoreStrings = Lists.newArrayList();
        scoreStrings.add(headerLine);

        for (int i = 0; i < scores.size(); i++) {
            String line = "";
            for (Integer score : scores.get(i)) {
                line += score + ",";
            }
            line = line.substring(0, line.length() - 1);
            scoreStrings.add(i + "," + line);
        }

        // Save file
        Path file = Paths.get("evolved_ghosts_vs_static_pacman_" + strDate + ".csv");
        try {
            Files.write(file, scoreStrings, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }
}
