package ga;

import entrants.ghosts.silvaw.MyGhostsMiniMax;
import entrants.pacman.silvaw.MyPacManMiniMax;
import pacman.Executor;
import pacman.controllers.examples.po.POCommGhosts;
import project.PacManHeuristic;
import project.SettableHeuristic;

import java.util.Map;

/**
 * Created by William on 8/15/2016.
 */
public class FitnessCalc
{

    // Calculate inidividuals fittness by comparing it to our candidate solution
    static int getPacManFitness(Individual individual, Map<String, Integer> ghostGenes) {
        Map<String, Integer> geneMap = individual.getGeneMap();
        Executor executor = new Executor(false, true);
        return executor.runGame(
                new MyPacManMiniMax(new SettableHeuristic(geneMap)),
                new MyGhostsMiniMax(new SettableHeuristic(ghostGenes)), false, 0);
    }

    // Calculate inidividuals fittness by comparing it to our candidate solution
    static int getGhostFitness(Individual individual, Map<String, Integer> pacManGenes) {
        Map<String, Integer> geneMap = individual.getGeneMap();
        Executor executor = new Executor(false, true);

        return 0 - executor.runGame(
                new MyPacManMiniMax(new SettableHeuristic(pacManGenes)),
                new MyGhostsMiniMax(new SettableHeuristic(geneMap)), false, 0);
    }
}