package org.hucompute.textimager.uima.cltk;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.cltk.CLTKTokenizer;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class CLTKTokenizerTest {
	@Test
	public void simpleExampleLa() throws UIMAException{
		JCas cas = JCasFactory.createText("atque haec abuterque puerve paterne nihil.");
		cas.setDocumentLanguage("la");

		AnalysisEngineDescription cltkTokenizer = createEngineDescription(CLTKTokenizer.class,
				CLTKTokenizer.PARAM_DOCKER_IMAGE,"textimager-cltk");
		
		SimplePipeline.runPipeline(cas, cltkTokenizer);
		
		int[][] tokens = new int[][] {
			new int[] { 0, 5 },
			new int[] { 6, 10 },
			new int[] { 11, 17 },
			new int[] { 17, 20 },
			new int[] { 21, 25 },
			new int[] { 25, 27 },
			new int[] { 28, 35 },
			new int[] { 36, 41 },
			new int[] { 41, 42 }
		};
		
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		assertArrayEquals(tokens, casTokens);
	}
}
