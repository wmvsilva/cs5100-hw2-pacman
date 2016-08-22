package genetic_algorithm;

/**
 * A population of either Pac-Men or ghosts. In this population, it is possible to determine which of the individuals
 * are the fittest.
 */
public class Population
{
    /**
     * The individuals of this population
     */
    private Individual[] individuals;
    /**
     * Is this a population of Pac-Men?
     */
    private boolean isPacManPop;

    /**
     * @param populationSize number of individuals to create in this population
     * @param initialize should the individuals in this population be created when this is constructed
     * @param isPacManPopulation should this be constructing a population of Pac-Men? (or ghosts?)
     */
    public Population(int populationSize, boolean initialize, boolean isPacManPopulation)
    {
        individuals = new Individual[populationSize];
        this.isPacManPop = isPacManPopulation;
        if (initialize) {
            for (int i = 0; i < size(); i++) {
                Individual newIndividual;
                if (isPacManPopulation) {
                    newIndividual = new PacManIndividual();
                } else {
                    newIndividual = new GhostIndividual();
                }
                newIndividual.generateIndividual();
                individuals[i] = newIndividual;
            }
        }
    }

    /**
     * @return is this a population containing Pac-Men? (Or ghosts?)
     */
    boolean isPacManPop()
    {
        return isPacManPop;
    }

    /**
     * @param index the number of the individual to get in this population
     * @return the individual with the given index in this population
     */
    Individual getIndividual(int index)
    {
        return individuals[index];
    }

    /**
     * @param opposingPopulation the opposing population to test this population against to determine fitness
     * @return the fittest individual from this population
     */
    public Individual getFittest(Population opposingPopulation)
    {
        Individual fittest = individuals[0];
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness(opposingPopulation) <= getIndividual(i).getFitness(opposingPopulation)) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    /**
     * @return the number of individuals in this population
     */
    public int size()
    {
        return individuals.length;
    }

    /**
     * @param index the index to save this individual at
     * @param indiv the individual to put in this population
     */
    void saveIndividual(int index, Individual indiv)
    {
        individuals[index] = indiv;
    }
}
