package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

import java.util.*;

/**
 * Pacman controller which uses depth-first search with the whole maze as the graph
 * to be traversed. Clearly, ghosts are ignored.
 * The DFS is reset if Pacman dies.
 */
public class MyPacManAStar extends PacmanController
{
    private Queue<Integer> pathQueue = new LinkedList<>();
    private boolean initialized = false;
    private boolean[] marked;
    private int lastMove = -99;

    private void initialize(Game game)
    {
        Node[] mazeGraph = game.getCurrentMaze().graph;
        marked = new boolean[mazeGraph.length];
    }

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
            int pacmanCurrentNodeIndex = game.getPacmanCurrentNodeIndex();
            Node[] mazeGraph = game.getCurrentMaze().graph;
            // From node
            Node pacNode = mazeGraph[pacmanCurrentNodeIndex];
            // Find random unvisited node
            int randomUnvisitedNodeIndex = findRandomUnvisitedNode();

            // TODO Implement shortest path with A*
            int[] path = game.getShortestPath(pacmanCurrentNodeIndex, randomUnvisitedNodeIndex);
            for (Integer nodeIndex : path) {
                pathQueue.add(nodeIndex);
            }
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