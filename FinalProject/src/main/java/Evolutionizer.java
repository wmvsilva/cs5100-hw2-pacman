import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import genetic_algorithm.GeneticAlgorithm;
import genetic_algorithm.Individual;
import genetic_algorithm.Population;
import minimax.FeatureWeightNames;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Uses a genetic algorithm to simultaneously evolve two populations of Pac-Men and ghosts to create genes for the
 * controllers of each which are successful at the game. The genes of the fittest ghost and fittest Pac-Man from
 * each generation are saved to a file.
 */
public class Evolutionizer
{
    /**
     * The size of the pacman population as well as the ghost population which evolves for some number of generations
     */
    private static final int POPULATION_SIZE = 50;
    /**
     * The number of generations to create before stopping the program
     */
    private static final int GENERATION_COUNT = 50;

    /**
     * Runs a population of Pac-Man and a population of ghosts for a set number of generations with the Pac-Men and
     * ghosts evolving to beat the other through the use of a genetic algorithm. After the generations have been
     * completed, the genes of the fittest Pac-Men and ghosts from each generation are saved to files. These genes
     * can be used to replay the games.
     *
     * @param ignored ignored arguments
     */
    public static void main(String[] ignored)
    {
        System.out.println("Starting...");
        // Create populations for Pac-Men and the ghosts
        Population myPacManPop = new Population(POPULATION_SIZE, true, true);
        Population myGhostPop = new Population(POPULATION_SIZE, true, false);
        // Lists to keep track of the fittest Pac-Men and ghosts from each generation.
        List<Individual> fittestPacMenFromEachGeneration = Lists.newArrayList();
        List<Individual> fittestGhostsFromEachGeneration = Lists.newArrayList();

        // For each generation, Pac-Man and ghost compete and evolve
        for (int generationCount = 0; generationCount < GENERATION_COUNT; generationCount++) {
            // Evolve PacMen
            Individual fittestPacMan = myPacManPop.getFittest(myGhostPop);
            System.out.println("PacMan- Generation: " + generationCount + " Fittest: " + fittestPacMan.getFitness(myGhostPop));
            myPacManPop = GeneticAlgorithm.evolvePopulation(myPacManPop, myGhostPop);

            // Evolve Ghosts
            Individual fittestGhost = myGhostPop.getFittest(myPacManPop);
            System.out.println("Ghosts- Generation: " + generationCount + " Fittest: " + fittestGhost.getFitness(myPacManPop));
            myGhostPop = GeneticAlgorithm.evolvePopulation(myGhostPop, myPacManPop);

            // Add the fittest Pac-Man and ghosts to the lists
            fittestPacMenFromEachGeneration.add(fittestPacMan);
            fittestGhostsFromEachGeneration.add(fittestGhost);
        }
        System.out.println("PacMan- Generation: " + GENERATION_COUNT + "\n Genes:\n" + myPacManPop.getFittest(myGhostPop));
        System.out.println("Ghosts- Generation: " + GENERATION_COUNT + "\n Genes:\n" + myGhostPop.getFittest(myGhostPop));

        // Print results (fittest Pac-Men and fittest ghosts) to file
        saveFile("pacman", fittestPacMenFromEachGeneration);
        saveFile("ghosts", fittestGhostsFromEachGeneration);
    }

    /**
     * Saves the gene data of the given individuals to a CSV file called [namePrefix]_DATE.csv
     *
     * @param namePrefix the prefix of the filename of the file to be created
     * @param individuals the individuals to save the gene data of
     */
    private static void saveFile(String namePrefix, List<Individual> individuals)
    {
        // Get the names of all genes
        List<String> headerNames = FeatureWeightNames.getNames();

        String headerLine = "";
        for (String column : headerNames) {
            headerLine = headerLine + column + ",";
        }
        headerLine = headerLine.substring(0, headerLine.length() - 1);

        List<String> fileLines = Lists.newArrayList(headerLine);

        // Each individual's genes are a line of the file
        for (Individual individual : individuals) {
            Map<String, Integer> genes = individual.getGeneMap();
            String fileLine = "";
            for (String column : headerNames) {
                fileLine += (genes.get(column) + ",");
            }
            fileLine = fileLine.substring(0, fileLine.length() - 1);
            fileLines.add(fileLine);
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
        String strDate = dateFormat.format(new Date());

        // Save file
        Path file = Paths.get(namePrefix + "_" + strDate + ".csv");
        try {
            Files.write(file, fileLines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }
}
