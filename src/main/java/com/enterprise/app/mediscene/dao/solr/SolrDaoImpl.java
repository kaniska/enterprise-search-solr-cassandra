package com.enterprise.app.mediscene.dao.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.dom4j.Element;
import org.dom4j.Node;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.enterprise.app.mediscene.model.BusinessEntity;
import com.enterprise.app.mediscene.model.SearchCriteria;

@Component("solrDao")
public class SolrDaoImpl implements SolrDao{

private @Value("#{envProps['solr.base.url']}") String SOLR_HOST_URL;
	
	private  String SOLR_UPDATE_URL = null;
	private  String SOLR_COMMIT_URL = null;
	private  String SOLR_SEARCH_URL = null;
    private HttpSolrServer SOLR_SERVER  = null;
	private Log logger = LogFactory.getLog(getClass());

    
	@PostConstruct
    public void initProps() 
    {
        //System.out.println("Method customInit() invoked...");
		SOLR_UPDATE_URL = SOLR_HOST_URL.concat("collection1/update");
		SOLR_COMMIT_URL = SOLR_HOST_URL.concat("update");
		SOLR_SEARCH_URL = SOLR_HOST_URL.concat("collection1");
		SOLR_SERVER = new HttpSolrServer(SOLR_SEARCH_URL);
    }
     
    @PreDestroy
    public void destroyResources() 
    {
        //System.out.println("Method customDestroy() invoked...");
    }
	
	/**
	 * 
	 * @param docs
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void indexDocuments(List<SolrInputDocument> docs)
			throws ClientProtocolException, IOException {
		try {
			for (SolrInputDocument sd : docs) {
				
				String xmlDoc = new String(
						("<add>" + ClientUtils.toXML(sd) + "</add>\n")
								.getBytes("UTF-8"));
				
				//deleteExistingDocumentsById(sd.getFieldValue("id").toString());
				//deleteExistingDocumentsByName(sd.getFieldValue("name").toString());
				
				insertDocIntoSolr(xmlDoc);
				//System.out.println(doc);
				//System.out.println(response.getEntity().toString());
			}
			Request.Post(SOLR_COMMIT_URL).useExpectContinue().bodyString("<commit/>", ContentType.TEXT_XML).execute().returnResponse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void insertDocIntoSolr(String xmlDoc) throws IOException,
			ClientProtocolException {
		HttpResponse response = Request.Post(SOLR_UPDATE_URL).useExpectContinue()
				.bodyString(xmlDoc, ContentType.APPLICATION_XML)
				.execute().returnResponse();
		
		//System.out.println(response);
	}
	
	/**
	 * 
	 * @param id
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void deleteExistingDocumentsById(String idVal) throws ClientProtocolException, IOException {
		String xmlDoc = new String("<delete><id>" + idVal+"</id></delete>");
	    Request.Post(SOLR_UPDATE_URL).useExpectContinue()
				.bodyString(xmlDoc, ContentType.TEXT_XML)
				.execute().returnResponse();
	    
	    Request.Post(SOLR_COMMIT_URL).useExpectContinue().bodyString("<commit/>", ContentType.TEXT_XML).execute().returnResponse();
	}
	
	/**
	 * 
	 * @param id
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void deleteExistingDocumentsByName(String nameVal) throws ClientProtocolException, IOException {
		String xmlDoc = new String("<delete><query>name:" + nameVal+"</query></delete>");
	    Request.Post(SOLR_UPDATE_URL).useExpectContinue()
				.bodyString(xmlDoc, ContentType.TEXT_XML)
				.execute().returnResponse();
	    
	    Request.Post(SOLR_COMMIT_URL).useExpectContinue().bodyString("<commit/>", ContentType.TEXT_XML).execute().returnResponse();
	}
	

	/**
	 * 
	 * @param list
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void createSolrDocumentsFromXMLNodes(List<Node> list, String entityType)
			throws Exception {
		List<SolrInputDocument> solrDocList = findModelElements(list);	
		indexDocuments(solrDocList);
	}
	

	/**
	 * @param list
	 * @return
	 */
	private List<SolrInputDocument> findModelElements(List<Node> list) {
		List<SolrInputDocument> solrDocList = new ArrayList<SolrInputDocument>(
				1);

		Iterator<?> iter = list.iterator();
		//
		while (iter.hasNext()) {
			Element elm = (Element) iter.next();

			List<Element> childelms = elm.elements(); // children
			Iterator<?> subiter = childelms.iterator();

			while (subiter.hasNext()) {
				
				SolrInputDocument sd = new SolrInputDocument();
				
				Element subelm = (Element) subiter.next();

				// Traverse one level down the hierarchy
				List<Element> grandchild_elems = subelm.elements(); // children
				if (grandchild_elems != null && grandchild_elems.size() > 1) {
					Iterator<?> nestediter = grandchild_elems.iterator();
					while (nestediter.hasNext()) {
						Element grandelm = (Element) nestediter.next();
						if(!sd.containsKey(grandelm.getName())) {
							// Traverse second level deep 
							// Traverse one level down the hierarchy
							List<Element> superGrandchild_elems = grandelm.elements(); // super_grand_children
							if (superGrandchild_elems != null && superGrandchild_elems.size() > 1) {
								Iterator<?> deepNestedIter = superGrandchild_elems.iterator();
								while (deepNestedIter.hasNext()) {
									Element superGrandElm = (Element) deepNestedIter.next();
									if(!sd.containsKey(superGrandElm.getName())) {
									 sd.addField(superGrandElm.getName(),  (String) superGrandElm.getData());
									}
								}
							} else {
								sd.addField(grandelm.getName(), grandelm.getData());
							}
						}
					} // traverse all attributes
				} 
				String idVal = sd.getFieldValue("name").toString()
						.replace(" ", "_").replace(" ", "").trim();
				sd.addField("id", idVal);

				solrDocList.add(sd);
			} // traverse all entities
		}
		return solrDocList;
	}

	@Override
	@Deprecated
	public List<BusinessEntity> searchSolrDocuments(SearchCriteria criteria) throws Exception {
		List<BusinessEntity> viewDataList = new ArrayList<BusinessEntity>(1);
		
		logger.debug("Retrieving all Business Entities");
		
		SolrQuery query = new SolrQuery();
	    //  query.setQuery( "indication:*skin*" );
		criteria.getSearchFilter().replace("*", "");
		StringBuilder sb = new StringBuilder();
		sb.append(criteria.getFieldName());
		sb.append(":*");
		if(!criteria.getSearchFilter().trim().equals("")) {
			sb.append(criteria.getSearchFilter());
			sb.append("*");
		}
		
		query.setQuery(sb.toString());
		//query.setFields(fields);
	    query.setHighlight(true);
	    query.setHighlightSimplePre("<div style='background-color: #FFFF00'>");
	    query.setHighlightSimplePost("</div>");
	    //query.setHighlightSnippets(1);
	    
	    QueryResponse rsp = SOLR_SERVER.query( query );
	    //List<GenericsModel> resultSet = rsp.getBeans(GenericsModel.class);
	    SolrDocumentList resultSet = rsp.getResults();
	    
		for (SolrDocument responsedeData : resultSet) {
			List<String> responseDataList = new ArrayList<String>(1);
			String name = responsedeData.getFieldValue("name").toString();
			responseDataList.add(name);
			responseDataList.add(responsedeData.getFieldValue(criteria.getFieldName()).toString());

			BusinessEntity businessEntity = new BusinessEntity(responseDataList);
			viewDataList.add(businessEntity);
		}

		return viewDataList;

	}
	
	@Override
	public JSONObject searchWithCustomHandler(SearchCriteria criteria) throws Exception {
		
		SolrQuery query = new SolrQuery();
	    //  query.setQuery( "indication:*skin*" );
		query.setQuery("indication:*.*");
	    query.set(criteria.getCustomHandler());
	    
	    QueryResponse rsp = SOLR_SERVER.query( query );
	    //List<GenericsModel> resultSet = rsp.getBeans(GenericsModel.class);
	    SolrDocumentList resultSet = rsp.getResults();
	    
	    
		 JSONObject entityList = new JSONObject();
		    
		for (SolrDocument responsedeData : resultSet) {
			
			JSONObject entityObj = new JSONObject();
			if(!entityObj.has("name")) {
				String name = responsedeData.getFieldValue("name").toString();
				entityObj.put("name", name);
			}
			entityObj.put(criteria.getFieldName(), responsedeData.getFieldValue(criteria.getFieldName()).toString());

			entityList.put("entity", entityObj);
		}

		return entityList;	
	}

	@Override
	public JSONObject searchSolr(SearchCriteria criteria) throws Exception {
       logger.debug("Retrieving all Business Entities");
		
		SolrQuery query = new SolrQuery();
	    //  query.setQuery( "indication:*skin*" );
		criteria.getSearchFilter().replace("*", "");
		StringBuilder sb = new StringBuilder();
		sb.append(criteria.getFieldName());
		sb.append(":*");
		if(!criteria.getSearchFilter().trim().equals("")) {
			sb.append(criteria.getSearchFilter());
			sb.append("*");
		}
		
		query.setQuery(sb.toString());
		//query.setFields(fields);
	    query.setHighlight(true);
	    query.setHighlightSimplePre("<div style='background-color: #FFFF00'>");
	    query.setHighlightSimplePost("</div>");
	    //query.setHighlightSnippets(1);
	    
	    QueryResponse rsp = SOLR_SERVER.query( query );
	    //List<GenericsModel> resultSet = rsp.getBeans(GenericsModel.class);
	    SolrDocumentList resultSet = rsp.getResults();
	    
	    JSONObject entityList = new JSONObject();
	    
		for (SolrDocument responsedeData : resultSet) {
			
			JSONObject entityObj = new JSONObject();
			if(!entityObj.has("name")) {
				String name = responsedeData.getFieldValue("name").toString();
				entityObj.put("name", name);
			}
			entityObj.put(criteria.getFieldName(), responsedeData.getFieldValue(criteria.getFieldName()).toString());

			entityList.put("entity", entityObj);
		}

		return entityList;
	}

}
