package com.sos.app.mediscene.client.solr;

import javax.servlet.ServletContext;

public class SolrConfiguration {
	
	String[] localHost = { SolrQueryClient.LOCALHOST };
	private String shards = "localhost:8983/solr";
	private String server = SolrQueryClient.LOCALHOST;
	private String highlightField = "*";
	private String idField = "id";
	private int numHighlightSnippets = 1;
	private String highlightPreTag = "<span class='highlight'>";
	private String highlightPostTag = "</span>";
	private String[] autosuggestFields =  { "organization", "location" };
	private String[] suggestionFields =  { "organization", "person", "location" };
	private int autosuggestLimit = 10;
	private String titleField = "id";
	private String urlField = null;
	private String textField = "text,location,organization,person";
	private String rangeQueryReformatter = null;
	private int highlightSnippetLength = 50;
	private int resultsPerPage = 10;
	private int autosuggestLowerFrequency = 10;
	private boolean showScores = true;

	public SolrConfiguration(){		
	}

	public SolrConfiguration(ServletContext context){
		
		/*server = context.getInitParameter("solr-server");
		shards = context.getInitParameter("solr-shards");
		highlightField = context.getInitParameter("highlight-field");
		idField = context.getInitParameter("id-field");
		numHighlightSnippets = Integer.parseInt(context.getInitParameter("highlight-snippets"));
		
		if (context.getInitParameter("lucid-api") != null){
			apiServer = context.getInitParameter("lucid-api");
		}
		
		if (context.getInitParameter("highlight-pretag") != null){
			highlightPreTag = context.getInitParameter("highlight-pretag");
		}
		
		if (context.getInitParameter("highlight-posttag") != null){
			highlightPostTag = context.getInitParameter("highlight-posttag");
		}
		
		titleField = context.getInitParameter("title-field");
		
		textField = context.getInitParameter("text-field");
		
		highlightSnippetLength = Integer.parseInt(context.getInitParameter("highlight-snippet-length"));
		
		resultsPerPage = Integer.parseInt(context.getInitParameter("results-per-page"));
		
		showScores = (context.getInitParameter("show-scores").compareTo("true") == 0) ? true : false;
		
		if (context.getInitParameter("url-field") != null){
			urlField = context.getInitParameter("url-field");
		}
		
		if (context.getInitParameter("autosuggest-fields") != null){
			String autosuggest = context.getInitParameter("autosuggest-fields");
			autosuggestFields = autosuggest.split(",");
		}
		

		if (context.getInitParameter("suggestion-fields") != null){
			String suggestions = context.getInitParameter("suggestion-fields");
			suggestionFields = suggestions.split(",");
		}
		
		autosuggestLimit = Integer.parseInt(context.getInitParameter("autosuggest-limit"));
		
		autosuggestLowerFrequency = Integer.parseInt(context.getInitParameter("autosuggest-lower-frequency"));*/
		
	}
	
	public String getTitleField() {
		return titleField;
	}

	public void setTitleField(String titleField) {
		this.titleField = titleField;
	}
	
	public String getHighlightPreTag() {
		return highlightPreTag;
	}

	public void setHighlightPreTag(String highlightPreTag) {
		this.highlightPreTag = highlightPreTag;
	}

	public String getHighlightPostTag() {
		return highlightPostTag;
	}

	public void setHighlightPostTag(String highlightPostTag) {
		this.highlightPostTag = highlightPostTag;
	}
	

	public String getShards() {
		return shards;
	}
	
	public void setShards(String shards) {
		this.shards = shards;
	}
	
	public String getHighlightField() {
		return highlightField;
	}
	
	public void setHighlightField(String highlightField) {
		this.highlightField = highlightField;
	}
	
	public String getIdField() {
		return idField;
	}
	
	public void setIdField(String idField) {
		this.idField = idField;
	}
	
	public int getNumHighlightSnippets() {
		return numHighlightSnippets;
	}
	
	public void setNumHighlightSnippets(int numHighlightSnippets) {
		this.numHighlightSnippets = numHighlightSnippets;
	}

	public int getHighlightSnippetLength() {
		return highlightSnippetLength;
	}

	public void setHighlightSnippetLength(int highlightSnippetLength) {
		this.highlightSnippetLength = highlightSnippetLength;
	}

	public int getResultsPerPage() {
		return resultsPerPage;
	}

	public void setResultsPerPage(int resultsPerPage) {
		this.resultsPerPage = resultsPerPage;
	}

	public boolean isShowScores() {
		return showScores;
	}

	public void setShowScores(boolean showScores) {
		this.showScores = showScores;
	}

	public String[] getAutosuggestFields() {
		return autosuggestFields;
	}

	public void setAutosuggestFields(String[] autosuggestFields) {
		this.autosuggestFields = autosuggestFields;
	}

	public int getAutosuggestLimit() {
		return autosuggestLimit;
	}

	public void setAutosuggestLimit(int autosuggestLimit) {
		this.autosuggestLimit = autosuggestLimit;
	}

	public String getTextField() {
		return textField;
	}

	public void setTextField(String textField) {
		this.textField = textField;
	}

	public String getRangeQueryReformatter() {
		return rangeQueryReformatter;
	}

	public void setRangeQueryReformatter(String rangeQueryReformatter) {
		this.rangeQueryReformatter = rangeQueryReformatter;
	}

	public String getUrlField() {
		return urlField;
	}

	public String getServer(){
		return server;
	}
	
	public void setServer(String server){
		this.server = server;
	}
	
	
	public void setUrlField(String urlField) {
		this.urlField = urlField;
	}

	public int getAutosuggestLowerFrequency() {
		return autosuggestLowerFrequency;
	}

	public void setAutosuggestLowerFrequency(int autosuggestLowerFrequency) {
		this.autosuggestLowerFrequency = autosuggestLowerFrequency;
	}

	public String[] getSuggestionFields() {
		return suggestionFields;
	}

	public void setSuggestionFields(String[] suggestionFields) {
		this.suggestionFields = suggestionFields;
	}

}
