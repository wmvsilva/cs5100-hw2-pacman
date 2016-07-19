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
 */
public class MyPacManAStar extends PacmanController
{
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
    private boolean[] visited;
    /**
     * The last move completed by PacMan. Default -99 shows impossible starting move.
     */
    private int lastMove = -99;

    /**
     * @param game a copy of the current game
     * @param timeDue the time that the move is due
     * @return a move for PacMan such that PacMan will follow an A* path to a random unvisited node over and over
     */
    public MOVE getMove(Game game, long timeDue)
    {
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
        visited[game.getPacmanCurrentNodeIndex()] = true;
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
        visited = new boolean[mazeGraph.length];
        pathQueue = new LinkedList<>();
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
                int tentative_gScore = gScore.get(currentNodeIndex) + 1;
                if (!openSet.contains(neighbor)) {
                    openSet.add(neighbor);
                }
                else if (tentative_gScore >= gScore.get(neighbor)) {
                    continue;
                }

                cameFrom.put(neighbor, currentNodeIndex);
                gScore.put(neighbor, tentative_gScore);
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
        List<Integer> unvisitedNodes = new LinkedList<>();
        for (int i = 0; i < visited.length; i++) {
            if (!visited[i]) {
                unvisitedNodes.add(i);
            }
        }
        // Find random in list
        Random randomizer = new Random();
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
        for (Map.Entry<MOVE, Integer> entry : currentNode.neighbourhood.entrySet()) {
            if (entry.getValue() == nodeIndex) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("Node " + nodeIndex + " was not a neighbor of " +
                currentNode.nodeIndex);
    }
}