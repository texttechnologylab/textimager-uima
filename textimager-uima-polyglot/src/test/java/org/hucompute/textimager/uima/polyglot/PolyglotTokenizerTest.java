package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
* PolyglotTokenizerTest
*
* @date 08.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages. 
*/
public class PolyglotTokenizerTest {
	
	/**
	 * Test with JUnit if the Tokens are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testPartOfSpeechGerman() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Ein kurzer Satz um Polyglot zu testen.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Ein | kurzer | Satz | um | Polyglot | zu | testen | . | ";
		String outputCorrectBegin = "0 | 4 | 11 | 16 | 19 | 28 | 31 | 37 | ";
		String outputCorrectEnd = "3 | 10 | 15 | 18 | 27 | 30 | 37 | 38 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different POS-Tags and create the UIMA-Output.
		for (Token token : select(inputCas, Token.class)) {		
			outputTestToken = outputTestToken + token.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + token.getBegin() + " | ";
			outputTestEnd = outputTestEnd + token.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the Tokens are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testPartOfSpeechTurkish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("İstanbul, alo! Ne çok az Türk konuşabilir yazık.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | Ne | çok | az | Türk | konuşabilir | yazık | . | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different POS-Tags and create the UIMA-Output.
		for (Token token : select(inputCas, Token.class)) {		
			outputTestToken = outputTestToken + token.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + token.getBegin() + " | ";
			outputTestEnd = outputTestEnd + token.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
