# Final Project User Manual

## Evolutionizer

The `Evolutionizer` class is used to run the genetic algorithm. Following its completion, two files are saved: `pacman_yyyy-mm-dd_hh-mm-ss.csv` and `ghost_yyyy-mm-dd_hh-mm-ss.csv`. The pacman file contains the genes of the fittest Pac-Man individual from each generation in chronological order. The ghost file contains the genes of the fittest Ghost individual from each generation as well.

### Running Evolutionizer

To run the `Evolutionizer` class, simply open up FinalProject as a Maven project in your favorite IDE and then open the `Evolutionizer.java` class. To change the population size and the number of generations to run, one may change the static variables `POPULATION_SIZE` and `GENERATION_COUNT` respectively. To change the mutation rate and tournament size, one may navigate to `genetic_algorithm.GeneticAlgorithm.java` and change the static variables `MUTATION_RATE` and `TOURNAMENT_SIZE` respectively.

To actually run the class, simply run the `Evolutionizer`'s `main` method. The console will print game scores each time the fitness of an individual is calculated and whenever a generation of Pac-Men or ghosts is completed.

The two files produced by running `Evolutionizer` can be used with `MainMiniMax.java`.

## MainMinimax.java

To run the files produced by running `Evolutionizer.java`, one can run `MainMinimax.java`. By specifying the filenames of the pacman and ghost files as well as the file line of the genes to use in each file, one can run different generations of Pac-Men and ghosts. `MainMinimax.java` runs a visible Ms. Pac-Man game at normal speed.

### Running MainMinimax

First, open `MainMinimax.java` in your IDE. Just like with the `Evolutionizer` class, the FinalProject folder must be added as a Maven project. To change the file that contains the Pac-Man genes, change the static variable called `PAC_MAN_FILE`. Each set of genes is on a separate line. To change the genes, or line number used, for Pac-Man, change the static variable `PAC_MAN_FILE_LINE`. To change the file that contains the ghost genes, change the static variable called `GHOST_FILE`. To change the line number of the genes to use in the ghost file, change the static variable `GHOST_FILE_LINE`.

Finally, now that your variables are set, just run the main method of `MainMinimax.java` and a window running Ms. Pac-Man will appear which has the Pac-Man competing against the ghosts, with both parties using the specified feature weights in the genes.

## CoevolutionResultEvaluator.java

One may want to see how the scores between the evolving Pac-Men and ghosts change over time. To do this, one may use the files produced by `Evolutionizer.java` and provide them to this class which will take the Pac-Man population and ghost population of each generation and run them against one another several times. The average score is recorded and after all generations have been processed, a CSV file named `score_yyyy-mm-dd_hh-mm-ss.csv` is saved which contains the generation number in the first column and the average score in the second column. This CSV file can be used for later analysis.

### Running CoevolutionResultEvaluator

First, open up `CoevolutionResultEvaluator.java` in your IDE. One should change the static variable `PAC_MAN_FILE` to the Pac-Man file produced by running the `Evolutionizer` that they'd like to use. Next, you should change the static variable `GHOST_FILE` to the ghost file produced by running the `Evolutionizer`. Note that you should not be mixing together files from different runs of `Evolutionizer`. For example, running a Pac-Man file with 10 generations and a ghost file with 100 generations in this class will cause an error.

After you have set the variables, simply run the `main` method. The console will print the generation and the average score as each generation completes. After the main method has completed, a file called `score_yyyy-mm-dd_hh-mm-ss.csv` will be saved in the FinalProject folder.

## TestPacManAgainstStaticControllers.java

You may want to test the Pac-Man of each generation produced by `Evolutionizer` against some static ghost controllers to see the improvements over time. Using the `TestPacManAgainstStaticControllers` class allows you to take a file produced by `Evolutionizer` and run it against the Aggressive, Legacy, Legacy2, Random, and Starter ghosts provided by the Ms. Pac-Man AI library. Each generation of Pac-Man competes against each ghost controller some number of times and the average is recorded. After all the generations have been iterated through, a file named `evolved_pacman_vs_static_ghosts_yyyy-mm-dd_hh-mm-ss.csv` is produced which contains the average score against each controller for each generation.

### Running TestPacManAgainstStaticControllers

First, open up `TestPacManAgainstStaticControllers.java` in your IDE. Set the static variable `PAC_MAN_FILE` to the pacman file produced by `Evolutionizer` which you would like to test the genes over generations of. Then, run the `main` method of `TestPacManAgainstStaticControllers.java`. The console will print out the average scores as it determines them. On completion of the method, a CSV file called `evolved_pacman_vs_static_ghosts_yyyy-mm-dd_hh-mm-ss.csv` containing the average scores for each generation is produced.

## TestGhostsAgainstStaticControllers.java

You may want to test the ghosts of each generation produced by `Evolutionizer` against some static pac-man controllers to see the improvements over time. Using the `TestGhostsAgainstStaticControllers` class allows you to take a file produced by `Evolutionizer` and run it against the Generation,Nearest Pill,Random,Random NonRev,Starter Pacman provided by the Ms. Pac-Man AI library. Each generation of ghost competes against each Pac-man controller some number of times and the average is recorded. After all the generations have been iterated through, a file named `evolved_ghosts_vs_static_pacman_yyyy-mm-dd_hh-mm-ss.csv` is produced which contains the average score against each controller for each generation.

### Running TestGhostsAgainstStaticControllers

First, open up `TestGhostsAgainstStaticControllers.java` in your IDE. Set the static variable `GHOST_FILE` to the ghost file produced by `Evolutionizer` which you would like to test the genes over generations of. Then, run the `main` method of `TestGhostsAgainstStaticControllers.java`. The console will print out the average scores as it determines them. On completion of the method, a CSV file called `evolved_ghosts_vs_static_pacman_yyyy-mm-dd_hh-mm-ss.csv` containing the average scores for each generation is produced.
