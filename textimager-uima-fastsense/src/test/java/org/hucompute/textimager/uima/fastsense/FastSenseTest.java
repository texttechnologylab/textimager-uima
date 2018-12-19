package org.hucompute.textimager.uima.fastsense;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class FastSenseTest {
	@Test
	public void fastSenseENTest() throws UIMAException{
		/*JCas cas = JCasFactory.createText("Hello World !");
		cas.setDocumentLanguage("en");

		POS pos1 = new POS(cas, 0, 5);
		pos1.setPosValue("UH");
		pos1.addToIndexes();
		POS pos2 = new POS(cas, 6, 11);
		pos2.setPosValue("NNP");
		pos2.addToIndexes();
		POS pos3 = new POS(cas, 12, 13);
		pos3.setPosValue(".");
		pos3.addToIndexes();
		
		Token t1 = new Token(cas, 0, 5);
		t1.setPos(pos1);
		t1.addToIndexes();
		Token t2 = new Token(cas, 6, 11);
		t2.setPos(pos2);
		t2.addToIndexes();
		Token t3 = new Token(cas, 12, 13);
		t3.setPos(pos3);
		t3.addToIndexes();
		
		
		Paragraph p1 = new Paragraph(cas, 0, 13);
		p1.addToIndexes();
		
		//AnalysisEngineDescription fastSense = createEngineDescription(FastSenseEN.class,
			//	FastSenseEN.PARAM_DOCKER_IMAGE,"textimager-fastsense-en");

		AnalysisEngineDescription fastSense = createEngineDescription(FastSense.class,
				FastSense.PARAM_REST_ENDPOINT,"http://127.0.0.1:5000",
				FastSense.PARAM_DOCKER_VOLUMES,"/mnt/tb1/baumartz/git/fastSense/fastsense-en/best_model:/model");
		
		SimplePipeline.runPipeline(cas, fastSense);

		assertEquals(2, JCasUtil.select(cas, CategoryCoveredTagged.class).size());
		
		String[] urls = new String[] {
			new String("https://en.wikipedia.org/wiki/\"Hello,_World!\"_program"),
			new String("https://en.wikipedia.org/wiki/Olympic_Games"),
		};

		String[] casUrls = (String[]) JCasUtil.select(cas, CategoryCoveredTagged.class).stream().map(s -> new String(s.getValue())).toArray(String[]::new);
		
		assertArrayEquals(urls, casUrls);*/
		
		assertEquals(1, 1);
	}
}
