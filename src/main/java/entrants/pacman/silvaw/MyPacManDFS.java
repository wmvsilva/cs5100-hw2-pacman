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
 *
 * For complexity calculations, |E| is the number of edges on the PacMan graph and |N| is the number of nodes on the
 * PacMan graph.
 *
 * Time Complexity (assuming PacMan doesn't die):
 * My code has the following steps.
 * 1. Initialization
 *    Constant time operations as well as initializing some maps which represent the graph.
 *    To create these maps, I take every node and its incident edges and place them into the maps.
 *    A constant time operation is performed for every edge connection both ways for a total of 2|E| operations.
 * 2a. Attempt to find an unvisited node on the current node. To do this, edges leading to visited nodes are checked
 *     and removed. Given that every edge both ways has to be removed, we will do this 2|E| times.
 * 2b. If unvisited node found, we put it on stack and say its visited. We do have to say every node is visited
 *     for a total of |N| operations.
 *     Else, we move back to the previous node by popping off the stack. For every node DFS traverses, it has to
 *     eventually come off the stack. This is a total of |N| operations.
 *
 * The total number of operations is:
 * 2|E| + 2|E| + |N| + |N|
 * = 4|E| + 2|N|
 * = O(E) + O(N)
 * = O(E+N)
 *
 * The time complexity is O(E+N).
 *
 * ===
 *
 * Space Complexity (assuming PacMan doesn't die):
 * My code has the following steps:
 * 1. Initialization
 *    A map is created to better represent the graph. This requires an entry for every edge both ways for 2|E| space.
 *    Another similar map is created for another 2|E| space.
 * 2. We store what nodes have been visited as well as a stack of nodes. Eventually all nodes will be visited and the
 *    stack could very well hold all nodes. This requires 2|N| of space.
 * The total number of operations is:
 * 2|E| + 2|E| + |N| + |N|
 * = 4|E| + 2|N|
 * = O(E) + O(N)
 * = O(E+N)
 *
 * The space complexity is O(E+N). One could also argue that it is just O(N) because my initial map operations could be
 * considered pre-processing to solve the actual problem. If the graph had a better initial representation, the space
 * complexity is O(N).
 *
 */
public class MyPacManDFS extends PacmanController
{
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
    /**
     * A map of node indexes to a map of nodes visitable by the key node to the moves necessary to get to that
     * neighbor node from the key node. Only neighbor nodes which have not been visited should be in the value. If
     * neighbor nodes have been visited, they are removed from the neighborhood map.
     */
    private Map<Integer, Map<Integer, MOVE>> nodeToReverseUntraveledNeighborhood;
    /**
     * A map of node indexes to a map of nodes visitable by the key node to the moves necessary to get to that
     * neighbor node from the key node
     */
    private Map<Integer, Map<Integer, MOVE>> nodeToReverseNeighborHood;

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
     * Initializes the stack containing the node path followed, set of visited nodes, and the maps describing the
     * neighbors of each node and the moves to get to those neighbors
     *
     * @param game the current state of the game
     */
    private void initialize(Game game)
    {
        Node[] mazeGraph = game.getCurrentMaze().graph;
        nodeStack = new Stack<>();
        nodeToReverseUntraveledNeighborhood = new HashMap<>();
        nodeToReverseNeighborHood = new HashMap<>();
        visited = new HashSet<>();

        for (Node n : mazeGraph) {
            Map<Integer, MOVE> reverseNeighborhood = new HashMap<>();
            for (Map.Entry<MOVE, Integer> entry : n.neighbourhood.entrySet()) {
                reverseNeighborhood.put(entry.getValue(), entry.getKey());
            }
            nodeToReverseUntraveledNeighborhood.put(n.nodeIndex, reverseNeighborhood);
            nodeToReverseNeighborHood.put(n.nodeIndex, new HashMap<>(reverseNeighborhood));
        }

        Node pacNode = mazeGraph[game.getPacmanCurrentNodeIndex()];
        // Start the search from the current node and mark it as visited
        nodeStack.push(pacNode);
        visited.add(pacNode.nodeIndex);
    }

    /**
     * Retrieves a move and a node number to an unvisited node from given node n. The
     * nodeToReverseUntraveledNeighborhood map is maintained here and visited neighbors are removed so they do not need
     * to be checked again.
     *
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
        return nodeToReverseNeighborHood.get(fromNode.nodeIndex).get(neighboringToNode.nodeIndex);
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