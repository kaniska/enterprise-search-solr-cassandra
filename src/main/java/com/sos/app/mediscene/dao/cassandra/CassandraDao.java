package com.sos.app.mediscene.dao.cassandra;

import java.util.List;
import java.util.Map;

import org.dom4j.Node;

public interface CassandraDao {
	
	public void createDBDataFromXMLNodes(List<Node> list, String columnFamilyName, int batchId) throws Exception;
	
}
