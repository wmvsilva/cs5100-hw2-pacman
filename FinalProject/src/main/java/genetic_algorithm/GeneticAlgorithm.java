package genetic_algorithm;

import java.util.concurrent.ThreadLocalRandom;

import static genetic_algorithm.Individual.MAX_WEIGHT;
import static genetic_algorithm.Individual.MIN_WEIGHT;

class GeneticAlgorithm
{

    private static final double UNIFORM_RATE = 0.5;
    private static final double MUTATION_RATE = 0.015;
    private static final int TOURNAMENT_SIZE = 5;
    private static final boolean ELITISM = true;

    static Population evolvePopulation(Population pop, Population opposingPopulation) {
        Population newPopulation = new Population(pop.size(), false, pop.isPacManPop());

        if (ELITISM) {
            Individual elite = pop.getFittest(opposingPopulation);
            elite.resetFitness();
            newPopulation.saveIndividual(0, pop.getFittest(opposingPopulation));
        }

        int elitismOffset;
        if (ELITISM) {
            elitismOffset = 1;
        } else {
            elitismOffset = 0;
        }
        for (int i = elitismOffset; i < pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop, opposingPopulation);
            Individual indiv2 = tournamentSelection(pop, opposingPopulation);
            Individual newIndiv = crossover(indiv1, indiv2, pop.isPacManPop());
            newPopulation.saveIndividual(i, newIndiv);
        }

        for (int i = elitismOffset; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }

        return newPopulation;
    }

    private static Individual crossover(Individual indiv1, Individual indiv2, boolean isPacManPopulation) {
        Individual newSol;
        if (isPacManPopulation) {
            newSol = new PacManIndividual();
        } else {
            newSol = new GhostIndividual();
        }
        for (int i = 0; i < indiv1.size(); i++) {
            if (Math.random() <= UNIFORM_RATE) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }

    private static void mutate(Individual indiv) {
        for (int i = 0; i < indiv.size(); i++) {
            if (Math.random() <= MUTATION_RATE) {
                int gene = ThreadLocalRandom.current().nextInt(MIN_WEIGHT, MAX_WEIGHT + 1);
                indiv.setGene(i, gene);
            }
        }
    }

    private static Individual tournamentSelection(Population pop, Population opposingPopulation) {
        Population tournament = new Population(TOURNAMENT_SIZE, false, pop.isPacManPop());
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        return tournament.getFittest(opposingPopulation);
    }
}
