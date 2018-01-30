package org.hucompute.textimager.uima.marmot;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertPOS;

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

public class MarMoTTaggerTest {

	@Test
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
				MarMoTTagger.class
//				,MarMoTTagger.PARAM_MODEL_LOCATION,"classpath:/org/hucompute/textimager/uima/marmot/test/de.marmot"
				,MarMoTTagger.PARAM_MODEL_LOCATION,"http://cistern.cis.lmu.de/marmot/models/CURRENT/spmrl/de.marmot"

				,MarMoTTagger.PARAM_POS_MAPPING_LOCATION,"classpath:/org/hucompute/textimager/uima/marmot/lib/pos-de-pretrained.map"
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		

		assertPOS(
				new String[] { "PR", "V", "ART", "ADJ", "NN","PUNC"},
				new String[] { "PDS|PDS", "VAFIN|VAFIN", "ART|ART", "ADJA|ADJA", "NN|NN","$.|$."}, 
				JCasUtil.select(cas, POS.class));	
	}
	
//	@Test
//	public void simpleExampleLa() throws UIMAException{
//		JCas cas = JCasFactory.createText("Magnus stultus est.", "la");
//		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();
//
//		new Token(cas,0,6).addToIndexes();
//		new Token(cas,7,14).addToIndexes();
//		new Token(cas,15,18).addToIndexes();
//		new Token(cas,18,19).addToIndexes();
//
//		AggregateBuilder builder = new AggregateBuilder();
//		
//		builder.add(createEngineDescription(
//				MarMoTTagger.class
////				,MarMoTTagger.PARAM_MODEL_LOCATION,"http://cistern.cis.lmu.de/marmot/models/CURRENT/spmrl/de.marmot"
////				,MarMoTTagger.PARAM_POS_MAPPING_LOCATION,"classpath:/org/hucompute/textimager/uima/marmot/lib/pos-de-pretrained.map"
//				));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		
//
//		assertPOS(
//				new String[] { "PR", "V", "ART", "ADJ", "NN","PUNC"},
//				new String[] { "NN", "V", "$."}, 
//				JCasUtil.select(cas, POS.class));	
//	}

}