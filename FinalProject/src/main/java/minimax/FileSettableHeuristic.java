package minimax;

import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import pacman.game.Constants;
import pacman.game.Game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper for {@link SettableHeuristic} which takes in a file containing feature weights and uses that information
 * to set the feature weights of the settable heuristic
 */
public class FileSettableHeuristic implements Heuristic
{
    /**
     * This class wraps this class and constructs it using feature weights from a file
     */
    private SettableHeuristic settableHeuristic;

    /**
     * @param filename file to get feature weights from
     * @param fileLine the line of filename to get the feature weights from
     */
    public FileSettableHeuristic(String filename, int fileLine)
    {
        checkNotNull(filename);

        // Read file
        Path file = Paths.get(filename);
        List<String> fileLines = null;
        try {
            fileLines = Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        String header = fileLines.get(0);
        List<String> columns = Arrays.asList(header.split(","));

        // Take the line we want to look at and split it into the column values
        String currentLine = fileLines.get(fileLine + 1);
        Map<String, Integer> geneMap = fileLineToGeneMap(columns, currentLine);

        System.out.println("GeneMap: " + geneMap.toString());
        System.out.println(currentLine);
        this.settableHeuristic = new SettableHeuristic(geneMap);
    }

    /**
     * @param columns the names of all the features
     * @param currentLine a comma-separated list of integers with each integer representing the weight of a feature
     *                    in the given columns list
     * @return a map of feature names to weights for those features
     */
    public static Map<String, Integer> fileLineToGeneMap(List<String> columns, String currentLine)
    {
        List<String> columnValues = Arrays.asList(currentLine.split(","));

        // Translate the values into the needed format for SettableHeuristic
        Map<String, Integer> geneMap = Maps.newHashMap();
        for (int i = 0; i < columns.size(); i++) {
            geneMap.put(columns.get(i), Integer.valueOf(columnValues.get(i)));
        }

        return geneMap;
    }

    @Override
    public int heuristicVal(Game game, Queue<Constants.MOVE> pacManMoveHistory)
    {
        return settableHeuristic.heuristicVal(game, pacManMoveHistory);
    }
}
