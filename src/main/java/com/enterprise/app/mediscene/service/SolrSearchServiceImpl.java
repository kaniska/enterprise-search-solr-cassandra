package com.sos.app.mediscene.service;

import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sos.app.mediscene.dao.solr.SolrDao;
import com.sos.app.mediscene.model.BusinessEntity;
import com.sos.app.mediscene.model.SearchCriteria;

@Component("solrSearchService")
public class SolrSearchServiceImpl implements SolrSearchService {

	@Autowired
	private SolrDao solrDao;

	/**
	 * @throws Exception
	 */
	@Override
	@Deprecated
	public List<BusinessEntity> searchData(SearchCriteria criteria)
			throws Exception {
		return solrDao.searchSolrDocuments(criteria);
	}

	@Override
	public String searchSolr(SearchCriteria criteria) throws Exception {
		return solrDao.searchSolr(criteria).toString();
	}

	@Override
	public String searchWithCustomHandler(SearchCriteria criteria) throws Exception {
		return solrDao.searchWithCustomHandler(criteria).toString();
	}
}
