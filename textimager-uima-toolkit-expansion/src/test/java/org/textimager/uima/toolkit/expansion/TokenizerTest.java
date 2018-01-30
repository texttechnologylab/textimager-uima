package org.textimager.uima.toolkit.expansion;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.fit.util.JCasUtil;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;


public class TokenizerTest {
	@Test 
	public void TokenizerJAP() throws UIMAException{ 
		JCas cas = JCasFactory.createText("それは良いテストです。", "ja");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(MeCabTokenizer.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	 
		AssertAnnotations.assertToken(new String[]{"それ", "は", "良い", "テスト","です", "。"}, JCasUtil.select(cas, Token.class));

		builder.add(createEngineDescription(OpenerProjectPOSTagger.class,
				OpenerProjectPOSTagger.PARAM_POS_MAPPING_LOCATION, 
				"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/pos-default.map"
				));
	}
}


