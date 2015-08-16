package com.sos.app.mediscene.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.solr.client.solrj.beans.Field;

@XmlRootElement(name="Generics")
public class GenericsModel implements Serializable {

	private static final long serialVersionUID = 1L;
	@Field
	private String name;
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the side_effect
	 */
	public String getSide_effect() {
		return side_effect;
	}
	/**
	 * @return the indications
	 */
	public String getIndications() {
		return indications;
	}
	/**
	 * @return the contra_indications
	 */
	public String getContra_indications() {
		return contra_indications;
	}
	/**
	 * @return the symptoms
	 */
	public String getSymptoms() {
		return symptoms;
	}
	@Field
	private String side_effect;
	@Field
	private String indications;
	@Field
	private String contra_indications;
	@Field
	private String symptoms;
}
