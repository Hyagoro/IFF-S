package prolog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.List;

import jpl.Atom;
import jpl.Variable;
import jpl.Term;
import jpl.Query;

public class Engine
{
	private static File fichierProlog = null;
	public Engine(String fichierBaseRegleGenere)
	{
		fichierProlog = new File(fichierBaseRegleGenere);
	}
	public void consultFichierProlog(String fichierPL)
	{
		File file1 = new File(fichierPL);
		
		Term consult_arg[] =
		{ 
	            new Atom(file1.getAbsolutePath()) 
		};
		
		Query consult_query = new Query( "consult", consult_arg);
		
		@SuppressWarnings("deprecation")
		boolean consulted = consult_query.query();
		
		if ( !consulted )
		{
			System.err.println( "Consult failed" );
			System.exit( 1 );
		}
		
		//test_1(question, paramQuestion);
	}
	
	public static boolean listMortToBaseDeFait(List<String> liste, String typeDonne)
	{
		if(liste.size() > 0)
		{
			String contenuListe = "";
			String mot = liste.get(0);
			System.out.println(mot);
			contenuListe = contenuListe.concat(typeDonne+"("+dateSQLToDateProlog(mot)+").\n");
			
			ecrire(fichierProlog.getAbsolutePath() ,contenuListe);
			return true;
		}
		return false;
	}
	
	public boolean listToBaseDeFait(List<String> liste, String typeDonne)
	{
		String contenuListe = "";
		int max = liste.size();
		for(int i = 0; i < max; i++)
		{
			String mot = liste.get(i).replace(" ", "");//RoyalAirForce
			String lettreAdditionelle = "a";
			mot = lettreAdditionelle.concat(mot);
			
			contenuListe = contenuListe.concat(typeDonne+"("+i+","+mot+").\n");
		}
		ecrire(fichierProlog.getAbsolutePath() ,contenuListe);
		return true;
	}
	
	public static String dateSQLToDateProlog(String dateSQL)
	{
		String[] tabNb = dateSQL.split("-", 3);
		return "date("+tabNb[0]+","+tabNb[1]+","+tabNb[2]+")";
	}
	
	public static boolean triListToBaseDeFait(List<String> lieu, List<String> date_fin, List<String> date_debut, String typeDonne)
	{
		String contenuListe = "";
		int max = lieu.size();
		for(int i = 0; i < max; i++)
		{
			String motLieu = lieu.get(i);
			String motDateF = date_fin.get(i);
			String motDateD = date_debut.get(i);
			String lettreAdditionelle = "a";
			motLieu = lettreAdditionelle.concat(motLieu);
			motDateD = dateSQLToDateProlog(motDateD);
			motDateF = dateSQLToDateProlog(motDateF);
			
			contenuListe = contenuListe.concat(typeDonne+"("+i+","+motLieu+","+motDateD+","+motDateF+").\n");
		}
		ecrire(fichierProlog.getAbsolutePath() ,contenuListe);
		return true;
	}
	
	
	
	
	
	@SuppressWarnings("deprecation")
	static void
	test_2()
	{
		Term args[] = 
		{ 
			new Atom( "steve" ),
			new Atom( "ralf" )
		};
		Query query = new Query( "descendent_of", args );

		System.out.println( "descendent_of(steve, ralf) = " + query.query() );
	}
	
	public static String test_1(String question,Term parametres[])
	{


		Variable X = new Variable("X");
		Term args[] = {X};
		Query query = new Query( "lieuMort", args );		
		
		String tmp = "Lieu : ";
		Hashtable<?, ?> hashResult = null;
		try
		{
			hashResult = query.oneSolution();
		}
		catch(Exception e){System.err.println("Erreur fichier prolog : "+e.getCause());}
		if(hashResult != null)
		{
			Term bound_to_x = (Term) (hashResult).get("X");		
			tmp = tmp.concat(bound_to_x.toString().substring(1));
		}
		else
		{
			tmp = tmp.concat("Non trouvé.");
		}

		return tmp;
	}
	static void	test_3()
	{
		Variable X = new Variable();
		Term args[] = 
		{ 
			X,
			new Atom( "ralf" )
		};
		Query query = new Query( "descendent_of", args );

		System.out.println( "querying descendent_of( X, ralf )" );
		Hashtable<?,?> solution =	query.oneSolution();
		System.out.println( "X = " + solution.get( X ) );
	}
	
	
	static void
	test_4()
	{
		Variable X = new Variable();
		Term args[] = 
		{ 
			X,
			new Atom( "ralf" )
		};
		Query query = new Query( "descendent_of", args );

		System.out.println( "querying descendent_of( X, ralf )" );
		
		while ( query.hasMoreSolutions() )
		{
			Hashtable<?,?> solution = query.nextSolution();
			System.out.println( "X = " + solution.get( X ) );
		}

	}
	
	public static void supprimerFichier(String nomFic)
	{
		String adressedufichier = nomFic;//System.getProperty("user.dir") + "/"+ nomFic;
		File fichier = new File(adressedufichier);
		if(fichier.isFile())
			fichier.delete();
	}
	
	public static void ajouterRegles(String nomFichierSource, String nomFichierDest)
	{
		String chaine="";
		String fichier = nomFichierSource;
		
		//lecture du fichier texte	
		try
		{
			InputStream ips = new FileInputStream(fichier); 
			InputStreamReader ipsr = new InputStreamReader(ips);
			BufferedReader br = new BufferedReader(ipsr);
			String ligne;
			while ((ligne=br.readLine())!=null)
			{
				chaine+=ligne+"\n";
			}
			br.close(); 
		}		
		catch (Exception e)
		{
			System.out.println(e.toString());
		}
		ecrire(nomFichierDest, "\n"+chaine);
	}
	
	private static void ecrire(String nomFic, String texte)
	{
		String adressedufichier = nomFic;//System.getProperty("user.dir") + "/"+ nomFic;
		try
		{
			FileWriter fw = new FileWriter(adressedufichier, true);
			BufferedWriter output = new BufferedWriter(fw);
			output.write(texte);			
			output.flush();
			output.close();
			System.out.println("fichier créé");
		}
		catch(IOException ioe)
		{
			System.out.print("Erreur : ");
			ioe.printStackTrace();
		}

	}
}
