package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

import java.util.*;

/**
 * PacMan controller which uses A* search to find a path to a random unvisited node over and over.
 * Ghosts, pills, and power pills are ignored. If PacMan dies, A* simply finds a new random unvisited node and begins
 * again.
 *
 * For complexity calculations, |E| is the number of edges on the PacMan graph and |N| is the number of nodes on the
 * PacMan graph.
 *
 * Time Complexity (assuming no PacMan death):
 * My code does the following:
 * 1. Initialization
 *    The variables are initialized. To make the reverse neighborhood variable, it requires an operation for every edge
 *    in the graph for |E| operations.
 * 2. Visit the current node. We must do this for every node in every path created.
 * 3. Either follow or create path
 * 3a. Create path
 *     Find a random node takes constant time.
 *     A* search-
 *         Before the while loop, everything is initialized which takes constant time.4
 *         The while loop uses the openSet which contains discovered nodes to be evaluated. At most, the openSet will
 *         contain every node |N|. For every neighbor of the node with the lowest f score, some constant time
 *         operations must be completed. At most, it will be 4 operations for each PacMan direction.
 *         Overall, knowing the number of edges is limited, we have |N|*c*4c = O(|N|) for A* here.
 *     Then we just start following the path which is a constant time operation.
 * 3b. Follow path
 *     This just takes a node off the queue and converts the node to a move. This will occur once for every node in
 *     the paths produced.
 *
 * Analyzing the number of paths created, at most, we will have to create one to get to every node (although this is
 * very unlikely).
 * Therefore, for this algorithm to complete the PacMan graph, we have |E| + (|N|)(|N| + |N| + |N|) + some constant
 * time operations for a total of |E| + 3|N|^2 + C operations.
 * = O(N^2 + E)
 * As with the BFS analysis, the time complexity could be O(N^2) if the initial graph representation was better.
 *
 * =====
 *
 * Space Complexity (assuming no PacMan death):
 * My code does the following:
 * 1. Initialization
 *    We have a list of unvisited nodes for |N| space. Also, A* makes a path queue which is only as large as |N|.
 * 2. A* calculation contains 5 maps. Each map will only hold as many nodes in the graph for |N| space.
 *
 * In total, all data objects are |N| large at most.
 * = O(N) space complexity
 */
public class MyPacManAStar extends PacmanController
{
    /**
     * Random number producer
     */
    private static final Random randomizer = new Random();

    /**
     * Queue containing the node path computing by A* that PacMan should follow
     */
    private Queue<Integer> pathQueue;
    /**
     * Has the node path queue and array of visited nodes been initialized?
     */
    private boolean initialized = false;
    /**
     * The value of each index i in this node answers if node with index i has been visited
     */
    private List<Integer> unvisitedNodes;
    /**
     * The last move completed by PacMan. Default -99 shows impossible starting move.
     */
    private int lastMove = -99;
    /**
     * A map of node indexes to a map of nodes visitable by the key node to the moves necessary to get to that
     * neighbor node from the key node
     */
    private Map<Integer, Map<Integer, MOVE>> nodeToReverseNeighborHood;

    /**
     * @param game a copy of the current game
     * @param timeDue the time that the move is due
     * @return a move for PacMan such that PacMan will follow an A* path to a random unvisited node over and over
     */
    public MOVE getMove(Game game, long timeDue)
    {
        if ((game.getNumberOfActivePills() == 0) && (game.getNumberOfActivePowerPills() == 0)) {
            // Game has ended. We need to reset everything for next maze.
            initialized = false;
            return MOVE.NEUTRAL;
        }
        if (lastMove == game.getPacmanCurrentNodeIndex()) {
            // For some reason, when PacMan dies and there is a game over, PacMan does not move but another getMove
            // call occurs. The game logic expects PacMan to be in a certain location so return here if that happens.
            return MOVE.NEUTRAL;
        }
        lastMove = game.getPacmanCurrentNodeIndex();

        if (!initialized) {
            initialize(game);
            initialized = true;
        }
        // Pac man died? Don't keep trying to follow the old A* path...
        if (game.wasPacManEaten()) {
            pathQueue.clear();
        }

        // Mark current node as visited
        unvisitedNodes.remove(Integer.valueOf(game.getPacmanCurrentNodeIndex()));
        if (pathQueue.isEmpty()) {
            // The A* path was followed, create a new path to follow

            int randomUnvisitedNodeIndex = findRandomUnvisitedNode();
            int pacManCurrentNodeIndex = game.getPacmanCurrentNodeIndex();
            // Use A* to create path to some random unvisited node
            Integer[] path = getShortestPath(pacManCurrentNodeIndex, randomUnvisitedNodeIndex, game);
            Collections.addAll(pathQueue, path);

            // The HEAD of the queue is the current node. This should be removed.
            pathQueue.remove();
            // Start following the path...
            return nodeToMove(pathQueue.remove(), game);
        } else {
            // Continue following the path
            return nodeToMove(pathQueue.remove(), game);
        }
    }

    /**
     * Initialize the variables needed for this class to function
     *
     * @param game a copy of the current game
     */
    private void initialize(Game game)
    {
        Node[] mazeGraph = game.getCurrentMaze().graph;
        unvisitedNodes = new ArrayList<>();
        for (Node n : mazeGraph) {
            unvisitedNodes.add(n.nodeIndex);
        }
        pathQueue = new LinkedList<>();
        nodeToReverseNeighborHood = new HashMap<>();

        for (Node n : mazeGraph) {
            Map<Integer, MOVE> reverseNeighborhood = new HashMap<>();
            for (Map.Entry<MOVE, Integer> entry : n.neighbourhood.entrySet()) {
                reverseNeighborhood.put(entry.getValue(), entry.getKey());
            }
            nodeToReverseNeighborHood.put(n.nodeIndex, new HashMap<>(reverseNeighborhood));
        }
    }

    /**
     * @param fromNodeIndex the node to start the path
     * @param toNodeIndex the node to get ot
     * @param game a copy of the current game
     * @return a list of node indices representing a path from fromNodeIndex to toNodeIndex creating using A* search
     */
    private Integer[] getShortestPath(int fromNodeIndex, int toNodeIndex, Game game)
    {
        Node[] graph = game.getCurrentMaze().graph;

        // Set of nodes evaluated
        Set<Integer> closedSet = Sets.newHashSet();
        // Set of discovered nodes to be evaluated
        Set<Integer> openSet = Sets.newHashSet(fromNodeIndex);
        Map<Integer, Integer> cameFrom = new HashMap<>();

        Map<Integer, Integer> gScore = new HashMap<>();
        gScore.put(fromNodeIndex, 0);
        Map<Integer, Integer> fScore = new HashMap<>();
        fScore.put(fromNodeIndex, heuristicCostEstimate(fromNodeIndex, toNodeIndex, game));

        while (!openSet.isEmpty()) {
            int currentNodeIndex = nodeWithLowestFScore(openSet, fScore);
            if (currentNodeIndex == toNodeIndex) {
                return reconstructPath(cameFrom, currentNodeIndex);
            }

            openSet.remove(currentNodeIndex);
            closedSet.add(currentNodeIndex);
            for (int neighbor : graph[currentNodeIndex].neighbourhood.values()) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                int tentativeGScore = gScore.get(currentNodeIndex) + 1;
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }
                else if (tentativeGScore >= gScore.get(neighbor)) {
                    continue;
                }

                cameFrom.put(neighbor, currentNodeIndex);
                gScore.put(neighbor, tentativeGScore);
                fScore.put(neighbor, gScore.get(neighbor) + heuristicCostEstimate(neighbor, toNodeIndex, game));
            }
        }

        throw new RuntimeException("Failure");
    }

    private int heuristicCostEstimate(int toNode, int goalNode, Game game)
    {
        return game.getManhattanDistance(toNode, goalNode);
    }

    private int nodeWithLowestFScore(Set<Integer> openSet, Map<Integer, Integer> fScore)
    {
        Optional<Integer> result = Optional.absent();
        Optional<Integer> lowestFScore = Optional.absent();

        for (int node : openSet) {
            if (!result.isPresent()) {
                result = Optional.of(node);
                lowestFScore = Optional.of(fScore.get(node));
            } else if (fScore.get(node) < lowestFScore.get()) {
                result = Optional.of(node);
                lowestFScore = Optional.of(fScore.get(node));
            }
        }

        return result.get();
    }

    private Integer[] reconstructPath(Map<Integer, Integer> cameFrom, int givenCurrent)
    {
        int current = givenCurrent;
        List<Integer> totalPath = Lists.newArrayList(current);
        while (cameFrom.containsKey(current)) {
            current = cameFrom.get(current);
            totalPath.add(current);
        }
        totalPath = Lists.reverse(totalPath);
        return totalPath.toArray(new Integer[totalPath.size()]);
    }

    /**
     * @return node index of an unvisited node
     */
    private int findRandomUnvisitedNode()
    {
        // Find random in list
        return unvisitedNodes.get(randomizer.nextInt(unvisitedNodes.size()));
    }

    /**
     * @param nodeIndex node to move to from current node
     * @param game copy of the current game
     * @return the move to get to the given nodeIndex from the PacMan's current node
     */
    private MOVE nodeToMove(int nodeIndex, Game game)
    {
        Node currentNode = game.getCurrentMaze().graph[game.getPacmanCurrentNodeIndex()];
        return nodeToReverseNeighborHood.get(currentNode.nodeIndex).get(nodeIndex);
    }
}