package org.hucompute.textimager.fasttext.languageidentification;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.Language;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

import static org.junit.Assert.*;

import java.util.Iterator;


public class LanguageIdentificationPercentageTest {

	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test. This is a test.");
		DocumentMetaData.create(cas).setDocumentId("test");
		new Sentence(cas, 0, 22).addToIndexes();
		new Sentence(cas, 23, cas.getDocumentText().length()).addToIndexes();
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				LanguageIdentificationPercentage.class
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		System.out.println(JCasUtil.select(cas, Language.class));
		assertEquals(JCasUtil.select(cas, Language.class).size(),2);
		Iterator<Language> iter = JCasUtil.select(cas, Language.class).iterator();
		assertEquals(iter.next().getValue(), "de");
		assertEquals(iter.next().getValue(), "en");
	}
	
	

}
