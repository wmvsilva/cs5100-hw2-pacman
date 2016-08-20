package genetic_algorithm;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by William on 8/15/2016.
 */
public class PacManIndividual extends Individual
{
    @Override
    public int getPersonalFitness(Population population)
    {
        int randomIndex = ThreadLocalRandom.current().nextInt(0, population.size());
        Map<String, Integer> randomGhostGenes = population.getIndividual(randomIndex).getGeneMap();
        return FitnessCalc.getPacManFitness(this, randomGhostGenes);
    }
}
