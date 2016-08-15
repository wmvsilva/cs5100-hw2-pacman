package ga;

import entrants.pacman.silvaw.MyPacManMiniMax;
import pacman.Executor;
import pacman.controllers.examples.po.POCommGhosts;
import project.SettableHeuristic;

/**
 * Created by William on 8/15/2016.
 */
public class Evolutionizer
{
    public static void main(String[] args)
    {
        // Set a candidate solution
        // FitnessCalc.setSolution("1111000000000000000000000000000000000000000000000000000000001111");

        // Create an initial population
        Population myPop = new Population(10, true);

        // Evolve our population until we reach an optimum solution
        int generationCount = 0;
        while (generationCount < 45) {// (myPop.getFittest().getFitness() < FitnessCalc.getMaxFitness()) {
            generationCount++;
            System.out.println("Generation: " + generationCount + " Fittest: " + myPop.getFittest().getFitness());
            myPop = Algorithm.evolvePopulation(myPop);
        }
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myPop.getFittest());

        //Executor executor = new Executor(false, true);
        //executor.runGameTimed(new MyPacManMiniMax(new SettableHeuristic(myPop.getFittest().getGeneMap())),
        //        new POCommGhosts(50), true);
    }
}
