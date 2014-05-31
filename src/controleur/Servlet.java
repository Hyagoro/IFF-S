package controleur;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jdbc.IffJDBC;

import jpl.Atom;
import jpl.JPL;
import jpl.Term;
import jpl.Variable;
import prolog.Engine;


public class Servlet extends HttpServlet 
{
	private static final long serialVersionUID = 4621018881731510247L;
	private Engine engine;
	public void init()
	{
		JPL.init();
		
		System.out.println("JPL initialisé");
		
		/* JDBC pour MySQL */
		try 
		{
		    Class.forName( "com.mysql.jdbc.Driver" );
		    System.out.println("Driver MySQL chargé");
		} 
		catch ( ClassNotFoundException e ) 
		{
		    e.printStackTrace();
		}
	}
	
	public void destroy()
	{
		
	}
	
	@SuppressWarnings("static-access")
	public void doGet(HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException
	{
		String paramQuestion = request.getParameter( "question" );
//		String chaine = "";
		String resultat = "NULL";
		
		if(paramQuestion != null)
		{
			ServletContext context = getServletContext();
			String fichier = context.getRealPath("/WEB-INF/ressources/test.pl");
			String fichierGenere = context.getRealPath("/WEB-INF/ressources/prologRegles.pl");
			
			/**
			 * Pas de majuscule dans la question
			 * Variables en majuscule (règle Prolog)
			 */
			String[] words = 
			{
					"^.+\\(",
					"\\(.+\\)",
					"[A-Z]+"
			};
			
			/**
			 * Début du parsing  
			 */
			
			String question = null;
			LinkedList<Term> listArguments = new LinkedList<Term>();
			for(int i=0;i<words.length;i++)
            {
				Pattern pattern = Pattern.compile(words[i],Pattern.MULTILINE);
				Matcher matcher = pattern.matcher(paramQuestion);
				
				while(matcher.find())
				{
					if(i == 0)
					{
						question = paramQuestion.substring(matcher.start(), matcher.end() - 1);
						System.out.println("Question : "+question);
					}
					else if(i == 1)
					{
						//joe,ralph,jean
						String tmp = paramQuestion.substring(matcher.start() + 1, matcher.end() - 1);
						String[]tabArguments = null;
						if(tmp.contains(","))
							tabArguments = tmp.split(",");
						for(int j = 0; j < tabArguments.length ;j++)
						{
							String tmp2 = tabArguments[j];
							tmp2 = tmp2.trim();
							tmp2 = tmp2.replaceAll("\\s", "");
							listArguments.add(new Atom(tmp2));
							System.out.println("Arg["+j+"] : "+tabArguments[j]);
						}							
					}
					else if(i == 2)
					{
						String tmp = paramQuestion.substring(matcher.start(), matcher.end());
						listArguments.add(new Variable(tmp));
					}
				}
            }
						
			Term[] args = new Term[listArguments.size()];
			for(int i = 0; i < listArguments.size(); i++)
			{
				args[i] = listArguments.get(i);
			}
			
			/**
			 * Fin du parsing  
			 */
			
	
			engine = new Engine(fichierGenere);
			System.out.println("Etape 2 ! "+fichier);
			engine.consultFichierProlog(fichier);
			resultat = engine.test_1(question, args);

		}
		
		String message = "Résultat : "+paramQuestion+" : "+resultat;
		
		request.setAttribute( "test", message);
		this.getServletContext().getRequestDispatcher( "/WEB-INF/test.jsp" ).forward( request, response );
	}
	
	private String nomPrenomToRequeteSQL(String nom, String prenom)
	{
		return "SELECT Regiment FROM Soldat WHERE Nom = \""+nom+"\" and Prenom = \""+prenom+"\";";
	}
	
	private String requeteGetPrenoms(String nom, String prenomRecherche, IffJDBC bdd)
	{
		String prenoms = null;
		List<String> result = bdd.executerRequete("SELECT Prenom FROM Soldat WHERE Nom = \""+nom+"\";","Prenom");
		
		for(int j = 0; j < result.size(); j++)
		{	
			prenoms = result.get(j);
			System.out.println("trouvé : "+prenoms);
			
			if(prenoms.contains(prenomRecherche))
			{
				return prenoms;
			}
		}
		System.out.println("[1]prenoms : "+prenoms);
		return "";
	}
	
	private List<String> requeteGetRegiments(String regimentRecherche1, IffJDBC bdd)
	{
		String regimentRecherche = regimentRecherche1;
		List<String> result = bdd.executerRequete("SELECT regiments FROM attaques;","regiments");
        System.out.println("Régiment obtenu du soldat : "+regimentRecherche);

		List<String> listeRegiments = new LinkedList<String>();
		String regiment = null;
		regimentRecherche = regimentRecherche.trim();
		for(int j = 0; j < result.size(); j++)
		{	
			regiment = result.get(j);
			System.out.println("Recherche de "+regimentRecherche+" dans "+regiment);
			
			String tableauRegiments[] = regiment.split(",");
			for(int i = 0; i < tableauRegiments.length; i++)
			{
				tableauRegiments[i] = tableauRegiments[i].trim();
				System.out.println("Comparaison de ."+regimentRecherche+". et de ."+tableauRegiments[i]+"." );
				if(regimentRecherche.equals(tableauRegiments[i]))
				{
					listeRegiments.add(regiment);
					System.out.println("Regiment parmis : "+regiment);
				}
			}
//			if(regiment.contains(regimentRecherche))
//			{
//				listeRegiments.add(regiment);
//				System.out.println("Regiment parmis : "+regiment);
//			}
		}
		return listeRegiments;
	}
	
//	private String requeteGetIDRegiments(String regimentRecherche, IffJDBC bdd)
//	{
//		List<String> result = bdd.executerRequete("SELECT id FROM attaques;","regiments");
//		
//		String regiment = null;
//		for(int j = 0; j < result.size(); j++)
//		{	
//			regiment = result.get(j);
//			if(regiment.contains(regimentRecherche))
//			{
//				return regiment;
//			}
//		}
//		System.out.println("[1]regiment : "+regiment);
//		return "";
//	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		ServletContext context = getServletContext();
		String fichierGenere = context.getRealPath("/WEB-INF/ressources/prologDonnees.pl");
		String fichierRegles = context.getRealPath("/WEB-INF/ressources/prologRegles.pl");
		
        try 
        {
        	
            int length = request.getContentLength();
            byte[] input = new byte[length];
            
            ServletInputStream sin = request.getInputStream();
            int c, 
            count = 0 ;
            while ((c = sin.read(input, count, input.length-count)) != -1) 
            {
                count +=c;
            }
            sin.close(); 
            String recievedString = new String(input);
            response.setStatus(HttpServletResponse.SC_OK);
            OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
            
            /* Ce qu'on reçoit du mobile est parsé par un # "Jean#Michel" */
            String nomPrenom[] = recievedString.split("#");
            
            System.out.println("Nom, Prenom : "+nomPrenom[0]+" , "+ nomPrenom[1]);
            String nom = nomPrenom[0];
            String prenom = nomPrenom[1];
            
            IffJDBC bdd = new IffJDBC();
            /* Dans le cas ou il y ai plusieurs prénoms séparé par des virgules */
            String prenoms = requeteGetPrenoms(nom,prenom,bdd);
            List<String> resultatRegiment = bdd.executerRequete( nomPrenomToRequeteSQL(nom,prenoms), "Regiment");
            
            List<String> resultatRegiment2 = new LinkedList<String>();
            for(int i = 0; i < resultatRegiment.size(); i++)
    		{
    			resultatRegiment2.add(resultatRegiment.get(i));
    		} 
//          engine.listToBaseDeFait(resultatRegiment, "regiment");
            engine = new Engine(fichierGenere);
    		Engine.supprimerFichier(fichierGenere);
    		if(resultatRegiment2.size() > 0)
	    	{
	    		List<String> resultatMortSoldat = new LinkedList<String>();
    			List<String> listeRegiments = requeteGetRegiments(resultatRegiment2.get(0), bdd);
    			for(int k = 0; k < listeRegiments.size(); k++)
	    		{
		    		String regiments = listeRegiments.get(k);
		    		
		    		List<String> resultatLieuAttaques = new LinkedList<String>();
		    		List<String> resultatDateFinAttaques = new LinkedList<String>();
		    		List<String> resultatDateDebutAttaques = new LinkedList<String>();    		
		    		List<String> resultatRequete = new LinkedList<String>();
		    		
		    		resultatRequete = bdd.executerRequete
		    				( 
		    				"SELECT Date_deces FROM soldat WHERE Nom = \""+nom+"\" and Prenom = \""+prenom+"\";",
		    				"Date_deces");
		    		for(int i = 0; i < resultatRequete.size(); i++)
		    		{
		    			resultatMortSoldat.add(resultatRequete.get(i));
		    		}    
		    		
		    		
//		    		Engine.listMortToBaseDeFait(resultatMortSoldat, "mort");
		    		
		    		resultatRequete = bdd.executerRequete
		    				( 
		    				"SELECT lieu FROM attaques WHERE regiments = \""+regiments+"\";",
		    				"lieu");
		    		for(int i = 0; i < resultatRequete.size(); i++)
		    		{
		    			resultatLieuAttaques.add(resultatRequete.get(i));
		    		}
		
		    		resultatRequete	= bdd.executerRequete
		    				( 
		    				"SELECT date_fin FROM attaques WHERE regiments = \""+regiments+"\";",
		    				"date_fin");
		    		for(int i = 0; i < resultatRequete.size(); i++)
		    		{
		    			resultatDateFinAttaques.add(resultatRequete.get(i));
		    		}
		    		
		    		resultatRequete = bdd.executerRequete
		    				( 
		    				"SELECT date_debut FROM attaques WHERE regiments = \""+regiments+"\";",
		    				"date_debut");
		    		for(int i = 0; i < resultatRequete.size(); i++)
		    		{
		    			resultatDateDebutAttaques.add(resultatRequete.get(i));
		    		}
		    		    		
		    		Engine.triListToBaseDeFait
		    				(
		    				resultatLieuAttaques,
		    				resultatDateFinAttaques,
		    				resultatDateDebutAttaques,
		    				"attaque");
	    		}
    			Engine.listMortToBaseDeFait(resultatMortSoldat, "mort");
	    	}
    		Engine.ajouterRegles(fichierRegles, fichierGenere);

    		String resultat = "";
    		Variable L = new Variable();
    		Term[] args = new Term[]
			{
    			L
			};
    		
    		
    		engine.consultFichierProlog(fichierGenere);
    		resultat = Engine.test_1("lieuMort", args);

    		//TODO poser la question avec prolog et ajouter les regles

            /* Récupération des données du résultat de la requête de lecture */
    		System.out.println("Resultat : "+resultat);
    		
    		if(resultat != null)
    			writer.write(resultat);

            
            writer.flush();
            writer.close();
 
 
 
        } 
        catch (IOException e)
        {
 
 
            try
            {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().print(e.getMessage());
                response.getWriter().close();
            } 
            catch (IOException ioe)
            {
            }
        }   
	}
}
