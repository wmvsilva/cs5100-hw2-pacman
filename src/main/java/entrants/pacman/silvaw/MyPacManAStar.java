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
    private Queue<Integer> pathQueue = new LinkedList<>();
    private boolean initialized = false;
    private boolean[] marked;
    private int lastMove = -99;

    public MOVE getMove(Game game, long timeDue)
    {
        if (lastMove == game.getPacmanCurrentNodeIndex()) {
            System.out.println("DID NOT MOVE???");
            return MOVE.NEUTRAL;
        }
        lastMove = game.getPacmanCurrentNodeIndex();

        if (!initialized) {
            initialize(game);
            initialized = true;
        }
        // Pac man died? Clear the path from before...
        if (game.wasPacManEaten()) {
            pathQueue.clear();
        }
        // Node is visited
        marked[game.getPacmanCurrentNodeIndex()] = true;
        if (pathQueue.isEmpty()) {
            // Create random path to follow
            int pacManCurrentNodeIndex = game.getPacmanCurrentNodeIndex();
            // Find random unvisited node
            int randomUnvisitedNodeIndex = findRandomUnvisitedNode();

            Integer[] path = getShortestPath(pacManCurrentNodeIndex, randomUnvisitedNodeIndex, game);
            Collections.addAll(pathQueue, path);

            // Rip off first path queue
            pathQueue.remove();
            System.out.println(pathQueue);
            // Follow path
            return nodeToMove(pathQueue.remove(), game);
        } else {
            System.out.println("Currently on " + game.getPacmanCurrentNodeIndex());
            System.out.println(pathQueue.peek());
            // Follow path
            return nodeToMove(pathQueue.remove(), game);
        }
    }

    private void initialize(Game game)
    {
        Node[] mazeGraph = game.getCurrentMaze().graph;
        marked = new boolean[mazeGraph.length];
    }

    private Integer[] getShortestPath(int fromNodeIndex, int toNodeIndex, Game game)
    {
        Node[] graph = game.getCurrentMaze().graph;


        Set<Integer> closedSet = Sets.newHashSet();
        Set<Integer> openSet = Sets.newHashSet(fromNodeIndex);
        Map<Integer, Integer> cameFrom = new HashMap<>();

        Map<Integer, Integer> gScore = new HashMap<>();
        gScore.put(fromNodeIndex, 0);
        Map<Integer, Integer> fScore = new HashMap<>();
        fScore.put(fromNodeIndex, heuristic_cost_estimate(fromNodeIndex, toNodeIndex, game));

        while (!openSet.isEmpty()) {
            int currentNodeIndex = nodeWithLowestFScore(openSet, fScore);
            if (currentNodeIndex == toNodeIndex) {
                return reconstruct_path(cameFrom, currentNodeIndex);
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
                fScore.put(neighbor, gScore.get(neighbor) + heuristic_cost_estimate(neighbor, toNodeIndex, game));
            }
        }

        throw new RuntimeException("Failure");
    }

    private int heuristic_cost_estimate(int toNode, int goalNode, Game game)
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

    private Integer[] reconstruct_path(Map<Integer, Integer> cameFrom, int givenCurrent)
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

    private int findRandomUnvisitedNode()
    {
        List<Integer> unvisitedNodes = new LinkedList<>();
        for (int i = 0; i < marked.length; i++) {
            if (!marked[i]) {
                unvisitedNodes.add(i);
            }
        }
        // Find random in set
        Random randomizer = new Random();
        return unvisitedNodes.get(randomizer.nextInt(unvisitedNodes.size()));
    }

    private MOVE nodeToMove(int nodeIndex, Game game)
    {
        Node currentNode = game.getCurrentMaze().graph[game.getPacmanCurrentNodeIndex()];
        for (Map.Entry<MOVE, Integer> entry : currentNode.neighbourhood.entrySet()) {
            if (entry.getValue() == nodeIndex) {
                return entry.getKey();
            }
        }

        throw new RuntimeException("PATH NOT FOUND");
    }
}