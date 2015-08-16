/**
 * 
 */
package com.sos.app.mediscene.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sos.app.mediscene.context.BatchRequestContext;
import com.sos.app.mediscene.dao.solr.SolrDaoImpl;
import com.sos.app.mediscene.model.BusinessEntity;
import com.sos.app.mediscene.model.SearchCriteria;
import com.sos.app.mediscene.parser.XMLParser;
import com.sos.app.mediscene.service.BatchRequestService;
import com.sos.app.mediscene.service.SolrSearchService;
 

/**
 * @author kaniska_mac
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
"classpath:META-INF/root-context.xml"})
public class DataPersistenceTest {
	
    @Test
    public void testDataPersistence() throws Exception {
		try {
			ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
					new String[] { "META-INF/root-context.xml" });
			BatchRequestService batchRequestService = (BatchRequestService) ctx.getBean("batchRequestService");
			Resource template = ctx.getResource("classpath:drugs.xml");
			String requestXML = getStringFromInputStream(template
					.getInputStream());
			
	    	BatchRequestContext requestContext = new BatchRequestContext(requestXML);
	    	batchRequestService.handleBatchInsertion(requestContext);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
 // convert InputStream to String
 	private static String getStringFromInputStream(InputStream is) throws Exception {

 		BufferedReader br = null;
 		StringBuilder sb = new StringBuilder();

 		String line;
 		try {

 			br = new BufferedReader(new InputStreamReader(is));
 			while ((line = br.readLine()) != null) {
 				sb.append(line);
 			}

 		} catch (IOException e) {
 			e.printStackTrace();
 		} finally {
 			if (br != null) {
 				try {
 					br.close();
 				} catch (IOException e) {
 					e.printStackTrace();
 				}
 			}
 		}

 		return sb.toString();

 	}

}
