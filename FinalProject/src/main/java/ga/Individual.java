package ga;

import com.google.common.base.Objects;
import project.WeightNames;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

abstract public class Individual
{
    public static final int MIN_WEIGHT = -10000;
    public static final int MAX_WEIGHT = 10000;
    private List<Pair> genes = generateEmptyGenes();
    // Cache
    private int fitness = 0;

    static List<Pair> generateEmptyGenes()
    {
        List<Pair> emptyGenes = new ArrayList<>();
        for (String geneName : WeightNames.getNames()) {
            emptyGenes.add(new Pair(geneName, 0));
        }
        return emptyGenes;
    }

    // Create a random individual
    public void generateIndividual() {
        genes.clear();
        for (String geneName : WeightNames.getNames()) {
            genes.add(new Pair(geneName, ThreadLocalRandom.current().nextInt(MIN_WEIGHT, MAX_WEIGHT + 1)));
        }
    }

    public int getGene(int index) {
        return genes.get(index).value;
    }

    public void setGene(int index, int value) {
        genes.set(index, new Pair(genes.get(index).name, value));
        fitness = 0;
    }

    /* Public methods */
    public int size() {
        return genes.size();
    }

    abstract public int getPersonalFitness(Population population);

    public int getFitness(Population population) {
        if (fitness == 0) {
            fitness = getPersonalFitness(population);
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

        @Override
        public String toString()
        {
            return name + "=" + value;
        }
    }
}