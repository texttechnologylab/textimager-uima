package org.hucompute.textimager.uima.wiki;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym;
import org.junit.Test;

import com.google.common.collect.Iterators;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;

public class WikidataHyponymsTest  {
	
	@Test
	public void getWikipediaLink() throws UIMAException, IOException{
		WikidataHyponyms WikidataHyponyms = new WikidataHyponyms();
		assertEquals("Q4692", WikidataHyponyms.getWikidataId("de", "Renaissance"));
		assertEquals("Q169243", WikidataHyponyms.getWikidataId("en","Protagoras"));
	}
	
	
//	@Test
//	public void simpleExampleDE() throws UIMAException{
//		JCas cas = JCasFactory.createText("Mona Lisa ist ein weltberühmtes Ölgemälde von Leonardo da Vinci aus der Hochphase der italienischen Renaissance Anfang des 16. Jahrhunderts. Sonnenblume.", "de");
//
//		addWikipediaLink(cas, 32, 41, "https://de.wikipedia.org/wiki/%C3%96lmalerei", "internal", null);
//		addWikipediaLink(cas, 46, 63, "https://de.wikipedia.org/wiki/Leonardo_da_Vinci", "internal", null);
//		addWikipediaLink(cas, 72, 81, "https://de.wikipedia.org/wiki/Hochrenaissance", "internal", null);
//		addWikipediaLink(cas, 100, 111, "https://de.wikipedia.org/wiki/Renaissance", "internal", null);
//		addWikipediaLink(cas, 141, 152, "https://de.wikipedia.org/wiki/Sonnenblume", "internal", null);
//		
////		org.hucompute.textimager.uima.type.wikipedia.WikipediaLink wiki = new org.hucompute.textimager.uima.type.wikipedia.WikipediaLink(cas,100,111);
////		wiki.setAnchor(null);
////		wiki.setTarget("https://de.wikipedia.org/wiki/Renaissance");
////		wiki.setLinkType("internal");
////		wiki.addToIndexes();
//		
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(AnalysisEngineFactory.createEngineDescription(WikidataHyponyms.class));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		
//		org.hucompute.textimager.uima.type.wikipedia.WikipediaLink [] wikiLinks = Iterators.toArray(JCasUtil.select(cas, org.hucompute.textimager.uima.type.wikipedia.WikipediaLink.class).iterator(), org.hucompute.textimager.uima.type.wikipedia.WikipediaLink.class);
//
//		//Mahlerei
//		assertEquals(wikiLinks[0].getWikiData(),"Q174705");
//		assertTrue(new HashSet<String>(Arrays.asList(new String[]{"Q1231896","Q26904132","Q11177771","Q2695280","Q1914636","Q3249551","Q4026292","Q1190554","Q1150070","Q20937557","Q5915039","Q26907166","Q16887380","Q937228","Q17553950","Q58415929","Q714737","Q1207505","Q4406616","Q35120","Q151885","Q2145290","Q7184903","Q4393498","Q488383"})).containsAll(new HashSet<String>(Arrays.asList(wikiLinks[0].getWikiDataHyponyms().toArray()))));
//		
//		//Da Vinci
//		assertEquals(wikiLinks[1].getWikiData(),"Q762");
//		assertTrue(new HashSet<String>(Arrays.asList(new String[]{"Q154954", "Q215627", "Q4330518", "Q830077", "Q3778211", "Q5", "Q795052", "Q7887142", "Q488383", "Q18336849", "Q7184903", "Q23958946", "Q2198779", "Q35120", "Q24229398"})).containsAll(new HashSet<String>(Arrays.asList(wikiLinks[1].getWikiDataHyponyms().toArray()))));
//		HashSet<String>da_vinci_hyponyms = new HashSet<>(Arrays.asList(wikiLinks[1].getWikiDataHyponyms().toArray()));
//		for (WikiDataHyponym hyponym: JCasUtil.selectCovered(WikiDataHyponym.class, wikiLinks[1])) {
//			assertTrue(da_vinci_hyponyms.contains(hyponym.getId()) || hyponym.getId().equals(wikiLinks[1].getWikiData()));
//		}
//		
//		//Hochrenaissance
//		assertEquals(wikiLinks[2].getWikiData(),"Q1474884");
//		assertArrayEquals(new String[]{"Q1207505", "Q1792644", "Q16887380", "Q937228", "Q769620", "Q735", "Q2996394", "Q488383", "Q20937557", "Q2145290", "Q483394", "Q1150070", "Q2198855", "Q4406616", "Q3249551", "Q9332", "Q13878858", "Q61788060", "Q32880", "Q4026292", "Q1792379", "Q151885", "Q1914636", "Q5915039", "Q80083", "Q11028", "Q781413", "Q1190554", "Q5127848", "Q4393498", "Q714737", "Q2018526", "Q9081", "Q26907166", "Q170658", "Q17553950", "Q1292119", "Q7184903", "Q184872", "Q58415929", "Q3533467", "Q968159", "Q35120", "Q16889133"}, wikiLinks[2].getWikiDataHyponyms().toArray());
//
//		//Renaisasnce
//		assertEquals(wikiLinks[3].getWikiData(),"Q4692");
//		assertArrayEquals(new String[]{"Q1207505", "Q1792644", "Q16887380", "Q937228", "Q769620", "Q735", "Q2996394", "Q488383", "Q20937557", "Q2145290", "Q483394", "Q1150070", "Q2198855", "Q4406616", "Q3249551", "Q9332", "Q13878858", "Q61788060", "Q32880", "Q4026292", "Q1792379", "Q151885", "Q1914636", "Q5915039", "Q80083", "Q11028", "Q781413", "Q1190554", "Q5127848", "Q4393498", "Q714737", "Q2018526", "Q9081", "Q26907166", "Q170658", "Q17553950", "Q1292119", "Q7184903", "Q184872", "Q58415929", "Q3533467", "Q968159", "Q35120", "Q16889133"}, wikiLinks[3].getWikiDataHyponyms().toArray());
//	
//		//sonnenblume
//		assertEquals(wikiLinks[4].getWikiData(),"Q171497");
//		HashSet<String>sonnenBlumeHyponmysGold = new HashSet<>(Arrays.asList(new String[]{"Q16521","Q171497","Q26949","Q666616","Q5704835","Q21004","Q25400","Q21730","Q2935471","Q747502","Q869087","Q165468","Q2710933","Q25314","Q25814","Q27133","Q642865","Q756","Q192154","Q19088","Q879246","Q133527","Q2382443","Q11973077","Q17539327"}));
//		HashSet<String>sonnenBlumeHyponmyspPredict = new HashSet<>(Arrays.asList(wikiLinks[4].getWikiDataHyponyms().toArray()));
//		assertTrue(sonnenBlumeHyponmysGold.containsAll(sonnenBlumeHyponmyspPredict));
//		
//	}
	
//	@Test
//	public void test() throws UIMAException, IOException{
//		CollectionReader coll = CollectionReaderFactory.createCollectionReader(XmiReader.class, XmiReader.PARAM_SOURCE_LOCATION,"src/test/resources/285557.xmi");
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(AnalysisEngineFactory.createEngineDescription(WikidataHyponyms.class));
//		SimplePipeline.runPipeline(coll,builder.createAggregate());
//	}
	
	private void addWikipediaLink(JCas cas, int begin, int end, String target, String linkTyp, String anchor){
		WikipediaLink wiki = new WikipediaLink(cas,begin,end);
		wiki.setAnchor(anchor);
		wiki.setTarget(target);
		wiki.setLinkType(linkTyp);
		wiki.addToIndexes();
	}
	
}
