package ga;

import entrants.ghosts.silvaw.MyGhostsMiniMax;
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
        Population myPacManPop = new Population(10, true, true);
        Population myGhostPop = new Population(10, true, false);

        // Evolve our population until we reach an optimum solution
        int generationCount = 0;
        while (generationCount < 15) {// (myPop.getFittest().getFitness() < FitnessCalc.getMaxFitness()) {
            generationCount++;
            // Evolve PacMen
            System.out.println("PacMan- Generation: " + generationCount + " Fittest: " + myPacManPop.getFittest(myGhostPop).getFitness(myGhostPop));
            myPacManPop = Algorithm.evolvePopulation(myPacManPop, myGhostPop);
            // Evolve Ghosts
            System.out.println("Ghosts- Generation: " + generationCount + " Fittest: " + myGhostPop.getFittest(myPacManPop).getFitness(myPacManPop));
            myGhostPop = Algorithm.evolvePopulation(myGhostPop, myPacManPop);
        }
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myPacManPop.getFittest(myGhostPop));
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myGhostPop.getFittest(myPacManPop));

        Executor executor = new Executor(false, true);
        executor.runGameTimed(
                new MyPacManMiniMax(new SettableHeuristic(myPacManPop.getFittest(myGhostPop).getGeneMap())),
                new MyGhostsMiniMax(new SettableHeuristic(myGhostPop.getFittest(myPacManPop).getGeneMap())), true);
    }
}
