package org.hucompute.textimager.uima.polyglot;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.assertEquals;

/**
* PolyglotSentenceBoundaryTest
*
* @date 08.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages.
*/
public class PolyglotSentenceBoundaryTest {

	/**
	 * Test with JUnit if the sentences are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testSentenceBoundaryGerman() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");

		// Create a new JCas - "Holder"-Class for Annotation.
		JCas inputCas = JCasFactory.createJCas();

		// Input
		inputCas.setDocumentText("Ein kurzer Satz um Polyglot zu testen. Die Bibliothek funktioniert!");

		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator);

		// Sample Text
		String outputCorrectToken = "Ein kurzer Satz um Polyglot zu testen. | Die Bibliothek funktioniert! | ";
		String outputCorrectBegin = "0 | 39 | ";
		String outputCorrectEnd = "38 | 67 | ";

		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";

		// Loop over different Sentence-Tags and create the UIMA-Output.
		for (Sentence sentence : select(inputCas, Sentence.class)) {
			outputTestToken = outputTestToken + sentence.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + sentence.getBegin() + " | ";
			outputTestEnd = outputTestEnd + sentence.getEnd() + " | ";
        }

		// JUnit-Test: CoveredText, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}

	/**
	 * Test with JUnit if the sentences are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testSentenceBoundaryTurkish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");

		// Create a new JCas - "Holder"-Class for Annotation.
		JCas inputCas = JCasFactory.createJCas();

		// Input
		inputCas.setDocumentText("İstanbul, alo! Ne çok az Türk konuşabilir yazık.");

		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator);

		// Sample Text
		String outputCorrectToken = "İstanbul, alo! | Ne çok az Türk konuşabilir yazık. | ";
		String outputCorrectBegin = "0 | 15 | ";
		String outputCorrectEnd = "14 | 48 | ";

		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";

		// Loop over different Sentence-Tags and create the UIMA-Output.
		for (Sentence sentence : select(inputCas, Sentence.class)) {
			outputTestToken = outputTestToken + sentence.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + sentence.getBegin() + " | ";
			outputTestEnd = outputTestEnd + sentence.getEnd() + " | ";
        }

		// JUnit-Test: CoveredText, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
