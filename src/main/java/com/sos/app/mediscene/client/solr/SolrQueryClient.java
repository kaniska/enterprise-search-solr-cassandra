package com.sos.app.mediscene.client.solr;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.LukeResponse.FieldTypeInfo;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SolrPingResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Suggestion;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.MoreLikeThisParams;
import org.apache.solr.common.util.NamedList;

public class SolrQueryClient {

	public static String LOCALHOST = "http://localhost:8983/solr";
	private SolrConfiguration configuration = null;
	private HttpSolrServer client = null;
	private String shards = null;

	public SolrQueryClient() throws Exception {
		this(LOCALHOST);
	}

	public SolrQueryClient(String solrHost) throws MalformedURLException {
		client = new HttpSolrServer(solrHost);
	}

	public SolrQueryClient(SolrConfiguration config)
			throws MalformedURLException {
		configuration = config;
		client = new HttpSolrServer(configuration.getServer());
		shards = configuration.getShards();
	}

	/**
	public SolrQueryClient(ServletContext context) throws MalformedURLException {
		configuration = new SolrConfiguration(context);
		client = new HttpSolrServer(configuration.getServer());
		shards = configuration.getShards();
	}
	*/

	public String doPing() {

		try {

			NamedList<Object> sresponse = ping();

			String response = "";
			for (int i = 0; i < sresponse.size(); i++) {
				response += "<p>Name=" + sresponse.getName(i) + "\n";
				response += "Value=" + sresponse.getVal(i).toString()
						+ "\n</p>";
			}

			return response;
		} catch (Exception ex) {
			return ex.getStackTrace().toString();
		}

	}

	private String highlightText(String q, String text) {

		String preTag = configuration.getHighlightPreTag();
		String postTag = configuration.getHighlightPostTag();

		String q2 = q.replaceAll("[\"\'\\Q[]()+-\\E]", "");

		String[] terms = q2.toLowerCase().split("\\s+");
		for (String s : terms) {
			if (s.length() > 3) {
				s = s.replace(".", " ");
				char[] queryUp = s.toCharArray();
				queryUp[0] = Character.toUpperCase(queryUp[0]);
				String qu = new String(queryUp);
				text = text
						.replaceAll(s, preTag + s + postTag)
						.replaceAll(qu, preTag + qu + postTag)
						.replaceAll(s.toUpperCase(),
								preTag + s.toUpperCase() + postTag);
			}
		}

		return text;
	}

	private NamedList<Object> ping() throws SolrServerException, IOException {

		SolrPingResponse response = client.ping();

		return response.getResponse();

	}

	public List<String> doSuggestedPhrases(String term, int limit)
			throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();

		if (shards != null) {
			query.setParam("shards", shards);
		}

		query.setQuery(term);
		query.setFacet(true);
		query.setFacetPrefix(term);
		query.set(FacetParams.FACET_ENUM_CACHE_MINDF, 10000);

		int maxCount = configuration.getAutosuggestLimit();
		if (limit > 0) {
			maxCount = limit;
		}
		query.setFacetLimit(maxCount * 10);
		query.setFacetSort(FacetParams.FACET_SORT_COUNT_LEGACY);

		for (String field : configuration.getSuggestionFields()) {
			query.addFacetField(field);
			query.set("f." + field + ".facet.method", "enum");
		}

		QueryResponse response = client.query(query);

		List<FacetField> theFacets = response.getFacetFields();

		List<Facet> allFacets = new LinkedList<Facet>();
		for (FacetField ff : theFacets) {
			List<Count> facetCounts = ff.getValues();
			if (facetCounts != null) {
				for (Count ct : facetCounts) {

					String facetName = ct.getName();
					if (!facetName.toLowerCase().equals(term.toLowerCase())) {
						String facetField = ct.getFacetField().getName();

						allFacets.add(new Facet(ct.getCount(), facetName,
								facetField));
					}
				}
			}
		}

		if (allFacets.size() < maxCount) {
			// do a second query
			SolrQuery query2 = new SolrQuery();

			query2.setQuery(term);
			query2.setFacet(true);
			query2.set(FacetParams.FACET_ENUM_CACHE_MINDF, 10000);

			query2.setFacetLimit(maxCount * 10);

			for (String field : configuration.getSuggestionFields()) {
				query2.addFacetField(field);
				query2.set("f." + field + ".facet.method", "enum");
			}

			QueryResponse response2 = client.query(query2);

			List<FacetField> theFacets2 = response2.getFacetFields();

			for (FacetField ff : theFacets2) {
				List<Count> facetCounts = ff.getValues();
				if (facetCounts != null) {
					for (Count ct : facetCounts) {

						String facetName = ct.getName();
						if (!facetName.toLowerCase().equals(term.toLowerCase())) {
							String facetField = ct.getFacetField().getName();

							allFacets.add(new Facet(ct.getCount(), facetName,
									facetField));
						}
					}
				}
			}
		}

		List<String> terms = new LinkedList<String>();

		Collections.sort(allFacets);

		long topCount = Long.MAX_VALUE;
		if (allFacets.size() > 10) {
			topCount = allFacets.get(0).getCount();
		}

		int ct = 0;
		for (Facet facet : allFacets) {
			if (facet.getCount() < topCount) {
				terms.add(facet.getName());
				if (ct >= maxCount)
					break;
				ct++;
			}
		}

		return terms;

	}

	public List<String> doAutosuggestFacets(String prefix, int limit)
			throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();
		if (shards != null) {
			query.setParam("shards", shards);
		}

		query.setQuery("*:*");
		query.setFacet(true);
		query.setFacetPrefix(prefix);
		query.set(FacetParams.FACET_ENUM_CACHE_MINDF, 10000);

		int maxCount = configuration.getAutosuggestLimit();
		if (limit > 0) {
			maxCount = limit;
		}
		query.setFacetLimit(maxCount);

		int lowestFrequency = configuration.getAutosuggestLowerFrequency();
		query.setFacetMinCount(lowestFrequency);

		for (String field : configuration.getAutosuggestFields()) {
			query.addFacetField(field);
			query.set("f." + field + ".facet.method", "enum");
		}

		QueryResponse response = client.query(query);

		List<FacetField> theFacets = response.getFacetFields();

		List<Facet> allFacets = new LinkedList<Facet>();
		for (FacetField ff : theFacets) {
			List<Count> facetCounts = ff.getValues();
			if (facetCounts != null) {
				for (Count ct : facetCounts) {

					String facetName = ct.getName();
					String facetField = ct.getFacetField().getName();

					allFacets.add(new Facet(ct.getCount(), facetName,
							facetField));
				}
			}
		}

		// Collections.sort(allFacets);

		List<String> terms = new LinkedList<String>();

		int ct = 0;
		for (Facet facet : allFacets) {
			if (facet.getCount() > lowestFrequency) {
				terms.add(facet.getName());
				if (ct >= maxCount)
					break;
				ct++;
			}
		}

		return terms;

	}

	/*
	 * public List<String> doAutosuggest(String prefix, int limit) throws
	 * SolrServerException, IOException {
	 * 
	 * SolrQuery query = new SolrQuery(); query.setQueryType("/terms");
	 * query.setTerms(true);
	 * query.setTermsMinCount(configuration.getAutosuggestLowerFrequency()); if
	 * (limit < 1) { limit = configuration.getAutosuggestLimit(); }
	 * 
	 * query.setTermsLimit(limit);
	 * 
	 * for (String field : configuration.getAutosuggestFields()) {
	 * query.addTermsField(field); }
	 * 
	 * boolean startParen = false; if (prefix.contains("\"")) { prefix =
	 * prefix.replaceAll("\"", ""); startParen = true; }
	 * query.setTermsPrefix(prefix.toLowerCase());
	 * 
	 * QueryResponse response = client.query(query); TermsResponse tresponse =
	 * response.getTermsResponse();
	 * 
	 * HashSet<Term> allTerms = new HashSet<Term>();
	 * 
	 * for (String key : tresponse.getTermMap().keySet()) { List<Term>
	 * theTermSet = tresponse.getTerms(key);
	 * 
	 * for (Term t : theTermSet) { allTerms.add(t); } }
	 * 
	 * List<String> terms = new LinkedList<String>(); for (Term t : allTerms) {
	 * // terms.add(t.getTerm()+"("+t.getFrequency()+")"); if (startParen) {
	 * terms.add("\\\"" + t.getTerm() + "\\\""); } else {
	 * terms.add(t.getTerm()); } if (terms.size() >= limit) break;
	 * 
	 * }
	 * 
	 * return terms;
	 * 
	 * }
	 */

	public List<String> doSpellcheck(String q) throws SolrServerException,
			IOException {

		SolrQuery query = new SolrQuery();
		if (shards != null) {
			query.setParam("shards", shards);
		}
		query.setQueryType("/spell");
		query.setParam("q", q.toLowerCase());
		query.setParam("spellcheck", "true");

		QueryResponse response = client.query(query);

		List<String> terms = new LinkedList<String>();

		SpellCheckResponse scr = response.getSpellCheckResponse();

		if (scr != null) {
			for (Suggestion suggestion : scr.getSuggestions()) {
				for (String alternative : suggestion.getAlternatives()) {
					terms.add(alternative);
				}
			}
		}

		return terms;

	}

	public List<Document> doMLT(String id, String fields, int limit)
			throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();
		if (shards != null) {
			query.setParam("shards", shards);
		}

		query.setQueryType("/" + MoreLikeThisParams.MLT);
		query.set(MoreLikeThisParams.MATCH_INCLUDE, false);
		query.set(MoreLikeThisParams.MIN_DOC_FREQ, 1);
		query.set(MoreLikeThisParams.MIN_TERM_FREQ, 1);
		query.set(MoreLikeThisParams.DOC_COUNT, limit);
		query.setRows(limit);
		query.set(MoreLikeThisParams.SIMILARITY_FIELDS, fields);
		query.setIncludeScore(true);
		query.addField(configuration.getIdField());

		id = id.replaceAll("\"", "");
		query.setQuery("id:\"" + id + "\"");

		String[] titleFields = configuration.getTitleField().split(",");
		for (String tf : titleFields) {
			query.addField(tf.trim());
		}

		if (configuration.getUrlField() != null) {
			query.addField(configuration.getUrlField());
		}

		QueryResponse response = client.query(query);

		List<Document> documents = new LinkedList<Document>();

		Iterator<SolrDocument> iter = response.getResults().iterator();
		while (iter.hasNext()) {

			SolrDocument resultDoc = iter.next();

			Object docId = resultDoc.getFieldValue(configuration.getIdField());

			String docid = (String) docId;

			String title = "";

			Vector<String> titles = new Vector<String>();
			for (String titleField : configuration.getTitleField().split(",")) {
				Object resultObjTitle = resultDoc.getFieldValue(titleField);
				try {

					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObjTitle;
					if (al != null) {
						for (String tf2 : al) {
							tf2 = tf2.trim();
							if (titles.size() > 0) {
								for (String compTitle : titles) {
									if (!tf2.startsWith(compTitle)
											&& !compTitle.startsWith(tf2)) {
										title += tf2 + " ";
									}
								}
								titles.add(tf2);
							} else {
								titles.add(tf2);
								title += tf2 + " ";
							}
						}
					}
				} catch (ClassCastException ex) {
					try {
						String tf2 = ((String) resultObjTitle).trim();
						if (titles.size() > 0) {
							for (String compTitle : titles) {
								if (!tf2.startsWith(compTitle)
										&& !compTitle.startsWith(tf2)) {
									title += tf2 + " ";
								}
							}
							titles.add(tf2);
						} else {
							titles.add(tf2);
							title += tf2 + " ";
						}
					} catch (ClassCastException ex2) {

					}
				}
			}

			String url = "";
			Object resultObjUrl = resultDoc.getFieldValue(configuration
					.getUrlField());
			if (resultObjUrl != null) {
				try {

					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObjUrl;
					if (al != null) {
						for (String tf2 : al) {
							url += tf2 + " ";
						}
					}
				} catch (ClassCastException ex) {
					try {
						url += (String) resultObjUrl;
					} catch (ClassCastException ex2) {

					}
				}
			}

			Document doc = new Document();
			doc.setId(docid);
			doc.setUrl(url.replaceAll("&amp;", "&").replaceAll("& ", "&"));
			// System.err.println(url.replaceAll("&amp;", "&"));

			doc.setTitle(title);
			doc.setScore(((Float) resultDoc.getFieldValue("score"))
					.doubleValue());

			documents.add(doc);

		}

		return documents;
	}

	public Document doGetDocumentById(String docid, String termquery,
			List<String> facetFields) throws SolrServerException {

		SolrQuery query = new SolrQuery();
		if (shards != null) {
			query.setParam("shards", shards);
		}

		if (docid.startsWith("id:")) {
			String idStr = docid.substring(docid.indexOf(':') + 1);
			docid = "id:" + "\"" + idStr + "\"";
		}
		query.setQuery(docid);

		query.setFacet(true);
		if (facetFields != null) {
			for (String ff : facetFields) {
				query.addFacetField(ff);
			}
		}
		String[] textFields = configuration.getTextField().split(",");
		for (String tf : textFields) {
			query.addField(tf.trim());
		}

		String[] titleFields = configuration.getTitleField().split(",");
		for (String tf : titleFields) {
			query.addField(tf.trim());
		}

		if (configuration.getUrlField() != null) {
			query.addField(configuration.getUrlField());
		}

		query.addField(configuration.getIdField());
		query.setIncludeScore(true);

		QueryResponse response = client.query(query);

		Iterator<SolrDocument> iter = response.getResults().iterator();
		while (iter.hasNext()) {

			SolrDocument resultDoc = iter.next();

			Object docId = resultDoc.getFieldValue(configuration.getIdField());

			String id = (String) docId;

			String title = "";

			HashSet<String> noTitleRepeats = new HashSet<String>();
			for (String titleField : configuration.getTitleField().split(",")) {
				Object resultObjTitle = resultDoc.getFieldValue(titleField);
				try {

					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObjTitle;
					if (al != null) {
						for (String tf2 : al) {
							if (!noTitleRepeats.contains(tf2)) {
								title += tf2 + " ";
								noTitleRepeats.add(tf2);
							}
						}
					}
				} catch (ClassCastException ex) {
					try {
						String tf2 = (String) resultObjTitle;
						if (!noTitleRepeats.contains(tf2)) {
							title += tf2 + " ";
							noTitleRepeats.add(tf2);
						}
					} catch (ClassCastException ex2) {

					}
				}
			}

			String url = "";
			Object resultObjUrl = resultDoc.getFieldValue(configuration
					.getUrlField());
			if (resultObjUrl != null) {
				try {

					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObjUrl;
					if (al != null) {
						for (String tf2 : al) {
							url += tf2 + " ";
						}
					}
				} catch (ClassCastException ex) {
					try {
						url += (String) resultObjUrl;
					} catch (ClassCastException ex2) {

					}
				}
			}

			HashSet<String> noRepeats = new HashSet<String>();

			Document doc = new Document();
			doc.setId(id);
			doc.setUrl(url.replaceAll("&amp;", "&").replaceAll("& ", "&"));

			// may need to provide limiting capability here
			List<DocumentText> allText = new LinkedList<DocumentText>();
			for (String tf : textFields) {
				Object resultObj = resultDoc.getFieldValue(tf);
				try {
					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObj;
					if (al != null) {
						for (String tf2 : al) {
							if (!noRepeats.contains(tf2)) {
								allText.add(new DocumentText(tf, highlightText(
										termquery, tf2)));
								noRepeats.add(tf2);
							}
						}
					}
				} catch (ClassCastException ex) {
					try {
						if (!noRepeats.contains((String) resultObj)) {
							String dtext = highlightText(termquery,
									resultObj.toString());
							allText.add(new DocumentText(tf, dtext));
							noRepeats.add((String) resultObj);
						}
					} catch (ClassCastException ex2) {
						// ex2.printStackTrace();
					}
				}

			}
			doc.setText(allText);
			doc.setTitle(title);
			if (configuration.isShowScores()) {
				doc.setScore(((Float) resultDoc.getFieldValue("score"))
						.doubleValue());
			} else {
				doc.setScore(-1.0);
			}

			// get facets for the document
			// avoid repeats
			HashSet<String> noRepeats2 = new HashSet<String>();

			if (facetFields != null) {
				List<Facet> docFacets = new LinkedList<Facet>();
				for (String ff : facetFields) {
					Object resultObjFacets = resultDoc.getFieldValue(ff);
					if (resultObjFacets != null) {
						try {

							@SuppressWarnings("unchecked")
							ArrayList<String> al = (ArrayList<String>) resultObjFacets;
							if (al != null) {
								for (String tf2 : al) {
									if (!noRepeats2.contains(tf2 + "==" + ff)) {
										docFacets.add(new Facet(1, tf2, ff));
										noRepeats2.add(tf2 + "==" + ff);
									}
								}
							}
						} catch (ClassCastException ex) {
							try {
								String fstr = (String) resultObjFacets;
								if (!noRepeats2.contains(fstr + "==" + ff)) {
									docFacets.add(new Facet(1, fstr, ff));
									noRepeats2.add(fstr + "==" + ff);
								}
							} catch (ClassCastException ex2) {

							}
						}
					}
				}

				doc.setFacets(docFacets);
				return doc;

			}
		}

		return null;

	}

	public QueryResult doQuery(String q, int start, List<String> facetFields,
			List<String> facetQuery, List<String> facetFilter, String field,
			String order) throws SolrServerException, IOException {

		SolrQuery query = new SolrQuery();
		if (shards != null) {
			query.setParam("shards", shards);
		}

		q = q.replaceAll("[:;.]", "");

		if (facetFilter != null && facetFilter.size() > 0) {
			q += " AND ";
			if (facetFilter.size() > 1) {
				q += "(";
				for (int idx = 0; idx < facetFilter.size(); idx++) {
					q += facetFilter.get(idx);
					if (idx < facetFilter.size() - 1) {
						q += " OR ";
					}
				}
				q += " )";
			} else {
				q += facetFilter.get(0);
			}
		}

		if (field != null && order != null) {
			ORDER orderItem = ORDER.desc;
			if (order.compareTo("a") == 0) {
				orderItem = ORDER.asc;
			}
			query.setSortField(field, orderItem);
		}

		query.setQuery(q);

		String[] textFields = configuration.getTextField().split(",");
		for (String tf : textFields) {
			query.addField(tf.trim());
		}

		String[] titleFields = configuration.getTitleField().split(",");
		for (String tf : titleFields) {
			query.addField(tf.trim());
		}

		if (configuration.getUrlField() != null) {
			query.addField(configuration.getUrlField());
		}

		query.addField(configuration.getIdField());
		query.setHighlight(true);
		query.setParam("hl.usePhraseHighlighter", "true");
		query.setParam("hl.highlightMultiTerm", "true");
		query.setHighlightSnippets(configuration.getNumHighlightSnippets());
		query.setParam("hl.fl", configuration.getHighlightField());
		query.setHighlightSimplePre(configuration.getHighlightPreTag());
		query.setHighlightSimplePost(configuration.getHighlightPostTag());
		query.setHighlightFragsize(configuration.getHighlightSnippetLength());
		query.setStart(start);
		query.setRows(configuration.getResultsPerPage());

		query.setFacet(true);

		if (facetQuery != null) {
			for (String fq : facetQuery) {
				query.addFacetQuery(fq);
			}
		}

		if (facetFields != null) {
			for (String ff : facetFields) {
				query.addFacetField(ff);
			}
		}

		// if (facetFilter != null){
		// for (String ffq : facetFilter){
		// query.addFilterQuery(ffq);
		// }
		// }

		query.setIncludeScore(true);

		QueryResponse response = client.query(query);

		List<Document> documents = new LinkedList<Document>();

		SolrDocumentList sdl = response.getResults();
		long maxResults = sdl.getNumFound();

		List<FacetField> theFacets = response.getFacetFields();

		List<Facet> allFacets = new LinkedList<Facet>();
		if (theFacets != null) {
			for (FacetField ff : theFacets) {
				List<Count> facetCounts = ff.getValues();
				if (facetCounts != null) {
					for (Count ct : facetCounts) {

						String facetName = ct.getName();
						String facetField = ct.getFacetField().getName();

						allFacets.add(new Facet(ct.getCount(), facetName,
								facetField));
					}
				}
			}
		}
		Map<String, Integer> facetQueries = response.getFacetQuery();
		for (String key : facetQueries.keySet()) {
			int ct = facetQueries.get(key);
			String facetField = key;
			String facetClassname = "";
			if (key.contains(":")) {
				facetField = key.split(":")[1];
				facetClassname = key.split(":")[0];
			}

			allFacets.add(new Facet(ct, facetField, facetClassname));
		}

		Iterator<SolrDocument> iter = response.getResults().iterator();
		while (iter.hasNext()) {

			SolrDocument resultDoc = iter.next();

			Object docId = resultDoc.getFieldValue(configuration.getIdField());

			String id = (String) docId;

			String title = "";

			Vector<String> titles = new Vector<String>();
			for (String titleField : configuration.getTitleField().split(",")) {
				Object resultObjTitle = resultDoc.getFieldValue(titleField);
				try {

					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObjTitle;
					if (al != null) {
						for (String tf2 : al) {
							tf2 = tf2.trim();
							if (titles.size() > 0) {
								for (String compTitle : titles) {
									if (!tf2.startsWith(compTitle)
											&& !compTitle.startsWith(tf2)) {
										title += tf2 + " ";
									}
								}
								titles.add(tf2);
							} else {
								titles.add(tf2);
								title += tf2 + " ";
							}
						}
					}
				} catch (ClassCastException ex) {
					try {
						String tf2 = ((String) resultObjTitle).trim();
						if (titles.size() > 0) {
							for (String compTitle : titles) {
								if (!tf2.startsWith(compTitle)
										&& !compTitle.startsWith(tf2)) {
									title += tf2 + " ";
								}
							}
							titles.add(tf2);
						} else {
							titles.add(tf2);
							title += tf2 + " ";
						}
					} catch (ClassCastException ex2) {

					}
				}
			}

			String url = "";
			Object resultObjUrl = resultDoc.getFieldValue(configuration
					.getUrlField());
			if (resultObjUrl != null) {
				try {

					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObjUrl;
					if (al != null) {
						for (String tf2 : al) {
							url += tf2 + " ";
						}
					}
				} catch (ClassCastException ex) {
					try {
						url += (String) resultObjUrl;
					} catch (ClassCastException ex2) {

					}
				}
			}

			HashSet<String> noRepeats = new HashSet<String>();

			Document doc = new Document();
			doc.setId(id);
			doc.setUrl(url.replaceAll("&amp;", "&").replaceAll("& ", "&"));

			// may need to provide limiting capability here
			List<DocumentText> allText = new LinkedList<DocumentText>();
			for (String tf : textFields) {
				Object resultObj = resultDoc.getFieldValue(tf);
				try {
					@SuppressWarnings("unchecked")
					ArrayList<String> al = (ArrayList<String>) resultObj;
					if (al != null) {
						for (String tf2 : al) {
							if (!noRepeats.contains(tf2)) {
								allText.add(new DocumentText(tf, highlightText(
										q, tf2)));
								noRepeats.add(tf2);
							}
						}
					}
				} catch (ClassCastException ex) {
					try {
						if (!noRepeats.contains((String) resultObj)) {
							String dtext = highlightText(q,
									resultObj.toString());
							allText.add(new DocumentText(tf, dtext));
							noRepeats.add((String) resultObj);
						}
					} catch (ClassCastException ex2) {
						// ex2.printStackTrace();
					}
				}

			}
			doc.setText(allText);
			doc.setTitle(title);
			if (configuration.isShowScores()
					&& resultDoc.getFieldValue("score") != null) {
				doc.setScore(((Float) resultDoc.getFieldValue("score"))
						.doubleValue());
			} else {
				doc.setScore(-1.0);
			}

			if (response.getHighlighting().get(id) != null) {
				// get all highlight snippets
				HashSet<String> theSnippets = new HashSet<String>();
				Map<String, List<String>> allHighlightSnippets = response
						.getHighlighting().get(id);
				for (String hlKey : allHighlightSnippets.keySet()) {
					theSnippets.addAll(allHighlightSnippets.get(hlKey));
				}

				// trim the snippets, eliminating
				List<String> snippetList = new LinkedList<String>();
				for (String key : theSnippets) {
					snippetList.add(key);
				}
				doc.setSnippets(snippetList);

			}

			// get facets for the document
			// avoid repeats
			HashSet<String> noRepeats2 = new HashSet<String>();

			if (facetFields != null) {
				List<Facet> docFacets = new LinkedList<Facet>();
				for (String ff : facetFields) {
					Object resultObjFacets = resultDoc.getFieldValue(ff);
					if (resultObjFacets != null) {
						try {

							@SuppressWarnings("unchecked")
							ArrayList<String> al = (ArrayList<String>) resultObjFacets;
							if (al != null) {
								for (String tf2 : al) {
									if (!noRepeats2.contains(tf2 + "==" + ff)) {
										docFacets.add(new Facet(1, tf2, ff));
										noRepeats2.add(tf2 + "==" + ff);
									}
								}
							}
						} catch (ClassCastException ex) {
							try {
								String fstr = (String) resultObjFacets;
								if (!noRepeats2.contains(fstr + "==" + ff)) {
									docFacets.add(new Facet(1, fstr, ff));
									noRepeats2.add(fstr + "==" + ff);
								}
							} catch (ClassCastException ex2) {

							}
						}
					}
				}

				doc.setFacets(docFacets);

			}

			documents.add(doc);

		}

		QueryResult qr = new QueryResult();
		qr.setFacets(allFacets);
		qr.setDocuments(documents);
		qr.setTotalMatches(maxResults);
		qr.setStartMatch(start);
		qr.setEndMatch((start + configuration.getResultsPerPage()) < maxResults ? (start + configuration
				.getResultsPerPage()) : maxResults);

		return qr;

	}

	public SolrConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(SolrConfiguration configuration) {
		this.configuration = configuration;
	}

	public List<Facet> doGetTerms(String field, int maxNo) throws IOException,
			SolrServerException {

		LukeRequest luke = new LukeRequest();
		luke.addField(field);
		luke.setNumTerms(maxNo);
		LukeResponse rsp = luke.process(client);

		NamedList<Object> objectList = rsp.getResponse();
		NamedList<Object> fieldList = (NamedList<Object>) objectList
				.get("fields");
		NamedList<Object> subList = (NamedList<Object>) fieldList.get(field);
		NamedList<Object> topTermsList = (NamedList<Object>) subList
				.get("topTerms");

		List<Facet> termFacet = new LinkedList<Facet>();
		for (int i = 0; i < topTermsList.size(); i++) {
			String term = topTermsList.getName(i);
			Integer val = (Integer) topTermsList.getVal(i);
			termFacet.add(new Facet(val.intValue(), term, field));
		}

		return termFacet;

	}

	public Map<String, String> doGetSchema() throws IOException,
			SolrServerException {

		Map<String, String> schema = new HashMap<String, String>();
		LukeRequest luke = new LukeRequest();
		luke.setShowSchema(true);

		// System.err.println("No Terms: "+noTerms);

		LukeResponse rsp = luke.process(client);

		Map<String, FieldTypeInfo> mapInfo = rsp.getFieldTypeInfo();

		for (String key : mapInfo.keySet()) {
			List<String> fields = mapInfo.get(key).getFields();
			for (String value : fields) {
				if (!value.contains("*")) {
					schema.put(value, key);
				}
			}
			System.err.println(key + "\t" + mapInfo.get(key).getFields());
		}

		return schema;

	}

	public static void main(String[] args) {

		try {

			SolrConfiguration config = new SolrConfiguration();
			config.setShards("localhost:8983/solr,localhost:8977/solr");

			SolrQueryClient client = new SolrQueryClient(config);
			String pingResponse = client.doPing();

			System.out.println(pingResponse);

			System.out.println("SHARDS: " + config.getShards());

			QueryResult qr2 = client.doQuery("hard", 0, null, null, null, null,
					null);

			System.out.println(qr2);
			System.out
					.println("***********************************************");

			// List<String> ts = client.doAutosuggest("s", -1);
			// for (String aterm : ts) {
			// System.out.println(aterm);
			// }

			List<String> suggestions = client.doSpellcheck("mixtor");
			for (String s : suggestions) {
				System.out.println("SPELLCHECK: " + s);
			}

			List<String> facetFields = Arrays.asList("manu", "cat", "weight");
			List<String> facets = Arrays.asList("price:[0 TO 100]",
					"price:[100 TO 200]", "price:[200 TO 500]");
			QueryResult qr3 = client.doQuery("computer", 0, facetFields,
					facets, null, null, null);

			System.out.println(qr3);

			List<Document> qrMlt = client.doMLT(
					"533e2eec-fea2-11d7-9944-f834d4acf1b9", "text,title", 20);
			for (Document d : qrMlt) {
				System.out.println(d.toString());
			}

			List<String> asfacets = client.doAutosuggestFacets("O", -1);
			for (String fauto : asfacets) {
				System.out.println("FACET: " + fauto);
			}

			List<String> facetFields2 = Arrays.asList("person", "organization");
			QueryResult qr1 = client.doQuery("Assange", 0, facetFields2, null,
					null, null, null);

			System.out.println(qr1);
			System.out
					.println("***********************************************");

			Document d = client.doGetDocumentById(
					"id:0,28804,2040099_2040079_2040056,00.html", "documents",
					facetFields2);

			System.out.println(d);

			List<String> suggestedTerms = client
					.doSuggestedPhrases("Japan", -1);
			for (String suggestion : suggestedTerms) {
				System.out.println("SUGGESTION: " + suggestion);
			}

			List<String> suggestedTerms2 = client.doSuggestedPhrases("tsunami",
					-1);
			for (String suggestion2 : suggestedTerms2) {
				System.out.println("SUGGESTION2: " + suggestion2);
			}

			Map<String, String> schema = client.doGetSchema();
			for (String key : schema.keySet()) {
				System.out.println(key + "\t" + schema.get(key));
			}

			for (Facet f : client.doGetTerms("organization", 10)) {
				System.out.println(f);
			}

			for (Facet f : client.doGetTerms("location", 10)) {
				System.out.println(f);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
