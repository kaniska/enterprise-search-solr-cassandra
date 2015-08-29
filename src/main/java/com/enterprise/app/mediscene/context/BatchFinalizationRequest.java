/**
 * 
 */
package com.enterprise.app.mediscene.context;

import java.io.Serializable;

/**
 * @author Kaniska_Mandal
 */
public final class BatchFinalizationRequest implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int batchid;
	private int appid;
	private String status;
	
	private String batchtimeend;
	private String batchtimestart;
	
	
	
	/**
	 * @return the batchid
	 */
	public int getBatchid() {
		return batchid;
	}
	/**
	 * @param batchid the batchid to set
	 */
	public void setBatchid(int batchid) {
		this.batchid = batchid;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the appid
	 */
	public int getAppid() {
		return appid;
	}
	/**
	 * @param appid the appid to set
	 */
	public void setAppid(int appid) {
		this.appid = appid;
	}
	/**
	 * @return the batchtimeend
	 */
	public String getBatchtimeend() {
		return batchtimeend;
	}
	/**
	 * @param batchtimeend the batchtimeend to set
	 */
	public void setBatchtimeend(String batchtimeend) {
		this.batchtimeend = batchtimeend;
	}
	/**
	 * @return the batchtimestart
	 */
	public String getBatchtimestart() {
		return batchtimestart;
	}
	/**
	 * @param batchtimestart the batchtimestart to set
	 */
	public void setBatchtimestart(String batchtimestart) {
		this.batchtimestart = batchtimestart;
	}
	

}
