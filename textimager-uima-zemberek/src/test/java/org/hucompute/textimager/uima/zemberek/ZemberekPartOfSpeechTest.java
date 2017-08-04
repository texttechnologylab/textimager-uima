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
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide several test cases for turkish language. 
*/
public class ZemberekPartOfSpeechTest {
	
	/**
	 * Test with JUnit if the POS are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testPartOfSpeech() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String testText = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(ZemberekSentenceBoundary.class);
		AnalysisEngineDescription posAnnotator = createEngineDescription(ZemberekPartOfSpeech.class, ZemberekPartOfSpeech.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/zemberek/lib/pos-default.map");
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(testText);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, sentenceAnnotator, posAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | İstanbul | , | , | alo | alo | ! | ! | Ne | çok | az | ";
		String outputCorrectValue = "ProperNoun | Adjective | Adverb | Punctuation | Interjection | Adjective | ProperNoun | Punctuation | Verb | Noun | Punctuation | ";
		String outputCorrectBegin = "0 | 0 | 8 | 8 | 10 | 10 | 13 | 13 | 15 | 18 | 22 | ";
		String outputCorrectEnd = "8 | 8 | 9 | 9 | 13 | 13 | 14 | 14 | 17 | 21 | 24 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different POS-Tags and create the UIMA-Output.
		for (POS pos : select(inputCas, POS.class)) {		
			outputTestToken = outputTestToken + pos.getCoveredText() + " | ";
			outputTestValue = outputTestValue + pos.getPosValue() + " | ";
			outputTestBegin = outputTestBegin + pos.getBegin() + " | ";
			outputTestEnd = outputTestEnd + pos.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
