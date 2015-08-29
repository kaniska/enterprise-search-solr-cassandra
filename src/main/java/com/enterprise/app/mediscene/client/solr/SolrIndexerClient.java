package com.enterprise.app.mediscene.client.solr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.enterprise.app.mediscene.dao.solr.SolrDaoImpl;
import com.enterprise.app.mediscene.parser.XMLParser;

public class SolrIndexerClient {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
				new String[] { "META-INF/root-context.xml" });
		Resource template = ctx.getResource("classpath:generics.xml");
		try {
			String genericsData = getStringFromInputStream(template
					.getInputStream());
			XMLParser customXMLParser = new XMLParser(genericsData);
			SolrDaoImpl docBuilder = new SolrDaoImpl();
			//
			docBuilder.createSolrDocumentsFromXMLNodes(customXMLParser.selectNodes("generics") , "generics");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
