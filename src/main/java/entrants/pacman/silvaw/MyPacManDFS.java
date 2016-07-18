package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 * PacMan controller which uses depth-first search with the whole maze as the graph
 * to be traversed. Ghosts, pills, and power pills are ignored.
 *
 * The DFS is reset if PacMan is eaten by a ghost, so PacMan will try the same path again if she is eaten.
 */
public class MyPacManDFS extends PacmanController {
    /**
     * Has the stack of nodes and array of visited nodes been initialized?
     */
    private boolean initialized = false;
    /**
     * Stack of nodes used with DFS to backtrack
     */
    private Deque<Node> nodeStack;
    /**
     * Array of booleans answering if the node matching the position in the array has been visited by PacMan
     */
    private boolean[] visited;

    /**
     * PacMan will move in such a way that she will perform a DFS of the game board, with the DFS resetting upon
     * her death
     *
     * @param game a copy of the current game
     * @param timeDue the time the next move is due
     * @return the move to be used by PacMan
     */
    @Override
    public MOVE getMove(Game game, long timeDue) {
        // Initialize the game if never initialized or re-initialize if PacMan has died
        if (!initialized || game.wasPacManEaten()) {
            initialize(game);
            initialized = true;
        }

        // Look at vertex on top of stack. Look at all vertexes that have not been visited
        Node curNode = nodeStack.peek();
        // Find a neighboring unvisited node if it exists
        Optional<Map.Entry<MOVE, Integer>> nextNodeOptional = retrieveUnvisitedAdjacentNode(curNode);

        if (nextNodeOptional.isPresent()) {
            // If there is unvisited neighboring node, visit it and move to it
            Map.Entry<MOVE, Integer> nextNode = nextNodeOptional.get();
            visited[nextNode.getValue()] = true;
            nodeStack.push(game.getCurrentMaze().graph[nextNode.getValue()]);
            return nextNode.getKey();
        } else {
            // If no unvisited neighboring node, move back to previous node
            nodeStack.pop();
            return findMoveToNeighborNode(curNode, nodeStack.peek());
        }
    }

    /**
     * Initializes the stack containing the node path followed and the array describing what nodes have been visited
     *
     * @param game the current state of the game
     */
    private void initialize(Game game)
    {
        Node[] mazeGraph = game.getCurrentMaze().graph;
        nodeStack = new ArrayDeque<>();
        visited = new boolean[mazeGraph.length];

        Node pacNode = mazeGraph[game.getPacmanCurrentNodeIndex()];
        // Start the search from the current node and mark it as visited
        nodeStack.push(pacNode);
        visited[pacNode.nodeIndex] = true;
    }

    /**
     * @param n node to find an unvisited neighboring node of
     * @return pair describing the move to get to the node and the node number
     */
    private Optional<Map.Entry<MOVE, Integer>> retrieveUnvisitedAdjacentNode(Node n)
    {
        for (Map.Entry<MOVE, Integer> moveNodeNumberEntry : n.neighbourhood.entrySet()) {
            if (!visited[moveNodeNumberEntry.getValue()]) {
                return Optional.of(moveNodeNumberEntry);
            }
        }
        return Optional.absent();
    }

    private MOVE findMoveToNeighborNode(Node fromNode, Node neighboringToNode)
    {
        for (Map.Entry<MOVE, Integer> moveNodeNumberEntry : fromNode.neighbourhood.entrySet()) {
            if (neighboringToNode.nodeIndex == moveNodeNumberEntry.getValue()) {
                return moveNodeNumberEntry.getKey();
            }
        }

        throw new RuntimeException(String.format("Node %s was not a neighbor of node %s", neighboringToNode.nodeIndex,
                fromNode.nodeIndex));
    }
}