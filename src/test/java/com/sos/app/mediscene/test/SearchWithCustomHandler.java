/**
 * 
 */
package com.enterprise.app.mediscene.test;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enterprise.app.mediscene.model.SearchCriteria;
import com.enterprise.app.mediscene.service.SolrSearchService;
 

/**
 * @author kaniska_mac
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:META-INF/root-context.xml"})
public class SearchWithCustomHandler {
	
	@Autowired
    private SolrSearchService solrSearchService;
 
    
    /**
     * 
     * Add following custom search handler to solarconfig.xml before running this test case.
     * 
     <requestHandler name="/medis" class="solr.SearchHandler">
    <!-- default values for query parameters can be specified, these
         will be overridden by parameters in the request
      -->
     <lst name="defaults">
       <str name="fl">name,indication,contra_indication,side_effect</str>
       <int name="qf">5</int>
       <str name="wt">json</str>
       <str name="df">edismax</str>
     </lst>
  </requestHandler>
     */
	
    @Test
    public void testInvokingCustomSearchHandler() throws Exception {
    	System.out.println("<--------------------> invoking custom search handler <-------------------->");
    	SearchCriteria criteria = new SearchCriteria();
    	criteria.setCustomHandler("medis");
    	criteria.setSearchFilter("skin");
    	invokeGivenCriteria(criteria);
    }
    
    private void invokeGivenCriteria(SearchCriteria criteria) throws Exception {
    	String jsonResponse = solrSearchService.searchWithCustomHandler(criteria);
    	Assert.assertFalse(jsonResponse.isEmpty());
    	System.out.println(jsonResponse);
    }
}
