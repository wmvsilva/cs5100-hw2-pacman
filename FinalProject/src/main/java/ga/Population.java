package ga;

/**
 * Created by William on 8/15/2016.
 */
public class Population
{
    private Individual[] individuals;

    boolean isPacManPop;

    public Population(int populationSize, boolean initialise, boolean isPacManPopulation) {
        individuals = new Individual[populationSize];
        this.isPacManPop = isPacManPopulation;
        // Initialise population
        if (initialise) {
            // Loop and create individuals
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

    public boolean isPacManPop()
    {
        return isPacManPop;
    }

    /* Getters */
    public Individual getIndividual(int index) {
        return individuals[index];
    }

    public Individual getFittest(Population opposingPopulation) {
        Individual fittest = individuals[0];
        // Loop through individuals to find fittest
        for (int i = 0; i < size(); i++) {
            if (fittest.getFitness(opposingPopulation) <= getIndividual(i).getFitness(opposingPopulation)) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    /* Public methods */
    // Get population size
    public int size() {
        return individuals.length;
    }

    // Save individual
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }
}
