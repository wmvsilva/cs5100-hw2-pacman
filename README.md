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
         Before the while loop, everything is initialized which takes constant time.4
         The while loop uses the openSet which contains discovered nodes to be evaluated. At most, the openSet will
         contain every node |N|. For every neighbor of the node with the lowest f score, some constant time
         operations must be completed. At most, it will be 4 operations for each PacMan direction.
         Overall, knowing the number of edges is limited, we have |N|*c*4c = O(|N|) for A* here.
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
