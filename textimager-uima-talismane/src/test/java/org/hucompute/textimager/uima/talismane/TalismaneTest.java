package org.hucompute.textimager.uima.talismane;

import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertPOS;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.services.util.XmlFormatter;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;



public class TalismaneTest {

	
	@Test
	public void TokenizerFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.", "fr");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				TalismaneTokenSentenceAnnotator.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Sentence Sentence : JCasUtil.select(cas, Sentence.class)) {
			System.out.println(Sentence);
		}

		System.out.println(cas.getCas());
		AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));
		AssertAnnotations.assertSentence(new String[] {"Ceci est un bon test."}, JCasUtil.select(cas, Sentence.class));

	}
	
	@Test
	public void Test() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.", "fr");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				TalismaneTokenSentenceAnnotator.class));
		builder.add(createEngineDescription(
				TalismanePOS.class,
				TalismanePOS.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/talismane/lib/pos-default.map"));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		

		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
		
		AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));
		AssertAnnotations.assertSentence(new String[] {"Ceci est un bon test."}, JCasUtil.select(cas, Sentence.class));

	}
	
	
	

}
