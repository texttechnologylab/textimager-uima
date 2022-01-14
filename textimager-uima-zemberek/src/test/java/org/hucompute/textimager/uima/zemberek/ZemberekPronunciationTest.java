package org.hucompute.textimager.uima.zemberek;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.zemberek.ZemberekTokenizerDefault;
import org.junit.Test;
import pronunciationAnnotation.type.PronunciationAnnotation;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;

/**
* ZemberekPronunciationTest
*
* @date 10.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Pronunciation Test. Test if the analysis is generated correctly.
*
*/
public class ZemberekPronunciationTest {

	/**
	 * Test with JUnit if the analysis is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testPronunciation() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";

		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		// Create a new Engine Description for the Disambiguation.
		AnalysisEngineDescription pronunciationAnnotator = createEngineDescription(ZemberekPronunciation.class);

		// Create a new JCas - "Holder"-Class for Annotation.
		JCas inputCas = JCasFactory.createJCas();

		// Input
		inputCas.setDocumentText(text);

		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, pronunciationAnnotator);

		// Sample Text
		String outputCorrectValue = "istanbul | a | alo | a | ne | çok | az | türk | konuşabilir | yazık | a | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";

		// Generate Text with library
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";

		// Loop over different pronunciation and create the test text.
		for (PronunciationAnnotation pronunciation : select(inputCas, PronunciationAnnotation.class)) {
			outputTestValue = outputTestValue + pronunciation.getValue() + " | ";
			outputTestBegin = outputTestBegin + pronunciation.getBegin() + " | ";
			outputTestEnd = outputTestEnd + pronunciation.getEnd() + " | ";
        }

		// JUnit Test for Value, Begin, End
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}

}
