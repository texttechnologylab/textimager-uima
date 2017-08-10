package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import sentimentAnnotation.type.SentimentAnnotation;

/**
* PolyglotSentimentTest
*
* @date 09.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages. 
*/
public class PolyglotSentimentTest {
	
	/**
	 * Test with JUnit if the Sentiment is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testSentimentGerman() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		AnalysisEngineDescription polarityAnnotator = createEngineDescription(PolyglotSentiment.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Das Essen gestern Nacht hat großartig geschmeckt, obwohl der Fisch schlecht.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, polarityAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Das | Essen | gestern | Nacht | hat | großartig | geschmeckt | , | obwohl | der | Fisch | schlecht | . | ";
		String outputCorrectValue = "0 | 0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | 0 | -1 | 0 | ";
		String outputCorrectBegin = "0 | 4 | 10 | 18 | 24 | 28 | 38 | 48 | 50 | 57 | 61 | 67 | 75 | ";
		String outputCorrectEnd = "3 | 9 | 17 | 23 | 27 | 37 | 48 | 49 | 56 | 60 | 66 | 75 | 76 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different SentimentAnnotation-Tags and create the UIMA-Output.
		for (SentimentAnnotation sentiment : select(inputCas, SentimentAnnotation.class)) {		
			outputTestToken = outputTestToken + sentiment.getCoveredText() + " | ";
			outputTestValue = outputTestValue + sentiment.getValue() + " | ";
			outputTestBegin = outputTestBegin + sentiment.getBegin() + " | ";
			outputTestEnd = outputTestEnd + sentiment.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the Sentiment is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testSentimentEnglish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		AnalysisEngineDescription polarityAnnotator = createEngineDescription(PolyglotSentiment.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Barack Obama gave a fantastic speech last night.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, polarityAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Barack | Obama | gave | a | fantastic | speech | last | night | . | ";
		String outputCorrectValue = "0 | 0 | 0 | 0 | 1 | 0 | 0 | 0 | 0 | ";
		String outputCorrectBegin = "0 | 7 | 13 | 18 | 20 | 30 | 37 | 42 | 47 | ";
		String outputCorrectEnd = "6 | 12 | 17 | 19 | 29 | 36 | 41 | 47 | 48 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different SentimentAnnotation-Tags and create the UIMA-Output.
		for (SentimentAnnotation morpheme : select(inputCas, SentimentAnnotation.class)) {		
			outputTestToken = outputTestToken + morpheme.getCoveredText() + " | ";
			outputTestValue = outputTestValue + morpheme.getValue() + " | ";
			outputTestBegin = outputTestBegin + morpheme.getBegin() + " | ";
			outputTestEnd = outputTestEnd + morpheme.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the Sentiment is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testSentimentTurkish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		AnalysisEngineDescription polarityAnnotator = createEngineDescription(PolyglotSentiment.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("İstanbul, alo! Ne çok az Türk konuşabilir yazık.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, polarityAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | Ne | çok | az | Türk | konuşabilir | yazık | . | ";
		String outputCorrectValue = "0 | 0 | 0 | 0 | 0 | 0 | -1 | 0 | 0 | -1 | 0 | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different SentimentAnnotation-Tags and create the UIMA-Output.
		for (SentimentAnnotation morpheme : select(inputCas, SentimentAnnotation.class)) {		
			outputTestToken = outputTestToken + morpheme.getCoveredText() + " | ";
			outputTestValue = outputTestValue + morpheme.getValue() + " | ";
			outputTestBegin = outputTestBegin + morpheme.getBegin() + " | ";
			outputTestEnd = outputTestEnd + morpheme.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
