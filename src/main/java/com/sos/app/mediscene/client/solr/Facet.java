package com.sos.app.mediscene.client.solr;

import org.json.JSONObject;


public class Facet implements Comparable<Facet> {
	
	private long count = 0;
	private String name = "";
	private String classname = "";
	
	
	public Facet(long count, String name, String classname) {
		super();
		this.count = count;
		this.name = name;
		this.classname = classname;
	}
	
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Facet: " + this.classname + " [" + this.name + " (" + this.count + ")]\n";
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	@Override
	public int compareTo(Facet o) {
		return (int) (o.count - this.count);
	}
	
	public JSONObject toJSON() {
		
		JSONObject obj = new JSONObject();
		
		obj.put("count", count);
		obj.put("name", name);
		obj.put("classname", classname);	
		
		return obj;
		
	}


}
