package com.enterprise.app.mediscene.parser;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;


/**
 * 
 */
@SuppressWarnings("rawtypes")
public class XMLParser {

	//private final Logger log = new Logger(getClass());
	SAXReader saxReader = null; // SAXReader is NOT thread safe
	Document document = null;
	String content = null;
	
	/**
	 * 
	 * @param entityXML
	 */
	public XMLParser(String entityXML) {
		
		try {
			saxReader = new SAXReader(); // SAXReader is NOT thread safe
			document = saxReader.read(new StringReader(entityXML));
			content = entityXML;
		} catch (DocumentException e) {
			//log.error(e, entityXML, null);
			System.out.println(e.getMessage());
		}
	}
	
	public void cleanup(){
		if(document != null) document.clearContent();
		saxReader = null;
		document = null;
		System.gc();
	}
	
	public List<String> getChildRecords() {
		
		//log.info("getChildren=" + entityXML);
		
		if (document == null) { // something wrong with the parsing
			return null;
		}
		
		List<String> list = new ArrayList<String>();
		StringBuilder sb = null;
			
			@SuppressWarnings("unchecked")
			Iterator<Element> itr = document.getRootElement().elementIterator();
			
			while(itr.hasNext()) {
			    Element element = itr.next(); 
			    
			    if(element.attributeCount() > 0) {
			    		Iterator<Element> subIter = element.elementIterator();
			    		 sb = new StringBuilder();
			    		sb.append(element.asXML());
			    		/*while(subIter.hasNext()){
			    			sb.append(subIter.next().asXML());
			    		}*/
			    		list.add(sb.toString());
			    }	
			} 
			
		
		return list;
	}
	
   public String getParentRecord() {
		
		if (document == null) { // something wrong with the parsing
			return null;
		}
		
			@SuppressWarnings("unchecked")
			Iterator<Element> itr = document.getRootElement().elementIterator();
			
			while(itr.hasNext()) {
			    Element element = itr.next(); 
			    
			    if(element.attributeCount() > 0) {
			    	Attribute attr = element.attribute("isParent");
			    	if(attr != null && null != attr.getValue() &&  attr.getValue().equals("true")) {
			    		return element.getName();		
			    	}
			    }	
			} 
		
			//This is for Backward compatibility
			return document.getRootElement().getName();
	}
	

	/**
	 * 
	 * @return
	 */
	public String getTableName() {

		if (document == null) { // something wrong with the parsing
			return null;
		}

		String rootName = document.getRootElement().getName();
		//log.info("rootName=" + rootName);
		return rootName;
	}

	
	public String getBatchId() {

		if (document == null) { // something wrong with the parsing
			return null;
		}
		
		List list = (List) document.selectNodes("//batchid");
		if (!list.isEmpty()) {
			org.dom4j.Node node = (Node) list.get(0);
			return node.getText();
		}
		return "-1";
	}
	
	
	public int getAppId() {

		if (document == null) { // something wrong with the parsing
			return 0;
		}
		
		List list = (List) document.selectNodes("//AppId");
		if (!list.isEmpty()) {
			org.dom4j.Node node = (Node) list.get(0);
			return Integer.parseInt(node.getText());
		}
		
		return 0;
	}

	/**
	 * 
	 * @param nodeName
	 * @return
	 */
	
	public List selectNodes(String nodeName) {

		List list = (List) document.selectNodes("//" + nodeName);
		return list;
	}

	public List getIds() {

		List list = (List) document.selectNodes("//entity_id");

		return list;

	}

}
