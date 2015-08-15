package com.sos.app.mediscene.dao.solr;

import java.util.List;

import org.dom4j.Node;
import org.json.JSONObject;

import com.sos.app.mediscene.model.BusinessEntity;
import com.sos.app.mediscene.model.SearchCriteria;

public interface SolrDao {

	void createSolrDocumentsFromXMLNodes(List<Node> list, String entityType) throws Exception;

	@Deprecated
	List<BusinessEntity> searchSolrDocuments(SearchCriteria criteria) throws Exception ;

    JSONObject searchSolr(SearchCriteria criteria) throws Exception ;
    
    public JSONObject searchWithCustomHandler(SearchCriteria criteria) throws Exception;
}
