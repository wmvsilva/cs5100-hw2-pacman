% 2a

wife(elizabeth,philip).

wife(diana,charles).
wife(camilla,charles).
wife(anne,mark).
wife(anne,timothy).
wife(sarah,andrew).
wife(sophie,edward).

wife(kate,william).
wife(autumn,peter).
wife(zara,mike).

son(charles, philip).
son(charles, elizabeth).
daughter(anne, philip).
daughter(anne, elizabeth).
son(andrew, philip).
son(andrew, elizabeth).
son(edward, philip).
son(edward, elizabeth).

son(william, charles).
son(william, diana).
son(harry, charles).
son(harry, diana).
son(peter, mark).
son(peter, anne).
daughter(zara, mark).
daughter(zara, anne).
daughter(beatrice, andrew).
daughter(beatrice, sarah).
daughter(eugenie, andrew).
daughter(eugenie, sarah).
daughter(louise, edward).
daughter(louise, sophie).
son(james, edward).
son(james, sophie).

son(george, william).
son(george, kate).
daughter(savannah, peter).
daughter(savannah, autumn).
daughter(isla, peter).
daughter(isla, autumn).
daughter(mia, mike).
daughter(mia, zara).

% 2b
% Helper
mother(X, C):- child(C, X),wife(X, P).

husband(H,W):- wife(W,H).
spouse(X,Y):- wife(X,Y); husband(X,Y).
child(C,P):- son(C,P); daughter(C,P).
parent(P,C):- child(C,P).
grandChild(C,G):- parent(P,C),parent(G,P).
greatGrandParent(P,C):- grandChild(C,U),parent(P,U).
greatGrandChild(C,P):- greatGrandParent(P,C).
brother(B,S):- son(B,P),mother(P,S),(B \= S).
sister(S,C):- daughter(S,P),mother(P,C),(S \= C).
% Aunt is the sister of one's father or mother or the wife of one's uncle.
aunt(A,P):- (parent(Z,P),sister(A,Z)); wife(A,U),parent(Y,P),brother(U,Y).
% the brother of one's father or mother or the husband of one's aunt.
uncle(A,P):- (parent(Z,P),brother(A,Z)); husband(A,U),parent(Y,P),sister(U,Y).
brother_in_law(X,Y):- spouse(Y,S),brother(X,S).
sister_in_law(X,Y):- spouse(Y,S),sister(X,S).
