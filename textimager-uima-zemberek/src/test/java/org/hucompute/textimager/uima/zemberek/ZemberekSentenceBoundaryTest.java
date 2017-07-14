package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.zemberek.ZemberekSentenceBoundary;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

/**
* ZemberekSentenceBoundaryTest
*
* @date 17.06.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Sentence-Boundary Test. Test if the sentence is generated correctly.
*
*/
public class ZemberekSentenceBoundaryTest {
	
	/**
	 * Test with JUnit if the token is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testTokenizer() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create a new Engine Description for the Sentence-Boundary-Detection.
		AnalysisEngineDescription sentanceBoundAnnotator = createEngineDescription(ZemberekSentenceBoundary.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, sentanceBoundAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul, alo! | Ne çok az Türk konuşabilir yazık. | ";
		String outputCorrectBegin = "0 | 15 | ";
		String outputCorrectEnd = "14 | 48 | ";
		
		// Generate Text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		
		// Loop over different sentences and create the test text.
		for (Sentence sentence : select(inputCas, Sentence.class)) {
			outputTestToken = outputTestToken + sentence.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + sentence.getBegin() + " | ";
			outputTestEnd = outputTestEnd + sentence.getEnd() + " | ";
        }
		
		// JUnit Test for Sentence, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
}
