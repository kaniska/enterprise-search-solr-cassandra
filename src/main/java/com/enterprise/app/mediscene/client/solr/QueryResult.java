package com.enterprise.app.mediscene.client.solr;

import java.util.List;

import org.json.JSONObject;

public class QueryResult {
	
	private long totalMatches = 0;
	private long startMatch = 0;
	private long endMatch = 0;
	private List<Document> documents = null;
	private List<Facet> facets = null;

	public long getTotalMatches() {
		return totalMatches;
	}
	public void setTotalMatches(long totalMatches) {
		this.totalMatches = totalMatches;
	}
	public List<Document> getDocuments() {
		return documents;
	}
	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	@Override
	public String toString() {
		String documentsString = "";
		for (Document d : documents){
			documentsString += d;
		}
		return "QUERYRESULTS:\n\nDOCUMENTS:\n" + documentsString + "\nTOTALMATCHES: "
				+ totalMatches + "\nFACETS: " + facets + "\n";
	}
	public long getStartMatch() {
		return startMatch;
	}
	public void setStartMatch(long startMatch) {
		this.startMatch = startMatch;
	}
	public long getEndMatch() {
		return endMatch;
	}
	public void setEndMatch(long endMatch) {
		this.endMatch = endMatch;
	}
	public List<Facet> getFacets() {
		return facets;
	}
	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}
	
	public JSONObject toJSON(){
		
		JSONObject obj = new JSONObject();
		
		obj.put("totalMatches", totalMatches);
		obj.put("startMatch", startMatch);
		obj.put("endMatch", endMatch);
		for (Document d : documents){
			obj.accumulate("documents", d.toJSON());
		}
		
		return obj;
		
	}

}
