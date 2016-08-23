# Final Project User Manual

## Evolutionizer

The Evolutionizer class is used to run the genetic algorithm. Following its completion, two files are saved: pacman_yyyy-mm-dd_hh-mm-ss.csv and ghost_yyyy-mm-dd_hh-mm-ss.csv. The pacman file contains the genes of the fittess Pac-Man individual from each generation in chronological order. The ghost file contains the genes of the fittess Ghost individual from each generation as well.

### Running Evolutionizer

To run the Evolutionizer class, simply open up FinalProject as a Maven project in your favorite IDE and then open the Evolutionizer.java class. To change the population size and the number of generations to run, one may change the static variables POPULATION_SIZE and GENERATION_COUNT respectively. To change the mutation rate and tournament size, one may navigate to genetic_algorithm.GeneticAlgorithm.java and change the static variables MUTATION_RATE and TOURNAMENT_SIZE respectively.

To actually run the class, simply run the Evolutionizer's main method. The console will print game scores each time the fitness of an individual is calculated and whenever a generation of Pac-Men or ghosts is completed.

The two files produced by running Evolutionizer can be used with MainMiniMax.java.

## MainMiniMax.java

To run the files produced by running Evolutionizer.java, one can run MainMiniMax.java. By specifying the filenames of the pacman and ghost files as well as the file line of the genes to use in each file, one can run different generations of Pac-Men and ghosts. MainMiniMax.java runs a visible Ms. Pac-Man game at normal speed.

### Running MainMiniMax

First, open MainMiniMax.java in your IDE. Just like with the Evolutionizer class, the FinalProject folder must be added as a Maven project. To change the file that contains the Pac-Man genes, change the static variable called PAC_MAN_FILE. Each set of genes is on a separate line. To change the genes, or line number used, for Pac-Man, change the static variable PAC_MAN_FILE_LINE. To change the file that contains the ghost genes, change the static variable called GHOST_FILE. To change the line number of the genes to use in the ghost file, change the static variable GHOST_FILE_LINE.

Finally, now that your variables are set, just run the main method of MainMiniMax.java and a window running Ms. Pac-Man will appear which has the Pac-Man competing against the ghosts, with both parties using the specified feature weights in the genes.