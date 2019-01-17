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
import org.junit.Test;

import com.google.common.collect.Iterators;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Arrays;

import de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink;
import de.tudarmstadt.ukp.dkpro.core.io.xmi.XmiReader;

public class WikidataHyponymsTest  {
	
	@Test
	public void getWikipediaLink() throws UIMAException, IOException{
		WikidataHyponyms WikidataHyponyms = new WikidataHyponyms();
		assertEquals("Q4692", WikidataHyponyms.wikiDataFromWikipediaLink("https://de.wikipedia.org/wiki/Renaissance"));
		assertEquals("Q4692", WikidataHyponyms.wikiDataFromWikipediaLink("Renaissance","de"));
		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("https://de.wikipedia.org/wiki/Renaissance"), WikidataHyponyms.wikiDataFromWikipediaLink("Renaissance","de"));

		assertEquals("Q169243", WikidataHyponyms.wikiDataFromWikipediaLink("https://en.wikipedia.org/wiki/Protagoras"));
		assertEquals("Q169243", WikidataHyponyms.wikiDataFromWikipediaLink("Protagoras","en"));
		assertEquals(WikidataHyponyms.wikiDataFromWikipediaLink("https://en.wikipedia.org/wiki/Protagoras"), WikidataHyponyms.wikiDataFromWikipediaLink("Protagoras","en"));
	}
	
	
	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Mona Lisa ist ein weltberühmtes Ölgemälde von Leonardo da Vinci aus der Hochphase der italienischen Renaissance Anfang des 16. Jahrhunderts.", "de");

		addWikipediaLink(cas, 32, 41, "https://de.wikipedia.org/wiki/%C3%96lmalerei", "internal", null);
		addWikipediaLink(cas, 46, 63, "https://de.wikipedia.org/wiki/Leonardo_da_Vinci", "internal", null);
		addWikipediaLink(cas, 72, 81, "https://de.wikipedia.org/wiki/Hochrenaissance", "internal", null);
		
		org.hucompute.textimager.uima.type.wikipedia.WikipediaLink wiki = new org.hucompute.textimager.uima.type.wikipedia.WikipediaLink(cas,100,111);
		wiki.setAnchor(null);
		wiki.setTarget("https://de.wikipedia.org/wiki/Renaissance");
		wiki.setLinkType("internal");
		wiki.addToIndexes();
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(AnalysisEngineFactory.createEngineDescription(WikidataHyponyms.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		org.hucompute.textimager.uima.type.wikipedia.WikipediaLink [] wikiLinks = Iterators.toArray(JCasUtil.select(cas, org.hucompute.textimager.uima.type.wikipedia.WikipediaLink.class).iterator(), org.hucompute.textimager.uima.type.wikipedia.WikipediaLink.class);
		System.out.println(Arrays.asList(wikiLinks));
		//Mahlerei
		assertEquals(wikiLinks[0].getWikiData(),"Q174705");
		assertArrayEquals(new String[]{"Q1231896", "Q26904132", "Q174705"}, wikiLinks[0].getWikiDataHyponyms().toArray());
		
		//Da Vinci
		assertEquals(wikiLinks[1].getWikiData(),"Q762");
		System.out.println(Arrays.toString(wikiLinks[1].getWikiDataHyponyms().toArray()));
		System.out.println(Arrays.toString(new String[]{"Q5", "Q215627", "Q762"}));
//		assertArrayEquals(new String[]{"Q5", "Q215627", "Q21070568", "Q762"}, wikiLinks[1].getWikiDataHyponyms().toArray());
		assertArrayEquals(new String[]{"Q5", "Q154954","Q215627", "Q762"}, wikiLinks[1].getWikiDataHyponyms().toArray());

		//Hochrenaissance
		assertEquals(wikiLinks[2].getWikiData(),"Q1474884");
		assertArrayEquals(new String[]{"Q32880", "Q968159", "Q1792644", "Q735", "Q2198855", "Q1474884"}, wikiLinks[2].getWikiDataHyponyms().toArray());

		//Renaisasnce
		assertEquals(wikiLinks[3].getWikiData(),"Q4692");
		assertArrayEquals(new String[]{"Q32880", "Q968159", "Q1792644", "Q735", "Q2198855", "Q4692"}, wikiLinks[3].getWikiDataHyponyms().toArray());
	}
	
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
