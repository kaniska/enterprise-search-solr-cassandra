/**
 * 
 */
package com.enterprise.app.mediscene.test;

import java.util.List;

import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.enterprise.app.mediscene.model.BusinessEntity;
import com.enterprise.app.mediscene.model.SearchCriteria;
import com.enterprise.app.mediscene.service.SolrSearchService;
 

/**
 * @author kaniska_mac
 *
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
    "classpath:META-INF/root-context.xml"})
public class BasicSearchTest {
	
	@Autowired
    private SolrSearchService solrSearchService;
 
    @Test
    public void testBasicSolrSearch() throws Exception {
 
    	SearchCriteria criteria = new SearchCriteria();
    	criteria.setSearchFilter("cancer");
    	criteria.setFieldName("side_effect");
    	invokeGivenCriteria(criteria);
    }
    
    @Test
    public void testSearchAllFields() throws Exception {
    	SearchCriteria criteria = new SearchCriteria();
    	criteria.setSearchFilter("pregnancy");
    	System.out.println("Searching all fields .. ");
    	criteria.setFieldName("side_effect");
    	invokeGivenCriteria(criteria);
    }
    
    /**
     * http://localhost:8983/solr/MediScience/select?q=indication:*cancer*%20OR%20contra_indication:*cancer*%20OR%20side_effect:*cancer*&fl=name,indication,contra_indication,side_effect&defType=edismax&bq=(indication:*skin*cancer^25.0)
     * 
     * http://stackoverflow.com/questions/12132139/searching-all-fields-in-solr-solrj
     * 
     * http://localhost:8983/solr/MediScience/select?q=name:*&defType=edismax&qf=indication^1000.5+side_effect^20&bq=indication:*cancer*^25.0
     * 
     * http://localhost:8983/solr/MediScience/select?q=name:*&defType=edismax&qf=indication^10.5+side_effect^2000&bq=side_effect:*cancer*^25.0
     * 
     * 
     */
    
    /**
     * 1) Rank by boosting fields and specific terms (Query Time)
		For example :
		Lets perform plain search to find generics with indications related to 'skin'
		> http://localhost:8983/solr/collection1/select?q=indication:*skin*

     * @throws Exception
     */
    @Test
    public void testRankByBostingFieldsSolrSearch() throws Exception {
    	System.out.println("<--------------------> Rank by boosting fields search results <-------------------->");
    	SearchCriteria criteria = new SearchCriteria();
    	criteria.setFieldName("indication");
    	criteria.setSearchFilter("skin");
    	invokeGivenCriteria(criteria);
    }
    
    private void invokeGivenCriteria(SearchCriteria criteria) throws Exception {
    	List<BusinessEntity> entityList = solrSearchService.searchData(criteria);
    	Assert.assertFalse(entityList.isEmpty());
    	for (BusinessEntity businessEntity : entityList) {
    		while(businessEntity.hasNext())
    			System.out.println(businessEntity.getNextValue());
    		System.out.println("<-------------------->");
    		// just print one record for testing
    		//break;
		}
    }
}
