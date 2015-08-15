/**
 * 
 */
package com.sos.app.mediscene.context;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * This is the 'Batch Initialization Response Payload' sent by DCS to external clients.
 * DCS will allocate a 'Batch Process Id', 'Current Run Date From' and 'Current Run Date To'
 * Fetch data incrementally through consistent batches.
 * 
 * @author Kaniska_Mandal
 */
@XmlRootElement(name="BatchInfo")
public class BatchInitializationResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	private String batchid;
	private String batchtimefrom;
	private String batchtimeto;
	private String errorCondition;
	private String errorMessage;
	
	
	public BatchInitializationResponse() {
		//
	}

	/**
	 * @param batchid
	 * @param lastrundatefrom
	 * @param lastrundateto
	 * @param status
	 */
	public BatchInitializationResponse(String batchid, String batchtimefrom,
			String batchtimeto, String errorCondition, String errorMessage) {
		this.batchid = batchid;
		this.batchtimefrom = batchtimefrom;
		this.batchtimeto = batchtimeto;
		this.errorCondition = errorCondition;
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the batchid
	 */
	public String getBatchid() {
		return batchid;
	}




	/**
	 * @return the errorCondition
	 */
	public String getErrorCondition() {
		return errorCondition;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @return the batchtimefrom
	 */
	public String getBatchtimefrom() {
		return batchtimefrom;
	}

	/**
	 * @return the batchtimeto
	 */
	public String getBatchtimeto() {
		return batchtimeto;
	}

}
