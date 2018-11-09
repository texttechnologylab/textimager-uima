package org.hucompute.textimager.uima.cltk;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class CLTKSegmenterTest {
	@Test
	public void simpleExampleLa() throws UIMAException{
		JCas cas = JCasFactory.createText("atque haec abuterque puerve paterne nihil. atque haec abuterque puerve paterne nihil.");
		cas.setDocumentLanguage("la");

		AnalysisEngineDescription cltkSegmenter = createEngineDescription(CLTKSegmenter.class,
				CLTKSegmenter.PARAM_DOCKER_IMAGE,"textimager-cltk");
		
		SimplePipeline.runPipeline(cas, cltkSegmenter);
		
		int[][] tokens = new int[][] {
			new int[] { 0, 5 },
			new int[] { 6, 10 },
			new int[] { 11, 17 },
			new int[] { 17, 20 },
			new int[] { 21, 25 },
			new int[] { 25, 27 },
			new int[] { 28, 35 },
			new int[] { 36, 41 },
			new int[] { 41, 42 },

			new int[] { 43+0, 43+5 },
			new int[] { 43+6, 43+10 },
			new int[] { 43+11, 43+17 },
			new int[] { 43+17, 43+20 },
			new int[] { 43+21, 43+25 },
			new int[] { 43+25, 43+27 },
			new int[] { 43+28, 43+35 },
			new int[] { 43+36, 43+41 },
			new int[] { 43+41, 43+42 }
		};
		
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		assertArrayEquals(tokens, casTokens);
	}
}
