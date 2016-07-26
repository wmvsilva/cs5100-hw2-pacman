% Is there a motorway that connects Snell engineering Centre (58) to Snell Library (59)?
Query:
motorway(58,59).

% A highway from hall A to hall B is desirable if it has only footpath connections 
Rule:
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