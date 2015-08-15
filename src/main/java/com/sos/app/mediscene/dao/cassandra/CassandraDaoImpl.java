/**
 * 
 */
package com.sos.app.mediscene.dao.cassandra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.cassandra.utils.TimeUUIDUtils;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

import org.dom4j.Element;
import org.dom4j.Node;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;


/**
 * @author kaniska_mac
 */

@Component("cassandraDao")

public class CassandraDaoImpl implements CassandraDao {

	//@Autowired
	//private Keyspace keyspace;
	//@Autowired
	//private CassandraTemplate cassandraTemplate;
	
	private static String SET_TYPE_COLUMNS = "indications precautions side_effects contra_indications adverse_drug_reactions generics_list caution";
	
	private @Value("#{envProps['cassandra.cluster.url']}") String CASSANDRA_HOST_URL;
	private @Value("#{envProps['cassandra.keyspece.name']}") String CASSANDRA_KEYSPACE_NAME;
	
	private me.prettyprint.hector.api.Cluster cluster;
	
	@SuppressWarnings("unchecked")
	public void  createDBDataFromXMLNodes(List<Node> list, String columnFamilyName, int batchId)
			throws Exception {

		List<Map<String, Object>> recordList = new ArrayList<Map<String, Object>>(1);
		Iterator<?> iter = list.iterator();
		//
		while (iter.hasNext()) {
			Element elm = (Element) iter.next();

			List<Element> childelms = elm.elements(); // children
			Iterator<?> subiter = childelms.iterator();

			while (subiter.hasNext()) {
				Element subelm = (Element) subiter.next();
				
				Map<String, Object> nameValueMap = new HashMap<String, Object>();

				// Traverse one level down the hierarchy
				List<Element> grandchild_elems = subelm.elements(); // grand_children
				if (grandchild_elems != null && grandchild_elems.size() > 1) {
					Iterator<?> nestediter = grandchild_elems.iterator();
					while (nestediter.hasNext()) {
						Element grandelm = (Element) nestediter.next();
						// Traverse second level deep 
						// Traverse one level down the hierarchy
						List<Element> superGrandchild_elems = grandelm.elements(); // super_grand_children
						if (superGrandchild_elems != null && superGrandchild_elems.size() > 1) {
							Iterator<?> deepNestedIter = superGrandchild_elems.iterator();
							while (deepNestedIter.hasNext()) {
								Element superGrandElm = (Element) deepNestedIter.next();
								nameValueMap.put(superGrandElm.getName(), (String) superGrandElm.getData());
							}
						} else {
							nameValueMap.put(grandelm.getName(), (String) grandelm.getData());
						}
					}// traverse all attributes inside entity
				}
				recordList.add(nameValueMap);
			}// traverse all entities in the batch
		}
		
		insertData(columnFamilyName, recordList, batchId);
	}


	/**
	 * @throws HectorException
	 */
	private boolean checkIfColumnExist(String columnName ) throws HectorException {
		 this.cluster = HFactory.getOrCreateCluster(CASSANDRA_HOST_URL, 
				 new CassandraHostConfigurator(CASSANDRA_HOST_URL));
		 KeyspaceDefinition keyspaceDefinition = this.cluster.describeKeyspace("mediscene");
		 
		 List<ColumnFamilyDefinition> columnFamilyDefinitions = keyspaceDefinition.getCfDefs();
		 for (ColumnFamilyDefinition cfDef : columnFamilyDefinitions) {
			 List<ColumnDefinition> columnDefinitions =  cfDef.getColumnMetadata();
			 for (ColumnDefinition columnDefinition : columnDefinitions) {
				if(columnDefinition.getName().equals(columnName)) {
					return true;
				}
			}
		 }
		 
		 return false;
	}
	
	
	private void insertData(String columnFamilyName, List<Map<String, Object>> recordList, int batchId) throws Exception {
		
		Cluster cluster = Cluster.builder().addContactPoint(CASSANDRA_HOST_URL).build();
        Session session = cluster.connect(CASSANDRA_KEYSPACE_NAME);
        try {
		/*
		 CassandraPersistentEntity cassandraData = new CassandraPersistentEntity.Builder()
		.id(String.valueOf(UUID.randomUUID().clockSequence()))
		.keySpace(keyspace).columnFamily(columnFamilyName)
		.columnList(buildColumnsFromData(nameValueMap)).build();
		cassandraTemplate.insert(cassandraData);*/
        
        BatchStatement batch = new BatchStatement();
        //
        for (Map<String, Object> map : recordList) {
        	Insert pStmt = QueryBuilder.insertInto(CASSANDRA_KEYSPACE_NAME, columnFamilyName);
        	pStmt = pStmt.value("id", TimeUUIDUtils.getUniqueTimeUUIDinMillis());
        	pStmt = pStmt.value("batch_id", String.valueOf(batchId));
        	
        	for (String key : map.keySet()) {
        		 String colName = key;
        		 
        		 if(colName.equals("generic_name")) {
        			 colName = "generic_category"; //TODO change hard-coded approach
        		 }
        		 
        		 //if(checkIfColumnExist(colName)) {
        			 //TODO add dynamic column creation feature
        		 //}
        		 
        		 Object colVal =  map.get(key);
        	     if(colName instanceof String && SET_TYPE_COLUMNS.contains(colName) ){
        	    	 String[] valueArray = colVal.toString().split(",");
        	    	 Set<String>  colValueSet = new HashSet<String>(1);
        	    	 for (String token : valueArray) {
        	    		 colValueSet.add(token);
					 }
        	    	 pStmt =  pStmt.value(colName, colValueSet);
        	     }else{
        	    	 if(colVal != null && !colVal.toString().trim().equals("")) {
        	    		 pStmt = pStmt.value(colName, colVal);
        	    	 }
        	     }
			}
        	batch.add(pStmt);
        }     
        //////
        session.execute(batch);
        } finally {
        	session.close();
        	cluster.close();
	   }
	}
}
