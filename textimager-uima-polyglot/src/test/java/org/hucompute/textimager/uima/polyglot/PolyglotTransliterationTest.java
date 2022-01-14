package org.hucompute.textimager.uima.polyglot;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import tansliterationAnnotation.type.TransliterationAnnotation;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.assertEquals;

/**
* PolyglotTransliterationTest
*
* @date 10.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages.
*/
public class PolyglotTransliterationTest {

	/**
	 * Test with JUnit if the transliteration is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testTransliterationArabian() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription transliterationAnnotator = createEngineDescription(PolyglotTransliteration.class, PolyglotTransliteration.PARAM_PYTHON_PATH, "/usr/bin/python", PolyglotTransliteration.PARAM_TO_LANGUAGE_CODE, "ar");

		// Create a new JCas - "Holder"-Class for Annotation.
		JCas inputCas = JCasFactory.createJCas();

		// Input
		inputCas.setDocumentText("Das Essen gestern Nacht hat großartig geschmeckt, obwohl der Fisch schlecht.");

		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, transliterationAnnotator);

		// Sample Text
		String outputCorrectToken = "Das | Essen | gestern | Nacht | hat | großartig | geschmeckt | , | obwohl | der | Fisch | schlecht | ";
		String outputCorrectValue = "داس | يسين | جتيرن | ناكهت | هات |  | جكهميكت |  | وبووهل | دير | فيش | شليكهت | ";
		String outputCorrectBegin = "0 | 4 | 10 | 18 | 24 | 28 | 38 | 48 | 50 | 57 | 61 | 67 | ";
		String outputCorrectEnd = "3 | 9 | 17 | 23 | 27 | 37 | 48 | 49 | 56 | 60 | 66 | 75 | ";

		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";

		// Loop over different TransliterationAnnotation-Tags and create the UIMA-Output.
		for (TransliterationAnnotation transliteration : select(inputCas, TransliterationAnnotation.class)) {
			outputTestToken = outputTestToken + transliteration.getCoveredText() + " | ";
			outputTestValue = outputTestValue + transliteration.getValue() + " | ";
			outputTestBegin = outputTestBegin + transliteration.getBegin() + " | ";
			outputTestEnd = outputTestEnd + transliteration.getEnd() + " | ";
        }

		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
