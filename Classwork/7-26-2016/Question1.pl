footpath(53,52).
footpath(54,53).
footpath(56,57).
footpath(56,58).
footpath(57,58).
footpath(57,59).
footpath(58,59).
footpath(60,59).
footpath(83,65).
footpath(65,68).
footpath(76,69).

footpath(52,100).
highwayNode(100).
motorway(100,68).
motorway(100,83).
motorway(100,60).
motorway(100,53).
motorway(100,54).
motorway(100,101).
motorway(100,59).

motorway(60,83).
motorway(60,68).
motorway(60,59).
motorway(59,83).
motorway(59,68).
motorway(83,68).

motorway(54,53).

footpath(57,101).
highwayNode(101).
motorway(101,54).
motorway(101,53).
motorway(101,62).

motorway(62,83).
motorway(62,63).
motorway(62,64).
motorway(62,76).
motorway(62,66).
motorway(62,68).

motorway(56,54).

motorway(68,66).
motorway(68,76).
motorway(68,64).
motorway(68,63).

motorway(63,64).
motorway(63,76).
motorway(63,66).
motorway(63,69).

motorway(64,76).
motorway(64,66).
motorway(64,69).

motorway(76,66).

motorway(69,66).

route(A,B):- footpath(A,B);footpath(B,A).
route(A,B):- motorway(A,B);motorway(B,A).

route(A,C):- route(A,B), route(B,C).
