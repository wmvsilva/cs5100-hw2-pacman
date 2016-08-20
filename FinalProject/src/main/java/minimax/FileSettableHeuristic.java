package minimax;

import pacman.game.Constants;
import pacman.game.Game;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class FileSettableHeuristic implements Heuristic
{
    private SettableHeuristic settableHeuristic;

    public FileSettableHeuristic(String filename, int fileLine) throws IOException
    {
        Path file = Paths.get(filename);
        List<String> fileLines = Files.readAllLines(file, StandardCharsets.UTF_8);
        String header = fileLines.get(0);
        List<String> columns = Arrays.asList(header.split(","));

        String currentLine = fileLines.get(fileLine + 1);
        List<String> columnValues = Arrays.asList(currentLine.split(","));

        Map<String, Integer> geneMap = new HashMap<>();
        for (int i = 0; i < columns.size(); i++) {
            geneMap.put(columns.get(i), Integer.valueOf(columnValues.get(i)));
        }
        System.out.println("GeneMap: " + geneMap.toString());
        System.out.println(currentLine);
        this.settableHeuristic = new SettableHeuristic(geneMap);
    }

    @Override
    public int heuristicVal(Game game, Queue<Constants.MOVE> moveHistory)
    {
        return settableHeuristic.heuristicVal(game, moveHistory);
    }
}
