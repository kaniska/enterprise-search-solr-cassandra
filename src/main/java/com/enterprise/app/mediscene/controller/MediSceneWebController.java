package com.enterprise.app.mediscene.controller;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.enterprise.app.mediscene.context.BatchRequestContext;
import com.enterprise.app.mediscene.model.BusinessEntity;
import com.enterprise.app.mediscene.model.SearchCriteria;
import com.enterprise.app.mediscene.service.BatchRequestService;
import com.enterprise.app.mediscene.service.SolrSearchService;

@Controller
public class MediSceneWebController {

	@Inject
	public SolrSearchService solrSearchService;
	@Inject
	private BatchRequestService batchRequestService;
	
	private @Value("#{envProps['mobileapp.apiToken']}") String MOBILE_APP_API_TOKEN;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showForm(Model model) throws Exception {
		SearchCriteria searchCriteria = new SearchCriteria();
		searchCriteria.setPage(5);
		searchCriteria.setPageSize(50);
		searchCriteria.setSearchFilter("*");
		
		searchCriteria.setFieldName("indication");
		searchCriteria.setApiToken(MOBILE_APP_API_TOKEN);
		//searchCriteria.setVersionNumber(monitoringService.getListOfVersions().get(0));		
		 // List<BusinessEntity> entityList = monitoringService.findEntityAttributeList(searchCriteria);
		 // List<String> entityAttributeList =monitoringService.findAttributeList(searchCriteria);
		
		model.addAttribute("columnLabelList", getColumnLabels(searchCriteria));
		
	List<BusinessEntity> responseDataList = new ArrayList<BusinessEntity>(1);
		try {
			responseDataList = solrSearchService.searchData(searchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("entityList", responseDataList);
		//model.addAttribute("versionNumberList", solrSearchService.getListOfVersions());
		
		model.addAttribute("searchCriteria", searchCriteria); // adding in
																// model
		return "home";
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public String list(SearchCriteria searchCriteria, Model model) throws Exception {
		
		/**
		 * Authenticate, if the requesting client has api token or not.
		 */
		if(null==searchCriteria.getApiToken() || !MOBILE_APP_API_TOKEN.equals(searchCriteria.getApiToken())){
			return "invalidApiToken";
		}
		
		model.addAttribute("columnLabelList", getColumnLabels(searchCriteria));
		
		List<BusinessEntity> responseDataList = new ArrayList<BusinessEntity>(1);
		try {
			responseDataList = solrSearchService.searchData(searchCriteria);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		model.addAttribute("entityList", responseDataList);
		//model.addAttribute("versionNumberList", monitoringService.getListOfVersions());
		model.addAttribute("searchCriteria", searchCriteria);
		
		return "home"; // users/list
	}
	
	/*@RequestMapping(value = "/api/search", method = RequestMethod.GET, consumes=MediaType.TEXT_PLAIN_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity findEntity(@RequestBody String query) throws Exception {
		String solrResponseData = null;
		ResponseEntity<String> response = new ResponseEntity<String>( org.springframework.http.HttpStatus.OK );
		try {
			Gson gson = new GsonBuilder().create();
			SearchCriteria searchCriteria = gson.fromJson(query, SearchCriteria.class);

			solrResponseData = solrSearchService.searchSolr(searchCriteria);
			response = new ResponseEntity<String>(solrResponseData,org.springframework.http.HttpStatus.ACCEPTED);		
		} catch (Exception e) {
			response = new ResponseEntity<String> (e.getMessage(), org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
		}
		
		return response;
	}*/
	
	@RequestMapping(value = "/api/search", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> findEntity(@RequestParam("fieldName") String fieldName, @RequestParam("searchFilter") String searchFilter,@RequestParam("apiToken") String apiToken) throws Exception {
		String solrResponseData = null;
		//HttpHeaders headers = addAccessControllAllowOrigin();

		ResponseEntity<String> response = new ResponseEntity<String>( org.springframework.http.HttpStatus.OK );
		
		/**
		 * Authenticate, if the requesting client has api token or not.
		 */
		if(null==apiToken || !MOBILE_APP_API_TOKEN.equals(apiToken)){
			return new ResponseEntity<String>("INVALID API TOKEN "+apiToken, org.springframework.http.HttpStatus.OK );
		}
		
		try {
			SearchCriteria searchCriteria = new SearchCriteria();
			searchCriteria.setFieldName(fieldName);
			searchCriteria.setSearchFilter(searchFilter);
			searchCriteria.setApiToken(apiToken);

			solrResponseData = solrSearchService.searchSolr(searchCriteria);
			response = new ResponseEntity<String>(solrResponseData,org.springframework.http.HttpStatus.ACCEPTED);		
		} catch (Exception e) {
			response = new ResponseEntity<String> (e.getMessage(),  org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
		}
		
		return response;
	}

	@RequestMapping(value = "/api/add", method = RequestMethod.POST , consumes="text/plain", produces="text/xml")
	public ResponseEntity<String> addEntity(@RequestBody String requestXML)  {

		BatchRequestContext requestContext = null;
		String message = "";
		ResponseEntity<String> response = new ResponseEntity<String>( org.springframework.http.HttpStatus.OK );
		try {
			requestContext = new BatchRequestContext(requestXML);
			batchRequestService.handleBatchInsertion(requestContext);
			//response = new ResponseEntity<String> (message, org.springframework.http.HttpStatus.OK);
			//TODO send a custom Response XML			
		} catch (Exception e) {
			message = logError(e, requestContext);
			response = new ResponseEntity<String> (message, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
		} finally {
			requestContext.cleanup();
			requestContext = null;
		}
		
		return response;
	}
	
	/**
	 * Playing a trick here : Do not set the server error code in Http header.
	 * Then the Http Client will error out and exit the process immediately.
	 * Set a normal code (202) as Response header and set the actual server error code 
	 * along with Error log inside the Response Data
	 * 
	 * @param e
	 * @param requestContext
	 * @return
	 */
	private String logError(Exception e, BatchRequestContext requestContext) {
		int errorCode = 0;
		SQLException sqlEx = null;
		StringBuilder errorMessage = new StringBuilder();
		errorMessage.append(e.getMessage());
		if (e instanceof SQLException) {
			sqlEx = (SQLException)e;
			errorCode = ((SQLException) e).getErrorCode();
		    Iterator<Throwable> iter = sqlEx.iterator();
		    int i = 1;
		    while(iter.hasNext()) {
		    	sqlEx = (SQLException) iter.next();
		    	errorMessage.append("\n Message ");
		    	errorMessage.append(i++);
		    	errorMessage.append(" -- ");
		    	errorMessage.append(sqlEx);
		    	
		    	break;
		    }
		}else{
			
		}
		try {
			batchRequestService.logInProcessError(requestContext, errorCode
					+ "", errorMessage.toString());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			requestContext.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
		}
		return e.getMessage();
	}

	
	private static Map<String, String> labelMap = new HashMap<String, String>(1);
	static {
		labelMap.put("indication", "Indications");
		labelMap.put("generic_name", "Generic Category");
		labelMap.put("contra_indication", "Contra-Indication");
		labelMap.put("side_effect", "Side Effects");
		labelMap.put("caution", "Caution");
		labelMap.put("symptom", "Symptoms");
	}
	
	private List<String> getColumnLabels(SearchCriteria searchCriteria){
		List<String> columnLabelList = new ArrayList<String>(1);
		columnLabelList.add("Name");
		columnLabelList.add(labelMap.get(searchCriteria.getFieldName()));
		return columnLabelList;
	}
	
	private HttpHeaders addAccessControllAllowOrigin() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        return headers;
    }
	
}
