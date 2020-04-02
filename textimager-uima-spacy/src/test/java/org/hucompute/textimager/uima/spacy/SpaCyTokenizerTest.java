//package org.hucompute.textimager.uima.spacy;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//import static org.junit.Assert.assertArrayEquals;
//
//import org.apache.uima.UIMAException;
//import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.fit.util.JCasUtil;
//import org.apache.uima.jcas.JCas;
//import org.junit.Test;
//
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
//
//public class SpaCyTokenizerTest {
//	@Test
//	public void simpleExampleLa() throws UIMAException{
//		JCas cas = JCasFactory.createText("Das ist   ein Test. Und noch      einer.  ");
//		cas.setDocumentLanguage("de");
//
//		AnalysisEngineDescription spacyTokenizer = createEngineDescription(SpaCyTokenizer.class
//				,SpaCyParser.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy");
//		
//		SimplePipeline.runPipeline(cas, spacyTokenizer);
//		
//		int[][] tokens = new int[][] {
//			new int[] { 0, 3 },
//			new int[] { 4, 7 },
//			new int[] { 10, 13 },
//			new int[] { 14, 18 },
//			new int[] { 18, 19 },
//			new int[] { 20, 23 },
//			new int[] { 24, 28 },
//			new int[] { 34, 39 },
//			new int[] { 39, 40 },
//		};
//
//		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);
//		
//		assertArrayEquals(tokens, casTokens);
//	}
//}
