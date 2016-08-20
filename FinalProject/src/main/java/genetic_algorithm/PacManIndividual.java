package genetic_algorithm;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Represents a Pac-Man individual in a population which has genes and evolves based on the fitness score found in
 * this class's method
 */
class PacManIndividual extends Individual
{
    /**
     * @param opposingPopulation enemy population to select a random individual of to test against
     * @return the fitness (score) of this Pac-Man testing it against a random individual from the given ghost pop
     */
    @Override
    public int getPersonalFitness(Population opposingPopulation)
    {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, opposingPopulation.size());
        Map<String, Integer> randomGhostGenes = opposingPopulation.getIndividual(randomIndex).getGeneMap();

        return FitnessCalculator.getPacManFitness(this, randomGhostGenes);
    }
}
