package org.hucompute.textimager.uima.wiki;

import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.mapdb.*;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class WikidataHyponyms extends JCasAnnotator_ImplBase {

	/**
	 * Param for Blazegraph-Query
	 */
	public static final String PARAM_BLAZEGRAPH = "blazegraph";
	@ConfigurationParameter(name = PARAM_BLAZEGRAPH, mandatory = false,defaultValue="http://huaxal.hucompute.org:8975/bigdata/sparql")
	protected String paramBlazegraph;

	/**
	 * Cache path for the wikidata results. If not set, a MemoryDB will used.
	 */
	public static final String PARAM_CACHE_PATH = "cachePath";
	@ConfigurationParameter(name = PARAM_CACHE_PATH, mandatory = false,defaultValue="mapdb/database")
	protected String mapDBCachePath;

	/**
	 * Number of Threads for wiki service call.
	 */
	public static final String PARAM_THREAD_COUNT = "numberOfThreads";
	@ConfigurationParameter(name = PARAM_THREAD_COUNT, mandatory = true,defaultValue="1")
	protected int numberOfThreads;


	HTreeMap<String, HashSet<WikidataHyponymObject>>allInstances;
	HTreeMap<String, HashSet<WikidataHyponymObject>>allhyponyms;
	DB db = null;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
//		if(!new File(mapDBCachePath).getParentFile().exists())
//			new File(mapDBCachePath).getParentFile().mkdirs();
//		int dbFileOffset = 0;

		try {
			db = DBMaker.memoryDB().make();
		}
		catch (DBException e){
			e.printStackTrace();
		}

//		while(db == null){
//			try{
//			db= DBMaker.fileDB(new File(mapDBCachePath+dbFileOffset))
//					.closeOnJvmShutdown()
//					//TODO encryption API
//					//.encryptionEnable("password")
//					.make();
//			}catch(DBException e){
//				try {
//					Thread.sleep(3000);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//				e.printStackTrace();
//				dbFileOffset++;
//			}
//		}
		allhyponyms = db.hashMap("allhyponyms").keySerializer(Serializer.STRING).valueSerializer(new HashSetSerializer()).createOrOpen();
		allInstances = db.hashMap("allInstances").keySerializer(Serializer.STRING).valueSerializer(new HashSetSerializer()).createOrOpen();
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

		for (WikipediaLink wikipediaLink : JCasUtil.select(aJCas, WikipediaLink.class)) {
			if(wikipediaLink.getLinkType().equals("internal")){
				executor.execute(new WikiRunner(wikipediaLink,aJCas));
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


	public class WikiRunner implements Runnable {

		private WikipediaLink wikipediaLink;
		private JCas jCas;

		public WikiRunner(WikipediaLink wikify,JCas jCas){
			this.wikipediaLink=wikify;
			this.jCas = jCas;
		}

		@Override
		public void run() {
			try {
				HashSet<WikidataHyponymObject>hyponyms = null;
				String link = wikipediaLink.getTarget().replaceAll(".*/", "");
				hyponyms = allhyponyms.get(link);

				if(hyponyms == null){
					String wikidataId = getWikidataId(jCas.getDocumentLanguage(), link);
					hyponyms  = new HashSet<>();
					if(wikidataId != null){
						hyponyms = getSubclassOf(wikidataId);
						boolean isInstanceOf = false;
						if(hyponyms.size()==1 && hyponyms.iterator().next().linkTo == null)
						{
							HashSet<WikidataHyponymObject>instances = getInstenceOf(wikidataId);
							hyponyms.addAll(instances);
							for (WikidataHyponymObject string : instances) {
								if(string.linkTo.equals("Q16521") || string.linkTo.equals("Q23038290")){
									hyponyms.addAll(getParentTaxon(wikidataId,1));
									break;
								}else{
									hyponyms.addAll(getSubclassOf(string.linkTo,1));
								}
							}
							isInstanceOf = true;
						}
						WikidataHyponymObject self = new WikidataHyponymObject();
						self.depth = -1;
						self.linkTo = wikidataId;
						self.isInstanceOf = isInstanceOf;
						hyponyms.add(self);
					}
					allhyponyms.put(link, hyponyms);
				}
				org.hucompute.textimager.uima.type.wikipedia.WikipediaLink wikilink = new org.hucompute.textimager.uima.type.wikipedia.WikipediaLink(jCas,wikipediaLink.getBegin(),wikipediaLink.getEnd());
				wikilink.setLinkType(wikipediaLink.getLinkType());
				wikilink.setTarget(wikipediaLink.getTarget());
				wikilink.setAnchor(wikipediaLink.getAnchor());

				HashSet<String>hyponymsList = new HashSet<>();
				for (WikidataHyponymObject wikidataHyponymObject : hyponyms) {
					if(wikidataHyponymObject.depth == -1){
						wikilink.setWikiData(wikidataHyponymObject.linkTo);
						wikilink.setIsInstance(wikidataHyponymObject.isInstanceOf);
					}
					else{
						hyponymsList.add(wikidataHyponymObject.linkTo);
					}
					WikiDataHyponym wiki = new WikiDataHyponym(jCas, wikipediaLink.getBegin(), wikipediaLink.getEnd());
					wiki.setId(wikidataHyponymObject.linkTo);
					wiki.setDepth(wikidataHyponymObject.depth);
					wiki.addToIndexes(jCas);
				}
				int i = 0;
				wikilink.setWikiDataHyponyms(new StringArray(jCas, hyponymsList.size()));
				for (String string : hyponymsList) {
					wikilink.setWikiDataHyponyms(i, string);
					i++;
				}

				wikilink.addToIndexes();
				wikipediaLink.removeFromIndexes();
			}
			catch(org.jsoup.HttpStatusException e){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
				if(e.getStatusCode() == 429)
					run();
			}
			catch(java.net.SocketTimeoutException e){
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}catch(JSONException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets Wikidata id from given Wikipedia title
	 * @param language
	 * @param title
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public String getWikidataId(String language, String title) throws JSONException, IOException {
		if(title.contains("#"))
			return null;
		String url = "https://"+
				language+
				".wikipedia.org/w/api.php?action=query&titles="+title+"&ppprop=wikibase_item&prop=pageprops&format=json&redirects";
		System.out.println(url);
		JSONObject json = new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());

		if(!json.getJSONObject("query").has("pages"))
			return null;

		String key = json.getJSONObject("query").getJSONObject("pages").keySet().iterator().next();
		if(json.getJSONObject("query").getJSONObject("pages").getJSONObject(key).has("pageprops"))
			return (json.getJSONObject("query").getJSONObject("pages").getJSONObject(key).getJSONObject("pageprops").getString("wikibase_item"));
		else
			return null;
	}

	public HashSet<WikidataHyponymObject>getSubclassOf(String wikidataId) throws JSONException, IOException{
		return getSubclassOf(wikidataId, 0);
	}

	/**
	 * Gets all hyponyms of a wikidata object with the depth
	 * @param wikidataId
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 */
	public HashSet<WikidataHyponymObject>getSubclassOf(String wikidataId, int depthOffset) throws JSONException, IOException{

		String query = "PREFIX gas: <http://www.bigdata.com/rdf/gas#>"+
				"SELECT ?item ?linkTo ?depth{"+
				"SERVICE gas:service {"+
				"gas:program gas:gasClass \"com.bigdata.rdf.graph.analytics.SSSP\" ;"+
				"gas:in wd:"+wikidataId+" ;"+
				"gas:traversalDirection \"Forward\" ;"+
				"gas:out ?item ;"+
				"gas:out1 ?depth ;"+
				//				"gas:maxVisited 6 ;"+
				"gas:linkType wdt:P279 ."+
				"}"+
				"OPTIONAL { ?item wdt:P279 ?linkTo}"+
				"}";
		System.out.println(query);

		String response = null;
		try{
			String url = paramBlazegraph+"?query="+URLEncoder.encode(query)+"&format=json";
			System.out.println("Ask: "+url);
			response = Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).ignoreContentType(true).execute().body();
		}catch (ConnectException e) {
			String url = "https://query.wikidata.org/sparql?query="+URLEncoder.encode(query,"UTF-8")+"&format=json";
			response = Jsoup.connect(url).userAgent("Mozilla").ignoreHttpErrors(true).ignoreContentType(true).execute().body();
		}
		System.out.println(response);
		JSONObject jsonHyponyms = new JSONObject(response);
		HashSet<WikidataHyponymObject>objects = json2Wikidata(jsonHyponyms,depthOffset);
		return objects;
	}


	public HashSet<WikidataHyponymObject>getParentTaxon(String wikidataId, int depthOffset) throws JSONException, IOException{

		String query = "PREFIX gas: <http://www.bigdata.com/rdf/gas#>"+
				"SELECT ?item ?linkTo ?depth{"+
				"SERVICE gas:service {"+
				"gas:program gas:gasClass \"com.bigdata.rdf.graph.analytics.SSSP\" ;"+
				"gas:in wd:"+wikidataId+" ;"+
				"gas:traversalDirection \"Forward\" ;"+
				"gas:out ?item ;"+
				"gas:out1 ?depth ;"+
				//				"gas:maxVisited 6 ;"+
				"gas:linkType wdt:P171 ."+
				"}"+
				"OPTIONAL { ?item wdt:P171 ?linkTo}"+
				"}";

		System.out.println(query);
		JSONObject jsonHyponyms = null;
		try{
			String url = paramBlazegraph+"?query="+URLEncoder.encode(query)+"&format=json";
			jsonHyponyms= new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
		}catch (ConnectException e) {
			String url = "https://query.wikidata.org/sparql?query="+URLEncoder.encode(query)+"&format=json";
			jsonHyponyms = new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body());
		}
		HashSet<WikidataHyponymObject>objects = json2Wikidata(jsonHyponyms,depthOffset);
		return objects;
	}

	public HashSet<WikidataHyponymObject>getInstenceOf(String wikidataId) throws JSONException, IOException{
		if(allInstances.containsKey(wikidataId)){
			return allInstances.get(wikidataId);
		}

		JSONObject jsonHyponymsInstanceOf = null;
		try{
			String sparqleInstanceOf = paramBlazegraph+"?query=SELECT%20%3FlinkTo%20%7B%0Awd%3A"+wikidataId+"%20wdt%3AP31%20%3FlinkTo%0A%7D&format=json";
			jsonHyponymsInstanceOf = new JSONObject(Jsoup.connect(sparqleInstanceOf).ignoreContentType(true).execute().body());
		}catch (ConnectException e) {
			String sparqleInstanceOf = "https://query.wikidata.org/sparql?query=SELECT%20%3FlinkTo%20%7B%0Awd%3A"+wikidataId+"%20wdt%3AP31%20%3FlinkTo%0A%7D&format=json";
			jsonHyponymsInstanceOf = new JSONObject(Jsoup.connect(sparqleInstanceOf).ignoreContentType(true).execute().body());
		}
		HashSet<WikidataHyponymObject>objects = json2Wikidata(jsonHyponymsInstanceOf,1);
		allInstances.put(wikidataId, objects);
		return objects;
	}

	public HashSet<WikidataHyponymObject> json2Wikidata(JSONObject jsonHyponymsInstanceOf, int depthOffset){
		HashSet<WikidataHyponymObject> output = new HashSet<>();
		for (Object hyponym : jsonHyponymsInstanceOf.getJSONObject("results").getJSONArray("bindings")) {
			JSONObject currentHyponym = (JSONObject)hyponym;
			WikidataHyponymObject wikiObject = new WikidataHyponymObject();
			if(currentHyponym.has("linkTo"))
				wikiObject.linkTo = currentHyponym.getJSONObject("linkTo").getString("value").replace("http://www.wikidata.org/entity/", "");
			//			if(currentHyponym.has("item"))
			//				hyponyms.add(currentHyponym.getJSONObject("item").getString("value").replace("http://www.wikidata.org/entity/", "wiki/"));
			if(currentHyponym.has("depth"))
				wikiObject.depth = ((int)Double.parseDouble(currentHyponym.getJSONObject("depth").getString("value")))+depthOffset;
			else
				wikiObject.depth = depthOffset;
			output.add(wikiObject);
		};
		return output;
	}

	//	private class WikiDataThread implements Runnable{
	//		WikipediaLink wikipediaLink;
	//		JCas aJCas;
	//
	//		public WikiDataThread(JCas aJCas,WikipediaLink wikipediaLink) {
	//			this.wikipediaLink = wikipediaLink;
	//			this.aJCas = aJCas;
	//		}
	//
	//		@Override
	//		public void run() {
	//			org.hucompute.textimager.uima.type.wikipedia.WikipediaLink wikilink = new org.hucompute.textimager.uima.type.wikipedia.WikipediaLink(aJCas,wikipediaLink.getBegin(),wikipediaLink.getEnd());
	//			wikilink.setLinkType(wikipediaLink.getLinkType());
	//			wikilink.setTarget(wikipediaLink.getTarget());
	//			wikilink.setAnchor(wikipediaLink.getAnchor());
	//			try {
	//				wikilink.setWikiData(wikiDataFromWikipediaLink(wikilink.getTarget()));
	//			} catch (IOException e1) {
	//				// TODO Auto-generated catch block
	//				e1.printStackTrace();
	//			}
	//			if(wikilink.getWikiData()!=null)
	//				try {
	//					List<String> wikidatas = wikidataHyponyms(wikilink.getWikiData());
	//					wikilink.setWikiDataHyponyms(new StringArray(aJCas, wikidatas.size()));
	//					for (int i = 0; i < wikidatas.size(); i++) {
	//						wikilink.setWikiDataHyponyms(i, wikidatas.get(i));
	//					}
	//				} catch (JSONException | IOException e) {
	//					// TODO Auto-generated catch block
	//					e.printStackTrace();
	//				}
	//			wikilink.addToIndexes();
	//			wikipediaLink.removeFromIndexes();
	//		}
	//
	//	}
	//
	//
	//
	//	public List<String>wikidataHyponyms(String wikidataId) throws JSONException, IOException{
	//		try{
	//		String query =
	//				"SELECT ?item{"+
	//						"wd:"+ wikidataId +" wdt:P31|wdt:P279*|wdt:P31/wdt:P279 ?item"+
	//						"}";
	//		String url = "https://query.wikidata.org/sparql?query="+URLEncoder.encode(query, "utf-8")+"&format=json";
	//		return getWikidataInstanceOfByJson(new JSONObject(Jsoup.connect(url).ignoreContentType(true).execute().body()).getJSONObject("results").getJSONArray("bindings"));
	//		}catch(HttpStatusException e){
	//			e.printStackTrace();
	//			if(e.getStatusCode() == 429){
	//				try {
	//					Thread.sleep(1000);
	//				} catch (InterruptedException e1) {
	//					// TODO Auto-generated catch block
	//					e1.printStackTrace();
	//				}
	//				return wikidataHyponyms(wikidataId);
	//			}
	//			else
	//			{
	//				return new ArrayList<>();
	//			}
	//		}
	//	}
	//
	//	public String wikiDataFromWikipediaLink(String wikiTitle,String wikipediaLanguage) throws IOException{
	//		String url = "https://" + wikipediaLanguage + ".wikipedia.org/w/api.php?action=query&prop=pageprops&ppprop=wikibase_item&redirects=1&format=xml&titles="+wikiTitle;
	//		Document doc;
	//		doc = Jsoup.connect(url).ignoreContentType(true).execute().parse();
	//		if(doc.select("query > pages pageprops").isEmpty())
	//			return null;
	//		else
	//			return (doc.select("query > pages pageprops").get(0).attr("wikibase_item"));
	//	}
	//
	//	public synchronized String wikiDataFromWikipediaLink(String wikipediaLink) throws IOException{
	//		String language = wikipediaLink.replaceAll(".*?//(.*?)\\..*", "$1");
	//		String title = null;
	//		if(wikipediaLink.contains("/w/index.php?title="))
	//			title = wikipediaLink.replaceAll(".*/w/index.php.title=", "");
	//		else
	//			title = wikipediaLink.replaceAll(".*?//.*?/.*?/(.*)", "$1");
	//		return wikiDataFromWikipediaLink(title, language);
	//	}
	//
	//	public List<String>getWikidataInstanceOfByJson(JSONArray json){
	//		List<String>output = new ArrayList<String>();
	//		for(int i = 0; i < json.length(); i++){
	//			if(json.getJSONObject(i).get("item") instanceof JSONObject)
	//				output.add(json.getJSONObject(i).getJSONObject("item").getString("value").replace("http://www.wikidata.org/entity/", ""));
	//			else
	//				output.add(json.getJSONObject(i).getString("item").replace("http://www.wikidata.org/entity/", ""));
	//		}
	//		return output;
	//	}
}
