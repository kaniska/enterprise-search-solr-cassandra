package com.enterprise.app.mediscene.client.solr;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONObject;

public class Document {

	private List<DocumentText> text;
	private String id = "";
	private String url = "";
	private long time = 0L;
	private String title = "";
	private Double score = -1.0;
	private List<Facet> facets = new LinkedList<Facet>();
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public String toString() {

		String snippetsString = "";
		if (snippets != null) {
			for (String s : snippets) {
				snippetsString += "\t\tSNIPPET: " + s + "\n";
			}
		}
		
		String textString = "";
		if (this.text != null){
			for (DocumentText textS : this.text){
				textString += "\t\t" + textS.getName() + " : " + textS.getText() + "\n";
			}
		}
		
		String docFacets = "";
		if (this.facets != null){
			for (Facet f : this.facets){
				docFacets += f.toString();
			}
		}

		return "\tID: " + id + "\n\tTITLE: " + title + "\n\tSNIPS:\n" + snippetsString + "\tTEXT: "
				+ textString + "\n\tTIME: " + time + "\n\tSCORE: " + score + "\n\tFACETS:\n" + docFacets + "\n\t---\n";
	}

	private List<String> snippets = new LinkedList<String>();

	public List<DocumentText> getText() {
		return text;
	}

	public void setText(List<DocumentText> text) {
		this.text = text;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getSnippets() {
		return snippets;
	}

	public void setSnippets(List<String> snippets) {
		this.snippets = snippets;
	}

	public Double getScore() {
		return score;
	}

	public void setScore(Double score) {
		this.score = score;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public List<Facet> getFacets() {
		return facets;
	}

	public void setFacets(List<Facet> facets) {
		this.facets = facets;
	}

	public JSONObject toJSON(){
		
		JSONObject obj = new JSONObject();
		obj.put("url", url);
		obj.put("score", score);
		obj.put("id", id);
		obj.put("title", title);
		
		for (DocumentText dt : text){
			obj.accumulate("texts", dt.toJSON());
		}
		
		for (Facet f : facets){
			obj.accumulate("facets", f.toJSON());
		}
		
		return obj;
		
	}
	
}
