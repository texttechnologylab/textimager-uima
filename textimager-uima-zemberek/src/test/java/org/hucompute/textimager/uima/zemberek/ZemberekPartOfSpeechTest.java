package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.zemberek.ZemberekTokenizerDefault;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;

/**
* ZemberekPartOfSpeechTest
*
* @date 13.06.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish POS Test. Test if the POS is generated correctly.
*
*/
public class ZemberekPartOfSpeechTest {
	
	/**
	 * Test with JUnit if the POS is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testPOS() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		// Create a new Engine Description for the Sentence-Boundary-Detection.
		AnalysisEngineDescription sentanceBoundAnnotator = createEngineDescription(ZemberekSentenceBoundary.class);
		// Create a new Engine Description for the POS.
		AnalysisEngineDescription posAnnotator = createEngineDescription(ZemberekNewPartOfSpeech.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		inputCas.setDocumentLanguage("tr");
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, sentanceBoundAnnotator, posAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | Ne | çok | az | Türk | konuşabilir | yazık | . | ";
		String outputCorrectValue = "Noun:ProperNoun | Punctuation | Interjection | Punctuation | Adjective | Adverb | Adjective | Noun:ProperNoun | Verb | Noun | Punctuation | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generate Text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		for (POS pos : select(inputCas, POS.class)) {
			outputTestToken = outputTestToken + pos.getCoveredText() + " | ";
			outputTestValue = outputTestValue + pos.getPosValue() + " | ";
			outputTestBegin = outputTestBegin + pos.getBegin() + " | ";
			outputTestEnd = outputTestEnd + pos.getEnd() + " | ";
		}

		// JUnit Test for Token, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
}
