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

public class CoevolutionResultEvaluator
{
    public static void main(String[] args)
    {
        String pacManFile = "pacman_2016-44-22_11-44-17.csv";
        String ghostFile = "ghosts_2016-44-22_11-44-17.csv";

        List<String> pacManFileLines = readFile(pacManFile);
        List<String> ghostFileLines = readFile(ghostFile);

        String header = pacManFileLines.get(0);
        List<String> columns = Arrays.asList(header.split(","));

        List<Integer> scores = Lists.newArrayList();
        for (int i = 1; i < pacManFileLines.size(); i++) {
            Map<String, Integer> pacManGenes = FileSettableHeuristic.fileLineToGeneMap(columns, pacManFileLines.get(i));
            Map<String, Integer> ghostGenes = FileSettableHeuristic.fileLineToGeneMap(columns, ghostFileLines.get(i));

            int score = (int) determineAverageScore(
                    new MyPacManMiniMax(new SettableHeuristic(pacManGenes)),
                    new MyGhostsMiniMax(new SettableHeuristic(ghostGenes)));
            System.out.println(i + "- " + score);
            scores.add(score);
        }

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

    private static List<String> readFile(String filename)
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

    private static double determineAverageScore(Controller<Constants.MOVE> pacManController,
                                                Controller<EnumMap<Constants.GHOST, Constants.MOVE>> ghostController)
    {
        double totalScore = 0;
        for (int i = 0; i < 10; i++) {
            Executor executor = new Executor(false, true);
            double score = (double) executor.runGame(pacManController, ghostController, false, 0);
            totalScore += score;
        }
        return totalScore / 10.0;
    }
}
