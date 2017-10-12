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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
* ZemberekTokenizerTest
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide several test cases for turkish language. 
*/
public class ZemberekTokenizerTest {
	
	/**
	 * Test with JUnit if the tokens with ZemberekTokenizerDefault are generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testTokenizerDefault() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String testText = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(testText);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | Ne | çok | az | Türk | konuşabilir | yazık | . | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		
		// Loop over different token and create the UIMA-Output.
		for (Token token : select(inputCas, Token.class)) {
			outputTestToken = outputTestToken + token.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + token.getBegin() + " | ";
			outputTestEnd = outputTestEnd + token.getEnd() + " | ";
        }

		// JUnit-Test: Token, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the tokens with ZemberekTokenizerAll are generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testTokenizerAll() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String testText = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerAll.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(testText);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | Ne | çok | az | Türk | konuşabilir | yazık | . | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		
		// Loop over different token and create the UIMA-Output.
		for (Token token : select(inputCas, Token.class)) {
			outputTestToken = outputTestToken + token.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + token.getBegin() + " | ";
			outputTestEnd = outputTestEnd + token.getEnd() + " | ";
        }

		// JUnit-Test: Token, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if ZemberekTokenizerDefault and ZemberekTokenizerAll have any differences in the same testText.
	 * @throws Exception
	 */
	@Test
	public void testDifferencesInTokenization() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String testText = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(testText);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator);
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		// Generated text with library
		String outputTestToken_2 = "";
		String outputTestBegin_2 = "";
		String outputTestEnd_2 = "";
		
		// Loop over different token and create the UIMA-Output.
		for (Token token : select(inputCas, Token.class)) {
			outputTestToken = outputTestToken + token.getCoveredText() + " | ";
			outputTestBegin = outputTestBegin + token.getBegin() + " | ";
			outputTestEnd = outputTestEnd + token.getEnd() + " | ";
        }
		
		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator_2 = createEngineDescription(ZemberekTokenizerAll.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas_2 = JCasFactory.createJCas();
		
		// Input
		inputCas_2.setDocumentText(testText);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas_2, tokenAnnotator_2);
		
		// Loop over different token and create the UIMA-Output.
		for (Token token : select(inputCas_2, Token.class)) {
			outputTestToken_2 = outputTestToken_2 + token.getCoveredText() + " | ";
			outputTestBegin_2 = outputTestBegin_2 + token.getBegin() + " | ";
			outputTestEnd_2 = outputTestEnd_2 + token.getEnd() + " | ";
	    }
		
		// JUnit-Test: Token, Begin, End
		assertEquals(outputTestToken, outputTestToken_2);
		assertEquals(outputTestBegin, outputTestBegin_2);
		assertEquals(outputTestEnd, outputTestEnd_2);
	}
}
