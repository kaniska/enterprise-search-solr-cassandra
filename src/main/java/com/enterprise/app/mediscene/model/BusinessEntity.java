package com.enterprise.app.mediscene.model;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;

public class BusinessEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private EntityValueIterator valueIterator;
	
    public BusinessEntity(List<String> columnDataList) {
    	this.valueIterator = new EntityValueIterator(columnDataList);
    }
 
    public boolean hasNext() {
    	return valueIterator.hasNext();
    }
    
    
    
public String getNextValue() {
	if(valueIterator.hasNext()){
		   return valueIterator.next().toString();
		}else{
			return "Unknown";
		}
	}
 
private class EntityValueIterator implements Iterator {

		private int index = 0; 
		private int limit = 0; 
		private List<String> valueList = null;

		public EntityValueIterator(List<String> columnDataList) {
			limit = columnDataList.size();
	    	this.valueList = columnDataList;
		}

		@Override
		public boolean hasNext() {
			return index < limit;
		}

		@Override
		public Object next() {
			return valueList.get(index++);
		}

		@Override
		public void remove() {
			// NO OP		
		}
		 
	 }
}
