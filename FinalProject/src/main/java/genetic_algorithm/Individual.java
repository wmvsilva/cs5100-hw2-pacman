package genetic_algorithm;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import minimax.FeatureWeightNames;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Individual
{
    /**
     * The minimum value that a gene can be
     */
    static final int MIN_WEIGHT = -500;
    /**
     * The maximum value that a gene can be
     */
    static final int MAX_WEIGHT = 500;
    /**
     * The genes of this individual consisting of pairs containing the name of the gene and the value of the gene.
     * It is a list rather than the map to make iteration in other classes easier.
     * The genes represent the feature weights of features in an evaluation function used in Minimax.
     */
    private List<Pair> genes = generateEmptyGenes();
    /**
     * The fitness of this individual. It is cached here after it is calculated.
     */
    private Optional<Integer> fitness = Optional.absent();

    /**
     * @param opposingPopulation enemy population to select a random individual of to test against
     * @return the fitness of this individual by testing it against the opposing population
     */
    abstract public int getPersonalFitness(Population opposingPopulation);

    /**
     * @return genes in which all values are set to 0
     */
    private static List<Pair> generateEmptyGenes()
    {
        List<Pair> emptyGenes = Lists.newArrayList();
        for (String geneName : FeatureWeightNames.getNames()) {
            emptyGenes.add(new Pair(geneName, 0));
        }
        return emptyGenes;
    }

    /**
     * Set this individual to have random gene values in the allowed range
     */
    void generateIndividual()
    {
        // Clear the existing genes first
        genes.clear();
        for (String geneName : FeatureWeightNames.getNames()) {
            genes.add(new Pair(geneName, ThreadLocalRandom.current().nextInt(MIN_WEIGHT, MAX_WEIGHT + 1)));
        }
    }

    /**
     * @param index get the gene at this index
     * @return return the value of the gene at the given index
     */
    int getGene(int index)
    {
        return genes.get(index).value;
    }

    /**
     * @param index index of the gene to set the value of
     * @param value the value to set the gene at the given index to
     */
    void setGene(int index, int value)
    {
        genes.set(index, new Pair(genes.get(index).name, value));
        // Genes have changed so fitness needs to be recalculated
        fitness = Optional.absent();
    }

    /**
     * @return number of genes that this individual has
     */
    public int size()
    {
        return genes.size();
    }

    /**
     * @param opposingPopulation opposing population to test this individual against
     * @return fitness of this individual (with higher numbers being more fit) by testing it against opposing population
     */
    public int getFitness(Population opposingPopulation)
    {
        if (!fitness.isPresent()) {
            fitness = Optional.of(getPersonalFitness(opposingPopulation));
        }
        return fitness.get();
    }

    /**
     * @return the genes of this individual in map form
     */
    public Map<String, Integer> getGeneMap()
    {
        Map<String, Integer> geneMap = Maps.newHashMap();
        for (Pair pair : genes) {
            geneMap.put(pair.name, pair.value);
        }

        return geneMap;
    }

    /**
     * Reset or uncache the fitness so it will be recalculated when the next time the fitness needs to be used
     */
    void resetFitness()
    {
        fitness = Optional.absent();
    }

    @Override
    public String toString()
    {
        return genes.toString();
    }

    /**
     * Pair representing a key and value
     */
    private static class Pair
    {
        /**
         * Key
         */
        private String name;
        /**
         * Value
         */
        private int value;

        /**
         * @param name the key of this pair
         * @param value the value of this pair
         */
        Pair(String name, int value)
        {
            this.name = checkNotNull(name);
            this.value = checkNotNull(value);
        }

        @Override
        public String toString()
        {
            return name + "=" + value;
        }
    }
}