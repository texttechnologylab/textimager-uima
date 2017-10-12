package org.hucompute.textimager.uima.marmot;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertPOS;
import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertLemma;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;

public class MarMoTLemmaTest {

//	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.", "de");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		new Token(cas,0,3).addToIndexes();
		new Token(cas,4,7).addToIndexes();
		new Token(cas,8,11).addToIndexes();
		new Token(cas,12,17).addToIndexes();
		new Token(cas,18,22).addToIndexes();
		new Token(cas,22,23).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				MarMoTLemma.class
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		assertLemma(new String[]{"der","sein","ein","gut","Test","--"}, JCasUtil.select(cas, Lemma.class));
	}
	
//	@Test
	public void simpleExampleLa() throws UIMAException{
		JCas cas = JCasFactory.createText("Hoc senatusconsulti genus in usu fuit a tempore Gracchorum usque ad secundum triumviratum (43 a.C.n.).");
//		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();
//
//		new Token(cas,0,6).addToIndexes();
//		new Token(cas,7,14).addToIndexes();
//		new Token(cas,15,18).addToIndexes();
//		new Token(cas,18,19).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				BreakIteratorSegmenter.class
				));
		builder.add(createEngineDescription(
				MarMoTLemma.class
				));
		
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		assertLemma(new String[]{"magnus","stultus","sum","."}, JCasUtil.select(cas, Lemma.class));

	}

}
