package org.hucompute.textimager.uima.wiki;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.AnnotationBase;
import org.apache.uima.jcas.cas.StringArray;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;

public class WikidataHyponyms extends JCasAnnotator_ImplBase {
	/**
	 * Number of Threads for wiki service call.
	 */
	public static final String PARAM_THREAD_COUNT = "numberOfThreads";
	@ConfigurationParameter(name = PARAM_THREAD_COUNT, mandatory = true,defaultValue="1")
	protected int numberOfThreads;
	

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
		
		for (WikipediaLink wikipediaLink : JCasUtil.select(aJCas, WikipediaLink.class)) {
			if(wikipediaLink.getLinkType().equals("internal")){
				executor.execute(new WikiDataThread(aJCas, wikipediaLink));
			}
		}

		executor.shutdown();
		while(!executor.isTerminated()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private class WikiDataThread implements Runnable{
		WikipediaLink wikipediaLink; 
		JCas aJCas;
		public WikiDataThread(JCas aJCas,WikipediaLink wikipediaLink) {
			this.wikipediaLink = wikipediaLink;
			this.aJCas = aJCas;
		}
		
		@Override
		public void run() {
			org.hucompute.textimager.uima.type.wikipedia.WikipediaLink wikilink = new org.hucompute.textimager.uima.type.wikipedia.WikipediaLink(aJCas,wikipediaLink.getBegin(),wikipediaLink.getEnd());
			wikilink.setLinkType(wikipediaLink.getLinkType());
			wikilink.setTarget(wikipediaLink.getTarget());
			wikilink.setAnchor(wikipediaLink.getAnchor());
			try {
				wikilink.setWikiData(wikiDataFromWikipediaLink(wikilink.getTarget()));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(wikilink.getWikiData()!=null)
				try {
					List<String> wikidatas = wikidataHyponyms(wikilink.getWikiData());
					wikilink.setWikiDataHyponyms(new StringArray(aJCas, wikidatas.size()));
					for (int i = 0; i < wikidatas.size(); i++) {
						wikilink.setWikiDataHyponyms(i, wikidatas.get(i));
					}
				} catch (JSONException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			wikilink.addToIndexes();
			wikipediaLink.removeFromIndexes();
//			removeFromIndexes(wikipediaLink);			
		}
		
	}
	
//	private synchronized void removeFromIndexes(AnnotationBase anno){
//		anno.removeFromIndexes();
//	}



	public List<String>wikidataHyponyms(String wikidataId) throws JSONException, IOException{
		try{
		String query = 
				"SELECT ?item{"+
						"wd:"+ wikidataId +" wdt:P31|wdt:P279*|wdt:P31/wdt:P279 ?item"+
						"}";
		String url = "https://query.wikidata.org/sparql?query="+URLEncoder.encode(query, "utf-8")+"&format=json";
		return getWikidataInstanceOfByJson(new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body()).getJSONObject("results").getJSONArray("bindings"));
		}catch(HttpStatusException e){
			e.printStackTrace();
			if(e.getStatusCode() == 429){
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				return wikidataHyponyms(wikidataId);
			}
			else
			{
				return new ArrayList<>();
			}
		}
	}

	public String wikiDataFromWikipediaLink(String wikiTitle,String wikipediaLanguage) throws IOException{
		String url = "https://" + wikipediaLanguage + ".wikipedia.org/w/api.php?action=query&prop=pageprops&ppprop=wikibase_item&redirects=1&format=xml&titles="+wikiTitle;
		Document doc;
		doc = Jsoup.connect(url).ignoreContentType(true).execute().parse();
		if(doc.select("query > pages pageprops").isEmpty())
			return null;
		else
			return (doc.select("query > pages pageprops").get(0).attr("wikibase_item"));
	}

	public synchronized String wikiDataFromWikipediaLink(String wikipediaLink) throws IOException{
		String language = wikipediaLink.replaceAll(".*?//(.*?)\\..*", "$1");
		String title = null;
		if(wikipediaLink.contains("/w/index.php?title="))
			title = wikipediaLink.replaceAll(".*/w/index.php.title=", "");
		else
			title = wikipediaLink.replaceAll(".*?//.*?/.*?/(.*)", "$1");
		return wikiDataFromWikipediaLink(title, language);
	}

	public List<String>getWikidataInstanceOfByJson(JSONArray json){
		List<String>output = new ArrayList<String>();
		for(int i = 0; i < json.length(); i++){
			if(json.getJSONObject(i).get("item") instanceof JSONObject)
				output.add(json.getJSONObject(i).getJSONObject("item").getString("value").replace("http://www.wikidata.org/entity/", ""));
			else
				output.add(json.getJSONObject(i).getString("item").replace("http://www.wikidata.org/entity/", ""));
		}
		return output;
	}
}
