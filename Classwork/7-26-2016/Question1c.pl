% 1. Is there a motorway that connects Snell engineering Centre (58) to Snell Library (59)?
Query:
motorway(58,59).


% 2. A highway from hall A to hall B is desirable if it has only footpath connections 
Rule:
desirable(A,B):- only_footpath_exists(A,B).

bothWaysFootpath(A,B):- footpath(A,B); footpath(B,A).

only_footpath_exists(X,Y) :- only_footpath(X,Y,_), !.

only_footpath(A,B,Path) :-
       only_footpath_travel(A,B,[A],Q), 
       reverse(Q,Path).

only_footpath_travel(A,B,P,[B|P]) :- 
       bothWaysFootpath(A,B).
only_footpath_travel(A,B,Visited,Path) :-
       bothWaysFootpath(A,C),           
       C \== B,
       \+member(C,Visited),
       only_footpath_travel(C,B,[C|Visited],Path).


% 3. A path from hall A to hall B is short if it is only of length 1 on the graph
Rule:
short(A,B):- connection(A,B).


% 4. The path from 66 to 68 is the path for the "cool kids".
Fact:
cool_kids_path(66,68).


% 5. A path from hall A to hall B is undesirable if it has at least one footpath connection
Rule (uses code from problem #2):
undesirable(A,B):- not(desirable(A,B)).


% 6. Is there a footpath that connects 76 to 69?
Query:
footpath(76,69);footpath(69,76).