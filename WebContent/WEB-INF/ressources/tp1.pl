estVide([]).

appartient(X,[X|_]) :- !.
appartient(X,[_|Q]) :- appartient(X,Q).

estUnEnsemble([]).
estUnEnsemble([T|Q]) :- not(appartient(T,Q)), estUnEnsemble(Q).

estOrdonne([_]).
estOrdonne([]).
estOrdonne([T|[TQ|QQ]]) :- T<TQ, estOrdonne([TQ|QQ]).

intersection([],_,[]).
intersection([X|L1],L2,L3) :- \+ appartient(X,L2),!,intersection(L1,L2,L3).
intersection([X|L1],L2,[X|L3]) :- intersection(L1,L2,L3).

union([],L,L).
union([X|L1],L2,L3) :- appartient(X,L2),!,union(L1,L2,L3).
union([X|L1],L2,[X|L3]) :- union(L1,L2,L3).

difference([],_,[]).
difference([X|L1],L2,L3) :- appartient(X,L2),!,difference(L1,L2,L3).
difference([X|L1],L2,[X|L3]) :- difference(L1,L2,L3).

selectionner(X,[X|L],L).
selectionner(X,[Y|LY],[Y|LZ]) :- selectionner(X,LY,LZ).

permuter([],[]).
permuter([X|L1],P) :-  permuter(L1,P1),selectionner(X,P,P1).

