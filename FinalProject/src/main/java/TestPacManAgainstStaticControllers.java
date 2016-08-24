import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import entrants.pacman.silvaw.MyPacManMiniMax;
import minimax.FileSettableHeuristic;
import minimax.SettableHeuristic;
import pacman.controllers.Controller;
import pacman.controllers.examples.AggressiveGhosts;
import pacman.controllers.examples.Legacy;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.controllers.examples.RandomGhosts;
import pacman.controllers.examples.StarterGhosts;
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
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TestPacManAgainstStaticControllers
{
    private static final String PAC_MAN_FILE = "pacman_sample_100generations.csv";

    public static void main(String[] ignored)
    {
        // Read files into memory
        List<String> pacManFileLines = CoevolutionResultEvaluator.readFile(PAC_MAN_FILE);

        // Get static controllers to test against
        List<Controller<EnumMap<Constants.GHOST, Constants.MOVE>>> ghostControllers = Lists.newArrayList();
        ghostControllers.add(new AggressiveGhosts());
        ghostControllers.add(new Legacy());
        ghostControllers.add(new Legacy2TheReckoning());
        ghostControllers.add(new RandomGhosts());
        ghostControllers.add(new StarterGhosts());

        String newFileHeader = "Generation,Aggressive,Legacy,Legacy2,Random,Starter";

        // Determine the ordering of the features
        String header = pacManFileLines.get(0);
        List<String> columns = Arrays.asList(header.split(","));

        // For each generation, run some number of games and record the average score.
        List<List<Integer>> scores = Lists.newArrayList();
        for (int i = 1; i < pacManFileLines.size(); i++) {
            Map<String, Integer> pacManGenes = FileSettableHeuristic.fileLineToGeneMap(columns, pacManFileLines.get(i));

            List<Integer> generationResults = Lists.newArrayList();
            int j = 0;
            for (Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController : ghostControllers) {
                String ghostControllerName = newFileHeader.split(",")[j + 1];

                int score = (int) CoevolutionResultEvaluator.determineAverageScore(
                        new MyPacManMiniMax(new SettableHeuristic(pacManGenes)),
                        ghostController);
                System.out.println("Generation-" + i + "-" + ghostControllerName + "-" + " AVG SCORE: " + score);
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
        Path file = Paths.get("evolved_pacman_vs_static_ghosts_" + strDate + ".csv");
        try {
            Files.write(file, scoreStrings, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }
}
