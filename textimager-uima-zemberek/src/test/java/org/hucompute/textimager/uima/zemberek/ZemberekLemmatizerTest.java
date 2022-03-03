package org.hucompute.textimager.uima.zemberek;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.zemberek.ZemberekLemmatizer;
import org.hucompute.textimager.uima.zemberek.ZemberekTokenizerDefault;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;

/**
* ZemberekLemmatizerTest
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide several test cases for turkish language.
*/
public class ZemberekLemmatizerTest {

	/**
	 * Test with JUnit if the lemmas are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testLemmatizer_1() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String testText = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";

		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		AnalysisEngineDescription lemmaAnnotator = createEngineDescription(ZemberekLemmatizer.class);

		// Create a new JCas - "Holder"-Class for Annotation.
		JCas inputCas = JCasFactory.createJCas();

		// Input
		inputCas.setDocumentText(testText);

		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, lemmaAnnotator);

		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | ne | çok | az | Türk | konuşmak | yazık | . | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";

		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";

		// Loop over different lemmas and create the UIMA-Output.
		for (Lemma lemma : select(inputCas, Lemma.class)) {
			outputTestToken = outputTestToken + lemma.getValue() + " | ";
			outputTestBegin = outputTestBegin + lemma.getBegin() + " | ";
			outputTestEnd = outputTestEnd + lemma.getEnd() + " | ";
        }

		// JUnit-Test: Lemma, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}

	/**
	 * Test with JUnit if the lemmas are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testLemmatizer_2() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String testText = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";

		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerAll.class);
		AnalysisEngineDescription lemmaAnnotator = createEngineDescription(ZemberekLemmatizer.class);

		// Create a new JCas - "Holder"-Class for Annotation.
		JCas inputCas = JCasFactory.createJCas();

		// Input
		inputCas.setDocumentText(testText);

		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, lemmaAnnotator);

		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | ne | çok | az | Türk | konuşmak | yazık | . | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";

		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";

		// Loop over different lemma and create the UIMA-Output.
		for (Lemma lemma : select(inputCas, Lemma.class)) {
			outputTestToken = outputTestToken + lemma.getValue() + " | ";
			outputTestBegin = outputTestBegin + lemma.getBegin() + " | ";
			outputTestEnd = outputTestEnd + lemma.getEnd() + " | ";
        }

		// JUnit-Test: Lemma, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
