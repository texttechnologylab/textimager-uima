package org.hucompute.textimager.uima.allennlp;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class AllenNLPNERTest {
	@Test
	public void simpleExampleLa() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein iPhone von Apple");
		cas.setDocumentLanguage("de");
		
		Token t1 = new Token(cas, 0, 3);
		t1.addToIndexes();
		Token t2 = new Token(cas, 4, 7);
		t2.addToIndexes();
		Token t3 = new Token(cas, 8, 11);
		t3.addToIndexes();
		Token t4 = new Token(cas, 12, 18);
		t4.addToIndexes();
		Token t5 = new Token(cas, 19, 22);
		t5.addToIndexes();
		Token t6 = new Token(cas, 23, 28);
		t6.addToIndexes();
		
		AnalysisEngineDescription spacyNer = createEngineDescription(AllenNLPNER.class);
		
		SimplePipeline.runPipeline(cas, spacyNer);
		
		String[] ents = new String[] { "MISC", "ORG" };

		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);
		
		assertArrayEquals(ents, casEnts);
	}
}
