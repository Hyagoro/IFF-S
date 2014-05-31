package jdbc;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mysql.jdbc.Connection;

public class IffJDBC 
{
	private List<String> resultat = new ArrayList<String>();
	
	/* Connexion à la base de données */
	String url = "jdbc:mysql://localhost:3306/iff";
	String utilisateur = "root";
	String motDePasse = "";
	Connection connexion = null;
	Statement statement = null;
	
	

    public List<String> executerRequete(String requete, String attributRetourne) 
    {
    	ResultSet result = null;
    	try
    	{
    	    connexion = (Connection) DriverManager.getConnection( url, utilisateur, motDePasse );
			statement = connexion.createStatement();    	    
    	    result = statement.executeQuery( requete );

    	    resultat.clear();
    	    while ( result.next() ) 
    	    {
    	        resultat.add(result.getString( attributRetourne ));
    	    }
    	}
    	catch ( SQLException e ) 
    	{
    		System.err.println("Requête mal formée : "+requete);
    		//e.printStackTrace();
    	}
    	finally 
    	{
    		if ( result != null ) 
    		{
    	        try 
    	        {
       	            result.close();
    	        } catch ( SQLException e ) {}
    	    }
    	    if ( statement != null ) 
    	    {
    	        try 
    	        {
    	            statement.close();
    	        } catch ( SQLException ignore ) {}
    	    }
    	    if ( connexion != null ) 
    	    {
    	        try 
    	        {
    	            connexion.close();
    	        } catch ( SQLException ignore ) {}
    	    }
    	}
    	
        return resultat;
    }
}
