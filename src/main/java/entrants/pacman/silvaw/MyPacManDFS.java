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
 * Pacman controller which uses depth-first search with the whole maze as the graph
 * to be traversed. Clearly, ghosts are ignored.
 * The DFS is reset if Pacman dies.
 */
public class MyPacManDFS extends PacmanController {
    private boolean initialized = false;
    private Deque<Node> nodeStack;
    private boolean[] marked;

    public MOVE getMove(Game game, long timeDue) {
        // Initialize the game if never initialized or Pacman has died
        if (!initialized || game.wasPacManEaten()) {
            initialize(game);
            initialized = true;
        }

        // Look at vertex on top of stack. Look at all vertexes that have not been visited
        Node n = nodeStack.peek();
        Optional<Map.Entry<MOVE, Integer>> nextNode = retrieveUnvisitedAdjacentNode(n);

        MOVE myMove;
        if (nextNode.isPresent()) {
            marked[nextNode.get().getValue()] = true;
            nodeStack.push(game.getCurrentMaze().graph[nextNode.get().getValue()]);
            myMove = nextNode.get().getKey();
        } else {
            nodeStack.pop();
            // Move back to old node
            myMove = findMoveToGetBackTo(n, nodeStack.peek());
        }

        return myMove;
    }

    private void initialize(Game game)
    {
        int pacmanCurrentNodeIndex = game.getPacmanCurrentNodeIndex();
        Node[] mazeGraph = game.getCurrentMaze().graph;
        Node pacNode = mazeGraph[pacmanCurrentNodeIndex];

        // Make stack
        nodeStack = new ArrayDeque<>();
        // Add current node
        nodeStack.push(pacNode);
        // Mark node as visited
        marked = new boolean[mazeGraph.length];
        marked[pacNode.nodeIndex] = true;
    }

    private Optional<Map.Entry<MOVE, Integer>> retrieveUnvisitedAdjacentNode(Node node)
    {
        for (Map.Entry<MOVE, Integer> moveNeighbor : node.neighbourhood.entrySet()) {
            if (!marked[moveNeighbor.getValue()]) {
                return Optional.of(moveNeighbor);
            }
        }
        return Optional.absent();
    }

    private MOVE findMoveToGetBackTo(Node current, Node old)
    {
        for (Map.Entry<MOVE, Integer> moveNeighbor : current.neighbourhood.entrySet()) {
            if (old.nodeIndex == moveNeighbor.getValue()) {
                return moveNeighbor.getKey();
            }
        }

        throw new RuntimeException("Old node not found");
    }
}