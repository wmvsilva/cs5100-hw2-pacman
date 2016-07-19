package entrants.pacman.silvaw;

import com.google.common.base.Optional;
import pacman.controllers.PacmanController;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

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
    private Stack<Node> nodeStack;
    /**
     * Set of node indexes visited by PacMan
     */
    private Set<Integer> visited;
    private Map<Integer, Map<Integer, MOVE>> nodeToReverseUntraveledNeighborhood;

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
        Optional<MoveNodeIndexPair> nextNodeOptional = retrieveUnvisitedAdjacentNode(curNode);

        if (nextNodeOptional.isPresent()) {
            // If there is unvisited neighboring node, visit it and move to it
            MoveNodeIndexPair nextNode = nextNodeOptional.get();

            visited.add(nextNode.getNodeIndex());
            nodeStack.push(game.getCurrentMaze().graph[nextNode.getNodeIndex()]);
            return nextNode.getMove();
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
        nodeStack = new Stack<>();
        nodeToReverseUntraveledNeighborhood = new HashMap<>();
        visited = new HashSet<>();

        for (Node n : mazeGraph) {
            Map<Integer, MOVE> reverseNeighborhood = new HashMap<>();
            for (Map.Entry<MOVE, Integer> entry : n.neighbourhood.entrySet()) {
                reverseNeighborhood.put(entry.getValue(), entry.getKey());
            }
            nodeToReverseUntraveledNeighborhood.put(n.nodeIndex, reverseNeighborhood);
        }

        Node pacNode = mazeGraph[game.getPacmanCurrentNodeIndex()];
        // Start the search from the current node and mark it as visited
        nodeStack.push(pacNode);
        visited.add(pacNode.nodeIndex);
    }

    /**
     * @param n node to find an unvisited neighboring node of
     * @return pair describing the move to get to the node and the node number
     */
    private Optional<MoveNodeIndexPair> retrieveUnvisitedAdjacentNode(Node n)
    {
        Map<Integer, MOVE> reverseUnvisitedNeighborhood = nodeToReverseUntraveledNeighborhood.get(n.nodeIndex);
        Set<Integer> unvisitedNodes = reverseUnvisitedNeighborhood.keySet();
        if (unvisitedNodes.isEmpty()) {
            return Optional.absent();
        } else {
            Set<Integer> unvisitedEdgesToRemove = new HashSet<>();
            for (Integer unvisitedNode : unvisitedNodes) {
                if (!visited.contains(unvisitedNode)) {
                    unvisitedNodes.removeAll(unvisitedEdgesToRemove);
                    return Optional.of(new MoveNodeIndexPair(reverseUnvisitedNeighborhood.get(unvisitedNode),
                            unvisitedNode));
                } else {
                    unvisitedEdgesToRemove.add(unvisitedNode);
                }
            }
            unvisitedNodes.removeAll(unvisitedEdgesToRemove);
            return Optional.absent();
        }
    }

    /**
     * @param fromNode node which a move must be found from
     * @param neighboringToNode the node which the move will move PacMan to from the fromNode
     * @return a move that would move PacMan on the fromNode to the given neighboring node
     */
    private MOVE findMoveToNeighborNode(Node fromNode, Node neighboringToNode)
    {
        for (Map.Entry<MOVE, Integer> moveNodeNumberEntry : fromNode.neighbourhood.entrySet()) {
            MoveNodeIndexPair moveNodeIndexPair = new MoveNodeIndexPair(moveNodeNumberEntry.getKey(),
                    moveNodeNumberEntry.getValue());

            if (neighboringToNode.nodeIndex == moveNodeIndexPair.getNodeIndex()) {
                return moveNodeIndexPair.getMove();
            }
        }

        throw new RuntimeException(String.format("Node %s was not a neighbor of node %s", neighboringToNode.nodeIndex,
                fromNode.nodeIndex));
    }

    /**
     * Pair containing a MOVE action which should lead to the node with the given index
     */
    private static class MoveNodeIndexPair
    {
        /**
         * Move action which should lead to the node with the given index
         */
        private MOVE move;
        /**
         * The node that the given mode should lead to
         */
        private int nodeIndex;

        MoveNodeIndexPair(MOVE move, int nodeIndex)
        {
            this.move = checkNotNull(move);
            this.nodeIndex = checkNotNull(nodeIndex);
        }
        
        MOVE getMove()
        {
            return move;
        }

        int getNodeIndex()
        {
            return nodeIndex;
        }
    }
}