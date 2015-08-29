package com.enterprise.app.mediscene.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * A backing bean for the main hotel search form. Encapsulates the criteria
 * needed to perform a hotel search.
 */
@XmlRootElement
public class SearchCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The user-provided search criteria for finding Hotels.
	 */
	private String searchFilter;
	
	private String versionNumber;
	
	/**
	 * The current page of the Hotel result list.
	 */
	private int page;

	private int pageSize;
	
	private String fieldName;
	
	private String apiToken;
	
	/**
	 * Custom handler name
	 */
	private String customHandler;
	
	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private String startDate;

	@DateTimeFormat(pattern = "MM-dd-yyyy")
	private String endDate;

	@XmlAttribute
	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	@XmlAttribute
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@XmlAttribute
	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String datetime) {
		this.startDate = datetime;
	}


	@XmlAttribute
	public String getSearchFilter() {
		return searchFilter;
	}

	public void setSearchFilter(String searchFilter) {
		this.searchFilter = searchFilter;
	}

	/**
	 * @return the page
	 */
	public int getPage() {
		return page;
	}

	/**
	 * @param page the page to set
	 */
	public void setPage(int page) {
		this.page = page;
	}

	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}

	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@XmlAttribute
	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String p_versionNumber) {
		this.versionNumber = p_versionNumber;
	}

	public String getCustomHandler() {
		return customHandler;
	}

	public void setCustomHandler(String customHandler) {
		this.customHandler = customHandler;
	}

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}
	
}
