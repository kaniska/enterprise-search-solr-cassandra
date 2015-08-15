package com.sos.app.mediscene.service;

import java.util.List;

import com.sos.app.mediscene.model.BusinessEntity;
import com.sos.app.mediscene.model.SearchCriteria;

public interface SolrSearchService {

	@Deprecated
	public List<BusinessEntity> searchData(SearchCriteria criteria)
			throws Exception;
	
	public String searchSolr(SearchCriteria criteria)
			throws Exception;

	public String searchWithCustomHandler(SearchCriteria criteria)
			throws Exception;
	
}
