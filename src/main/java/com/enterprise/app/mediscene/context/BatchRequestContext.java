/**
 * 
 */
package com.sos.app.mediscene.context;

import com.sos.app.mediscene.parser.XMLParser;

/**
 * Context Object that stores the batch specific attributes.
 * All the fields should be immutable except 'status'. 
 * @author Kaniska_Mandal
 */
public final class BatchRequestContext {
	
	// stores the 'entire data stream' received from the Biz Apps 
	private String requestXML;
	// stores the type of entity which is same as the parent element name
	private final String entityType = "Undefined";
	// stores the app id 
	private final int appid = -1;
	// stores the current Batch Number which binds all the requests into a logical group
	private int batchid = -1;
	// stores the id of the tenant from which the request is received
	//private final int tenantid;
	// stores the table name 
	private final String tablename;
	// stores the status of the current batch
	// if any error occurs during processing the request, the status field can be changed to 
	private int status =  org.apache.commons.httpclient.HttpStatus.SC_ACCEPTED;
	
	private XMLParser customXMLParser;

	public BatchRequestContext(String requestXML) {
		this.requestXML = requestXML;
		this.customXMLParser = new XMLParser(requestXML);
		//this.entityType = customXMLParser.getParentRecord();
		//this.appid = customXMLParser.getAppId();
		//this.batchid = Integer.parseInt(customXMLParser.getBatchId());
		//this.tenantid = SecurityHelper.getTenantId();
		this.tablename = customXMLParser.getTableName();
	}

	/**
	 * @return the requestXML
	 */
	public String getRequestXML() {
		return requestXML;
	}


	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the customXMLParser
	 */
	public XMLParser getcustomXMLParser() {
		return customXMLParser;
	}


	/**
	 * @return the batchid
	 */
	public int getBatchid() {
		return batchid;
	}

	/**
	 * @return the appid
	 */
	public int getAppid() {
		return appid;
	}

	/**
	 * @return the tablename
	 */
	public String getTablename() {
		return tablename;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType() {
		return entityType;
	}
	
	/**
	 * @param batchid the batchid to set
	 */
	public void setBatchid(int batchid) {
		this.batchid = batchid;
	}

	public void cleanup(){
		this.requestXML = null;		
		this.customXMLParser.cleanup();
	}

}
