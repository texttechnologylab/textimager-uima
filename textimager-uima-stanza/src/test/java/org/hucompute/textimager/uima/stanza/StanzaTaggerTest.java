package org.hucompute.textimager.uima.stanza;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class StanzaTaggerTest {
	@Test
	public void simpleExample() throws UIMAException, SAXException, IOException{
		JCas cas = JCasFactory.createText("Das ist   ein Test. Und noch      einer.  ");
		cas.setDocumentLanguage("de");
		
		Token t1 = new Token(cas, 0, 3);
		t1.addToIndexes();
		Token t2 = new Token(cas, 4, 7);
		t2.addToIndexes();
		Token t3 = new Token(cas, 10, 13);
		t3.addToIndexes();
		Token t4 = new Token(cas, 14, 18);
		t4.addToIndexes();
		Token t5 = new Token(cas, 18, 19);
		t5.addToIndexes();
		Token t6 = new Token(cas, 20, 23);
		t6.addToIndexes();
		Token t7 = new Token(cas, 24, 28);
		t7.addToIndexes();
		Token t8 = new Token(cas, 34, 39);
		t8.addToIndexes();
		Token t9 = new Token(cas, 39, 40);
		t9.addToIndexes();
		
		AnalysisEngineDescription stanzaTagger = createEngineDescription(StanzaTagger.class);
		
		stanzaTagger.toXML(new FileWriter(new File("StanzaTagger.xml")));
		
		SimplePipeline.runPipeline(cas, stanzaTagger);
		
		String[] pos = new String[] {
				"PRON", "AUX", "DET", "NOUN", "PUNCT",
				"CCONJ", "ADV", "PRON", "PUNCT"
		};

		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));

		assertArrayEquals(pos, casPos);
	}
	
//	@Test
//	public void multiTaggerTest() throws UIMAException{
//
//		JCas cas = JCasFactory.createText("Das ist ein IPhone von Apple.","de");
//
//		AnalysisEngineDescription spacyMulti = createEngineDescription(StanzaTagger.class,StanzaTagger.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/stanza");
//
//		SimplePipeline.runPipeline(cas, spacyMulti);
//
//		int[][] tokens = new int[][] {
//			new int[] { 0, 3 },
//			new int[] { 4, 7 },
//			new int[] { 8, 11 },
//			new int[] { 12, 18 },
//			new int[] { 19, 22 },
//			new int[] { 23, 28 },
//			new int[] { 28, 29 },
//		};
//		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);
//
//		String[] pos = new String[] {
//				"PDS", "VAFIN", "ART", "NN", "APPR", "NE", "$.",
//		};
//		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);
//
//		String[] deps = new String[] {
//				"SB","--", "NK", "PD", "PG","NK", "PUNCT",
//		};
//		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);
//
//		String[] ents = new String[] { "MISC","ORG"};
//		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		assertArrayEquals(tokens, casTokens);
//		assertArrayEquals(pos, casPos);
//		assertArrayEquals(deps, casDeps);
//		assertArrayEquals(ents, casEnts);
//	}

}