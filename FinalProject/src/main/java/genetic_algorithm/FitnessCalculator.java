package genetic_algorithm;

import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import pacman.Executor;
import minimax.SettableHeuristic;

import java.util.Map;

/**
 * Contains methods used to determine the fitness of individuals in a population
 */
class FitnessCalculator
{
    /**
     * @param individual PacManIndividual to determine fitness of
     * @param ghostGenes the genes of the ghost to test the PacMan against
     * @return the fitness (score) of PacMan when run against the given ghost
     */
    static int getPacManFitness(Individual individual, Map<String, Integer> ghostGenes)
    {
        Map<String, Integer> geneMap = individual.getGeneMap();

        // Run the game at full speed with visuals off to get the score
        Executor executor = new Executor(false, true);
        return executor.runGame(
                new MyPacManMiniMax(new SettableHeuristic(geneMap)),
                new MyGhostsMiniMax(new SettableHeuristic(ghostGenes)), false, 0);
    }

    /**
     * @param individual GhostIndividual to determine fitness of
     * @param pacManGenes the genes of the PacMan to test the given Ghosts against
     * @return the fitness (negative score) of the Ghosts when run again the given PacMan
     */
    static int getGhostFitness(Individual individual, Map<String, Integer> pacManGenes)
    {
        Map<String, Integer> geneMap = individual.getGeneMap();

        Executor executor = new Executor(false, true);
        return 0 - executor.runGame(
                new MyPacManMiniMax(new SettableHeuristic(pacManGenes)),
                new MyGhostsMiniMax(new SettableHeuristic(geneMap)), false, 0);
    }
}