package com.sos.app.mediscene.client.solr;

import org.json.JSONObject;

public class DocumentText {
	
	private String name;
	private String text;
	
	public DocumentText(String name, String text) {
		super();
		this.name = name;
		this.text = text;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "DocumentText [name=" + name + ", text=" + text + "]";
	}
	
	public JSONObject toJSON(){
		
		JSONObject obj = new JSONObject();
		obj.put("name", name);
		obj.put("text", text);
		
		return obj;
		
	}
	

}
