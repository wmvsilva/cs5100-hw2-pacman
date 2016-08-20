package genetic_algorithm;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * An individual of a population that represents a Ghost controller.
 */
class GhostIndividual extends Individual
{
    /**
     * @param opposingPopulation population of PacMan
     * @return retrieves a random Pac-Man from the opposing population and runs a game against it with no graphics
     * and at the fastest speed. The negative score of that game is the fitness of this ghost
     */
    @Override
    public int getPersonalFitness(Population opposingPopulation)
    {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, opposingPopulation.size());
        Map<String, Integer> randomPacManGenes = opposingPopulation.getIndividual(randomIndex).getGeneMap();

        return FitnessCalculator.getGhostFitness(this, randomPacManGenes);
    }
}
