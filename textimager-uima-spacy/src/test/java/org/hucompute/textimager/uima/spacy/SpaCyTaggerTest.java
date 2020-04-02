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
//import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
//
//public class SpaCyTaggerTest {
//	@Test
//	public void simpleExampleLa() throws UIMAException{
//		JCas cas = JCasFactory.createText("Das ist   ein Test. Und noch      einer.  ");
//		cas.setDocumentLanguage("de");
//		
//		Token t1 = new Token(cas, 0, 3);
//		t1.addToIndexes();
//		Token t2 = new Token(cas, 4, 7);
//		t2.addToIndexes();
//		Token t3 = new Token(cas, 10, 13);
//		t3.addToIndexes();
//		Token t4 = new Token(cas, 14, 18);
//		t4.addToIndexes();
//		Token t5 = new Token(cas, 18, 19);
//		t5.addToIndexes();
//		Token t6 = new Token(cas, 20, 23);
//		t6.addToIndexes();
//		Token t7 = new Token(cas, 24, 28);
//		t7.addToIndexes();
//		Token t8 = new Token(cas, 34, 39);
//		t8.addToIndexes();
//		Token t9 = new Token(cas, 39, 40);
//		t9.addToIndexes();
//		
//		AnalysisEngineDescription spacyTagger = createEngineDescription(SpaCyTagger.class
//				,SpaCyParser.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy");
//		
//		SimplePipeline.runPipeline(cas, spacyTagger);
//		
//		String[] pos = new String[] {
//				"PDS", "VAFIN", "ART", "NN", "$.",
//				"KON", "ADV", "PIS", "$."
//		};
//
//		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);
//		
//		assertArrayEquals(pos, casPos);
//	}
//}
