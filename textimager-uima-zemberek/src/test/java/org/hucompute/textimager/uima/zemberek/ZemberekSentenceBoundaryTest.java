package org.hucompute.textimager.uima.zemberek;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.zemberek.ZemberekSentenceBoundary;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;

/**
* ZemberekSentenceBoundaryTest
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide several test cases for turkish language.
*/
public class ZemberekSentenceBoundaryTest {

	/**
	 * Test with JUnit if the sentences are generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testSentenceBoundary() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String testText = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";

		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(ZemberekSentenceBoundary.class);

		// Create a new JCas - "Holder"-Class for Annotation.
		JCas inputCas = JCasFactory.createJCas();

		// Input
		inputCas.setDocumentText(testText);

		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, sentenceAnnotator);

		// Sample Text
		String outputCorrectToken = "İstanbul, alo! | Ne çok az Türk konuşabilir yazık. | ";
		String outputCorrectBegin = "0 | 15 | ";
		String outputCorrectEnd = "14 | 48 | ";

		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";

		// Loop over different sentences and create the UIMA-Output.
		for (Sentence sentence : select(inputCas, Sentence.class)) {
			outputTestToken = outputTestToken + sentence.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + sentence.getBegin() + " | ";
			outputTestEnd = outputTestEnd + sentence.getEnd() + " | ";
        }

		// JUnit-Test: Sentence, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
