package genetic_algorithm;

import java.util.concurrent.ThreadLocalRandom;

import static genetic_algorithm.Individual.MAX_WEIGHT;
import static genetic_algorithm.Individual.MIN_WEIGHT;

/**
 * Static class which is used to evolve a population
 */
public class GeneticAlgorithm
{
    /**
     * Should we use elitism during evolution? (Should the most fit individual of a generation move on to the next
     * generation unchanged?)
     */
    private static final boolean ELITISM = true;
    /**
     * The size of the tournament to use in tournament selection
     */
    private static final int TOURNAMENT_SIZE = 5;
    /**
     * The chance that a given gene will mutate to a random value during evolution
     */
    private static final double MUTATION_RATE = 0.05;

    /**
     * @param pop population from which to select an individual
     * @param opposingPopulation opposing population (such as ghosts if given pop is PacMan) to use to determine fitness
     *                           of pop if necessary
     * @return an individual from pop selected using tournament selection
     */
    private static Individual tournamentSelection(Population pop, Population opposingPopulation) {
        Population tournament = new Population(TOURNAMENT_SIZE, false, pop.isPacManPop());
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int randomIndex = (int) (Math.random() * pop.size());
            tournament.saveIndiv(i, pop.getIndividual(randomIndex));
        }

        return tournament.getFittest(opposingPopulation);
    }

    /**
     * @param indiv1 the first parent to share genes
     * @param indiv2 the second parent to share genes
     * @param isPacManPopulation should a PacMan individual be created? (As opposed to a Ghost individual)
     * @return an individual created by crossing over the genes of the two given individuals
     */
    private static Individual crossover(Individual indiv1, Individual indiv2, boolean isPacManPopulation) {
        Individual newIndividual;
        if (isPacManPopulation) {
            newIndividual = new PacManIndividual();
        } else {
            newIndividual = new GhostIndividual();
        }

        for (int i = 0; i < indiv1.size(); i++) {
            if (Math.random() <= 0.5) {
                newIndividual.setGene(i, indiv1.getGene(i));
            } else {
                newIndividual.setGene(i, indiv2.getGene(i));
            }
        }
        return newIndividual;
    }

    /**
     * @param indiv individual to potentially mutate some of the genes of to random values in a range
     */
    private static void mutate(Individual indiv) {
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= MUTATION_RATE) {
                int gene = ThreadLocalRandom.current().nextInt(MIN_WEIGHT, MAX_WEIGHT + 1);
                indiv.setGene(i, gene);
            }
        }
    }

    /**
     * @param pop produces a pop like this pop but evolved to the next generation
     * @param opposingPopulation the enemy of the given pop that pop will be tested against to determine fitness
     * @return population like the given pop but evolved
     */
    public static Population evolvePopulation(Population pop, Population opposingPopulation) {
        Population newPopulation = new Population(pop.size(), false, pop.isPacManPop());

        // Elitism keeps the fittest individual from a generation and moves it to the next generation unchanged
        if (ELITISM) {
            Individual mostFit = pop.getFittest(opposingPopulation);
            // Fitness must be reset so it will be recalculated
            mostFit.resetFitness();
            newPopulation.saveIndiv(0, pop.getFittest(opposingPopulation));
        }

        int offset;
        if (ELITISM) {
            offset = 1;
        } else {
            offset = 0;
        }
        // Produce a new population through crossover
        for (int i = offset; i < pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop, opposingPopulation);
            Individual indiv2 = tournamentSelection(pop, opposingPopulation);
            Individual babyIndividual = crossover(indiv1, indiv2, pop.isPacManPop());
            newPopulation.saveIndiv(i, babyIndividual);
        }

        // Mutate some genes of the new population
        for (int i = offset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }
}
