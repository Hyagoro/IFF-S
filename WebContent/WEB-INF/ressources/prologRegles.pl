dateSup(date(Y, M, D1), date(Y, M, D2)) :- !, D1 > D2.
dateSup(date(Y, M1, _), date(Y, M2, _)) :- !, M1 > M2.
dateSup(date(Y1, _, _), date(Y2, _, _)) :- Y1 > Y2.

lieuMort(L) :- attaque(_, L, date(Y1, M1, D1), date(Y2, M2, D2)),
			mort(date(Y3, M3, D3)),
			dateSup(date(Y3, M3, D3), date(Y1, M1, D1)),
			dateSup(date(Y2, M2, D2), date(Y3, M3, D3)).