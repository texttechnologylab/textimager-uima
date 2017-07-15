package org.textimager.uima.OpenerProject;

import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertPOS;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;
import org.hucompute.textimager.uima.OpenerProject.*;

public class OpenerProjectTest {

	
	@Test
	public void TokenizerFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.", "fr");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));

	}
	
	@Test
	public void TokenizerDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.", "de");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		AssertAnnotations.assertToken(new String[] {"Das", "ist", "ein", "guter", "Test","."}, JCasUtil.select(cas, Token.class));

	}
	
	@Test
	public void LanguageTestDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectLanguageIdentifier.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		assertEquals("Language", "de" , cas.getDocumentLanguage());
		
	}
	
	@Test
	public void LanguageTestFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectLanguageIdentifier.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		assertEquals("Language", "fr" , cas.getDocumentLanguage());
		
	}
	
	

}
