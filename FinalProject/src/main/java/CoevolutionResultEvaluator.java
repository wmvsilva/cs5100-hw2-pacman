import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import minimax.FileSettableHeuristic;
import minimax.SettableHeuristic;
import pacman.Executor;

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
import java.util.List;
import java.util.Map;

public class CoevolutionResultEvaluator
{
    public static void main(String[] args)
    {
        String pacManFile = "pacman_2016-36-22_11-36-13.csv";
        String ghostFile = "ghosts_2016-36-22_11-36-13.csv";

        // Read file
        Path pacManFilePath = Paths.get(pacManFile);
        List<String> pacManFileLines = null;
        try {
            pacManFileLines = Files.readAllLines(pacManFilePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        // Read file
        Path ghostFilePath = Paths.get(ghostFile);
        List<String> ghostFileLines = null;
        try {
            ghostFileLines = Files.readAllLines(ghostFilePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        String header = pacManFileLines.get(0);
        List<String> columns = Arrays.asList(header.split(","));

        List<Integer> scores = Lists.newArrayList();
        for (int i = 1; i < pacManFileLines.size(); i++) {
            Map<String, Integer> pacManGenes = FileSettableHeuristic.fileLineToGeneMap(columns, pacManFileLines.get(i));
            Map<String, Integer> ghostGenes = FileSettableHeuristic.fileLineToGeneMap(columns, ghostFileLines.get(i));

            Executor executor = new Executor(false, true);
            int score = executor.runGame(
                    new MyPacManMiniMax(new SettableHeuristic(pacManGenes)),
                    new MyGhostsMiniMax(new SettableHeuristic(ghostGenes)), false, 0);
            scores.add(score);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");
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
