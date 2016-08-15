package ga;

import project.WeightNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Individual
{
    private List<Pair> genes = new ArrayList<>();
    // Cache
    private int fitness = 0;

    // Create a random individual
    public void generateIndividual() {
        for (String geneName : WeightNames.getNames()) {
            genes.add(new Pair(geneName, ThreadLocalRandom.current().nextInt(-500, 500 + 1)));
        }
    }

    public int getGene(int index) {
        return genes.get(index).value;
    }

    public void setGene(int index, int value) {
        genes.get(index).value = value;
        fitness = 0;
    }

    /* Public methods */
    public int size() {
        return genes.size();
    }

    public int getFitness() {
        if (fitness == 0) {
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
    }

    public Map<String, Integer> getGeneMap()
    {
        Map<String, Integer> geneMap = new HashMap<>();
        for (Pair pair : genes) {
            geneMap.put(pair.name, pair.value);
        }

        return geneMap;
    }

    @Override
    public String toString() {
        return genes.toString();
    }

    private static class Pair
    {
        String name;
        int value;

        Pair(String name, int value)
        {
            this.name = name;
            this.value = value;
        }
    }
}