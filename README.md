# CS 5100 Homework 3
Assignment information can be found [here](https://docs.google.com/document/d/16Bf6-k3MuAswfZIrEwjE63hnetsyc32TGUOibEMROr8/edit).

## Modules
- entrants.pacman.silvaw.MyPacManMiniMax
  This implements the MiniMax search algorithm for Ms. PacMan.

## Compilation Instructions
This can be compiled as a standard Maven project in any IDE.
To run the DFS PacMan with random ghosts, run MainDFS.java.
To run the A* PacMan with random ghosts, run MainAStar.java.

## Complexity
```
Time Complexity:
My code has the following steps.

1. Create MiniMax tree
1a. For every other depth for PacMan, calculate game states for possible PacMan moves and create branches for each.
    Four game states are calculated. At a maximum, there could be 4 branches from this node.

1b. For every other depth for ghosts, calculate game states for possible ghost moves and create branches for each.
    At a maximum, there could be 3^4 branches. The game state may be calculated for each.

1c. For each leaf, calculate heuristic. This involves getting the score, total active pills, distance to nearest
    ghost, and determining the distance to the nearest pill. Getting the score and total active pills are constant
    time operations. Getting the distance to the nearest ghost takes 4 constant time operations since shortest path
    distances are pre-computed. Getting the distance to the nearest pill takes at most |P| constant time operations
    where P is the set of all pills.

    The average branching factor is (4 + 3^4)/2 = 42.5.
    If b is the branching factor, for a tree, there will be b^m nodes at depth m. Therefore, a tree has
    (b^0 + b^1 + b^2 + ... + b^m) nodes total. Keeping the highest order, that is O(b^m) operations for branching
    factor b with tree of depth m.
    Overall, we will have (42.5)^m leaves where the heuristic is calculated that uses P operations.
    For each nodes at a lower depth, that requires (b^0 + b^1 + ... + b^(m-1)) that may each need to calculate
    new game states. At most, 3^4 game states will need to be calculated in the case of ghosts.
    This gives us a total of P(42.5)^m + (3^4)(42.5)^(m-1).
    O(P(42.5)^m + (3^4)(42.5)^(m-1))
    = O(P(42.5)^m)

2. Select best move using MiniMax tree that has (b^0 + b^1 + b^2 + ... + b^m) nodes total.
2a. If leaf, return a value.
    This is a simple constant time operation that must be completed b^m times total.
2b. If node, find max value among children.
    Nodes on a given depth could have b^1,b^2,..., or b^m children total. Finding the max of the children requires
    looking at all the children for b^1 + b^2 + ... + b^m operations total.

    Overall, this is a total of b^m + (b^1 + b^2 + ... + b^m) = O(b^m).
    The average branching factor is 42.5 for O(42.5^m) for step.

The total time complexity is O(P(42.5)^m) + O(42.5^m)
= O(P(42.5)^m) where m is the depth of the MiniMax tree and P is the number of pills on a given maze. In practice,
however, the branching factor will actually be much less as PacMan and the ghosts are normally stuck in corridors
which will greatly limit the number of moves available.

=================

Space Complexity:
My code has the following steps:
1. Create MiniMax tree
   For each branch, the current game with previous moves by ghosts and PacMan is passed down. However, only one
   branch is created at a time and it goes down 1 depth each time until it reaching the leaf and creates all of the
   other nodes at that level. It is very similar to DFS. At each node, the game states on the nodes of the current
   search path must be maintained. Additionally, at each DFS node, possible ghost moves and pacman moves may be made.
   The game state and possible moves are no longer required after the DFS has returned from the search and will be
   garbage collected.
   Overall, for a maximum of m nodes, the game state and possible move set of ghosts or pacman must be maintained
   for G*3^4*c*m nodes where G is the size of the game, 3^4*C is the space contained by possible ghost moves, and
   m is the depth. Assuming constant game size, this gives us a O(m) space complexity for this step.

2. Select best move from MiniMax tree
   This also operates in a DFS-like fashion. All leaves will return a value. All nodes will compare the heuristic
   values produced by their children. At most, m MoveNumber objects will be required in memory. This is O(m) space
   complexity.

   Overall, O(m) + O(m)
   = O(m)

   The space complexity is O(m) where m is the depth of the MiniMax tree.
```

----------------------------------------------------------------
----------------------------------------------------------------
----------------------------------------------------------------
----------------------------------------------------------------
----------------------------------------------------------------

# CS 5100 HOMEWORK 2
Assignment information can be found [here](https://docs.google.com/document/d/16Bf6-k3MuAswfZIrEwjE63hnetsyc32TGUOibEMROr8/edit?pref=2&pli=1).

## Modules
- entrants.pacman.silvaw.MyPacManDFS
  This implements the uninformed search algorithm depth first search for Ms. PacMan.
- entrants.pacman.silvaw.MyPacManAStar
  This implements the informed search algorithm A* for Ms. PacMan.
  
## Complexity
### MyPacManDFS
For complexity calculations, |E| is the number of edges on the PacMan graph and |N| is the number of nodes on the
PacMan graph.

Time Complexity (assuming PacMan doesn't die):

My code has the following steps.

1. Initialization
   Constant time operations as well as initializing some maps which represent the graph.
   
   To create these maps, I take every node and its incident edges and place them into the maps.
   
   A constant time operation is performed for every edge connection both ways for a total of 2|E| operations.
   
2a. Attempt to find an unvisited node on the current node. To do this, edges leading to visited nodes are checked
    and removed. Given that every edge both ways has to be removed, we will do this 2|E| times.
    
2b. If unvisited node found, we put it on stack and say its visited. We do have to say every node is visited
    for a total of |N| operations.
    
Else, we move back to the previous node by popping off the stack. For every node DFS traverses, it has to
eventually come off the stack. This is a total of |N| operations.

The total number of operations is:

2|E| + 2|E| + |N| + |N|

= 4|E| + 2|N|

= O(E) + O(N)

= O(E+N)

The time complexity is O(E+N).

===

Space Complexity (assuming PacMan doesn't die):

My code has the following steps:

1. Initialization

   A map is created to better represent the graph. This requires an entry for every edge both ways for 2|E| space.
   
   Another similar map is created for another 2|E| space.
   
2. We store what nodes have been visited as well as a stack of nodes. Eventually all nodes will be visited and the
   stack could very well hold all nodes. This requires 2|N| of space.
   
The total number of operations is:

2|E| + 2|E| + |N| + |N|

= 4|E| + 2|N|

= O(E) + O(N)

= O(E+N)

The space complexity is O(E+N). One could also argue that it is just O(N) because my initial map operations could be
considered pre-processing to solve the actual problem. If the graph had a better initial representation, the space
complexity is O(N).

### MyPacManAStar
For complexity calculations, |E| is the number of edges on the PacMan graph and |N| is the number of nodes on the
 PacMan graph.

 Time Complexity (assuming no PacMan death):
 
 My code does the following:
 
 1. Initialization
    The variables are initialized. To make the reverse neighborhood variable, it requires an operation for every edge
    in the graph for |E| operations.
    
 2. Visit the current node. We must do this for every node in every path created.
 
 3. Either follow or create path
 
 3a. Create path
 
     Find a random node takes constant time.
     
     A* search-
     * Before the while loop, everything is initialized which takes constant time.
         
     * The while loop uses the openSet which contains discovered nodes to be evaluated. At most, the openSet will contain every node |N|. For every neighbor of the node with the lowest f score, some constant time operations must be completed. At most, it will be 4 operations for each PacMan direction.
     
     * Overall, knowing the number of edges is limited, we have |N|*c*4c = O(|N|) for A* here.
         
     Then we just start following the path which is a constant time operation.
     
 3b. Follow path
 
     This just takes a node off the queue and converts the node to a move. This will occur once for every node in
     the paths produced.

 Analyzing the number of paths created, at most, we will have to create one to get to every node (although this is
 very unlikely).
 
 Therefore, for this algorithm to complete the PacMan graph, we have |E| + (|N|)(|N| + |N| + |N|) + some constant
 time operations for a total of |E| + 3|N|^2 + C operations.
 
 = O(N^2 + E)
 
 As with the BFS analysis, the time complexity could be O(N^2) if the initial graph representation was better.

 =====

 Space Complexity (assuming no PacMan death):
 
 My code does the following:
 
 1) Initialization
    We have a list of unvisited nodes for |N| space. Also, A* makes a path queue which is only as large as |N|.
    
 2) A* calculation contains 5 maps. Each map will only hold as many nodes in the graph for |N| space.

 In total, all data objects are |N| large at most.
 
 = O(N) space complexity

## Compilation Instructions
This can be compiled as a standard Maven project in any IDE.
To run the DFS PacMan with random ghosts, run MainDFS.java.
To run the A* PacMan with random ghosts, run MainAStar.java.

# Competition ReadMe

Rules available on our site:

www.pacmanvghosts.co.uk

Change the package names to your username on the site.

Edit the entrants code

Use the bash script to prepare the zip file for submission (Works on Windows under Git bash)
