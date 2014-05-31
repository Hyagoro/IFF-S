jour(date(X,_,_),X).
mois(date(_,Y,_),Y).
année(date(_,_,Z),Z).

enseignant(1,titi,toto).
enseignant(2,tata,tutu).
enseignant(3,tati,tuto).

groupe(1,20).
groupe(2,10).
groupe(3,30).

salle(100,td,40).
salle(110,td,5).
salle(120,td,20).

matiere(1,info,td).
matiere(2,info,tp).
matiere(3,info,cours).

date(15,1,2012).
date(16,1,2012).
date(17,1,2012).

semaine(1,3,date(15,1,2012)).
semaine(2,3,date(16,1,2012)).
semaine(3,3,date(17,1,2012)).

cours(1,enseignant(1,titi,toto),groupe(1,20),salle(100,td,40),matiere(1,info,td),date(15,1,2012),120).
cours(2,enseignant(2,tata,tutu),groupe(2,10),salle(110,td,5),matiere(2,info,tp),date(16,1,2012),180).
cours(3,enseignant(3,tati,tuto),groupe(3,30),salle(120,td,20),matiere(3,info,cours),date(17,1,2012),60).
cours(4,enseignant(1,titi,toto),groupe(1,20),salle(100,td,40),matiere(1,info,td),date(16,1,2012),120).

nom(X,Y,Z) :- cours(X,enseignant(_,Y,Z),_,_,_,_,_).

duree(X,Y) :- cours(X,_,_,_,_,_,Y).

place(X) :- cours(X,_,groupe(_,Z),salle(_,_,Y),_,_,_),
			Y>=Z.	

combien(X,Y,Z):- combien2(X,Y,0,Z,[1,2,3,4]).
combien2(_,_,Z,Z,[]).
combien2(X,Y,K,Z,[T|L]) :- cours(T,_,_,_,matiere(X,_,_),date(J,M,A),_),
					semaine(_,Y,date(J,M,A)),
					W is K+1, !,
					combien2(X,Y,W,Z,L).
combien2(X,Y,K,Z,[_|L]) :- combien2(X,Y,K,Z,L).

heure(P,J,M,A,Z) :- heure2(P,J,M,A,Z,0,[1,2,3,4]).
heure2(_,_,_,_,Z,Z,[]).
heure2(P,J,M,A,C,Z,[T|L]) :- cours(T,enseignant(P,_,_),_,_,_,date(J2,M2,A2),D),
							avant(J,M,A,J2,M2,A2),
							W is (C + D) ,
							!,
							heure2(P,J,M,A,W,Z,L).
heure2(P,J,M,A,C,Z,[_|L]) :- heure2(P,J,M,A,C,Z,L).

avant(J,M,A,J2,M2,A2) :- A<A2 ; A=:=A2, M<M2 ; A=:=A2, M=:=M2, J=<J2.