package com.enterprise.app.mediscene.client.solr;

import java.util.List;

public class Breadcrumb {
	
	private String query;
	private List<String> facetQueries;
	private long count;

	public Breadcrumb(String query, List<String> facetQueries, long count) {
		super();
		this.query = query;
		this.count = count;
		this.facetQueries = facetQueries;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public List<String> getFacetQueries() {
		return facetQueries;
	}
	public void setFacetQueries(List<String> facetQueries) {
		this.facetQueries = facetQueries;
	}

}
