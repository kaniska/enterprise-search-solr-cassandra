package com.enterprise.app.mediscene.client.cassandra;

	
	import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
	  
import java.util.Set;
import java.util.UUID;

	import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Ordering;
import com.datastax.driver.core.querybuilder.QueryBuilder;

	public class CassandraClient { 
	        
	    /**
	     *      
	     * @param session
	     * @param name
	     * @param description
	     * @param indications
	     * @param contra_indications
	     * @param side_effect
	     */
		private static void insertGeneric(Session session, String name, String description,
	                       String indications_text, String contra_indications_text, String side_effect) {
	        	 
	                BatchStatement batch = new BatchStatement();
	                
	                
	                PreparedStatement genericsData = session.prepare("INSERT INTO MediScene.Generics "
	                		+ "(generic_id, name, description, indictions, contra_indictions) VALUES (?,?,?,?,?);");
	                Set<String>  indications = new HashSet<String>(1);
	                indications.add(indications_text);
	                
	                Set<String>  contra_indications = new HashSet<String>(1);
	                contra_indications.add(contra_indications_text);
	                
	                batch.add(genericsData.bind(UUID.randomUUID(), name, 
	                		description, indications, contra_indications));
	                
	                session.execute(batch);
	         }
	        
	         private static void getGenericByName(Session session, String name){
	                PreparedStatement ps = session.prepare("SELECT * FROM Generics WHERE name=? ALLOW FILTERING;");
	                ResultSet result = session.execute(ps.bind(name).setFetchSize(100));
	                for(Row row : result){
	                	System.out.println("Got the data >>> ");
	                	System.out.println(row.getString(1));
	                }
	         }
	        
	         /**
	          * 
	          * @param args
	          */
	         public static void main(String[] args) {
	              Cluster cluster = Cluster.builder().addContactPoint("localhost").build();
	              Session session = cluster.connect("MediScene");
	           /**
	              insertGeneric(session, "Avil", "Generics - Avil",
	            		  "In combination with other antiretroviral agents for the treatment of HIV-1 infections.", 
	            		  "It has been associated with fatal hypersensitivity reckoners hence should not be restarted after any hypersensitivity reaction occuring with abacavir.",
	            		  "Hypersensitivity reactions (fever, skin, rash, fatique, nausea, vomiting, diarrhoea or abdominal pain pharyngitis, dyspnoea or cough)");
	              **/
	            //  getGenericByName(session, "Avil");
	              
	             System.out.println("Column Type :"+ ("contra_indictions"));
	              
	              session.close();
	              cluster.close();
	       }
	      
	}