package genetic_algorithm;

import minimax.WeightNames;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by William on 8/15/2016.
 */
public class Evolutionizer
{
    public static void main(String[] args) throws IOException
    {
        System.out.println("Begin");
        // Set a candidate solution
        // FitnessCalc.setSolution("1111000000000000000000000000000000000000000000000000000000001111");

        // Create an initial population
        Population myPacManPop = new Population(50, true, true);
        Population myGhostPop = new Population(50, true, false);

        // Evolve our population until we reach an optimum solution
        int generationCount = 0;
        List<Individual> fittestPacMenFromEachGeneration = new ArrayList<>();
        List<Individual> fittestGhostsFromEachGeneration = new ArrayList<>();

        while (generationCount < 50) {// (myPop.getFittest().getFitness() < FitnessCalc.getMaxFitness()) {
            generationCount++;
            // Evolve PacMen
            Individual fittestPacMan = myPacManPop.getFittest(myGhostPop);
            System.out.println("PacMan- Generation: " + generationCount + " Fittest: " + fittestPacMan.getFitness(myGhostPop));
            myPacManPop = Algorithm.evolvePopulation(myPacManPop, myGhostPop);
            // Evolve Ghosts
            Individual fittestGhost = myGhostPop.getFittest(myPacManPop);
            System.out.println("Ghosts- Generation: " + generationCount + " Fittest: " + fittestGhost.getFitness(myPacManPop));
            myGhostPop = Algorithm.evolvePopulation(myGhostPop, myPacManPop);

            fittestPacMenFromEachGeneration.add(fittestPacMan);
            fittestGhostsFromEachGeneration.add(fittestGhost);
        }
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myPacManPop.getFittest(myGhostPop));
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myGhostPop.getFittest(myPacManPop));

        // Print results to file
        saveFile("pacman", fittestPacMenFromEachGeneration);
        saveFile("ghosts", fittestGhostsFromEachGeneration);

        //System.out.println("Press enter to run game");
        //System.in.read();
        //Executor executor = new Executor(false, true);
        //executor.runGameTimed(
        //        new MyPacManMiniMax(new SettableHeuristic(myPacManPop.getFittest(myGhostPop).getGeneMap())),
        //        new MyGhostsMiniMax(new SettableHeuristic(myGhostPop.getFittest(myPacManPop).getGeneMap())), true);
    }

    private static void saveFile(String namePostfix, List<Individual> individuals) throws IOException
    {
        List<String> headerNames = WeightNames.getNames();
        String headerLine = "";

        List<String> fileLines = new ArrayList<>();
        for (String column : headerNames) {
            headerLine = headerLine + column + ",";
        }
        headerLine = headerLine.substring(0, headerLine.length() - 1);
        fileLines.add(headerLine);

        // Add csv
        for (Individual individual : individuals) {
            Map<String, Integer> genes = individual.getGeneMap();
            String fileLine = "";
            for (String column : headerNames) {
                fileLine += (genes.get(column) + ",");
            }
            fileLine = fileLine.substring(0, fileLine.length() - 1);
            fileLines.add(fileLine);
        }

        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd_hh-mm-ss");

        //to convert Date to String, use format method of SimpleDateFormat class.
        String strDate = dateFormat.format(date);

        Path file = Paths.get(namePostfix + "_" + strDate + ".csv");
        Files.write(file, fileLines, Charset.forName("UTF-8"));
    }
}
