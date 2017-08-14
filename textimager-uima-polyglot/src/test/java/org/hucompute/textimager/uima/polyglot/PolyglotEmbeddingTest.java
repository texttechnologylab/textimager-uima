package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import embeddingAnnotation.type.EmbeddingAnnotation;
import sentimentAnnotation.type.SentimentAnnotation;

/**
* PolyglotEmbeddingTest
*
* @date 11.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages. 
*/
public class PolyglotEmbeddingTest {
	
	/**
	 * Test with JUnit if the Embedding is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testEmbeddingEnglish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription embeddingAnnotator = createEngineDescription(PolyglotEmbedding.class, PolyglotEmbedding.PARAM_PYTHON_PATH, "/usr/bin/python");
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Bush");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, embeddingAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Bush | Bush | Bush | Bush | Bush | Bush | Bush | Bush | Bush | Bush | ";
		String outputCorrectValue = "Kennedy | Nixon | Reagan | Buchanan | Carter | Foster | Butler | Churchill | Fisher | Roosevelt | ";
		String outputCorrectDistance = "1.62344777584 | 1.74774956703 | 1.77307474613 | 1.78913903236 | 1.81321394444 | 1.85869824886 | 1.79632925987 | 1.77311480045 | 1.76201224327 | 1.69560539722 | ";
		String outputCorrectBegin = "0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | ";
		String outputCorrectEnd = "4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestDistance = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different EmbeddingAnnotation-Tags and create the UIMA-Output.
		for (EmbeddingAnnotation embedding : select(inputCas, EmbeddingAnnotation.class)) {		
			outputTestToken = outputTestToken + embedding.getCoveredText() + " | ";
			outputTestValue = outputTestValue + embedding.getValue() + " | ";
			outputTestDistance = outputTestDistance + embedding.getDistance() + " | ";
			outputTestBegin = outputTestBegin + embedding.getBegin() + " | ";
			outputTestEnd = outputTestEnd + embedding.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectDistance, outputTestDistance);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the Embedding is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testEmbeddingTurkish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription embeddingAnnotator = createEngineDescription(PolyglotEmbedding.class, PolyglotEmbedding.PARAM_PYTHON_PATH, "/usr/bin/python");
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("İstanbul");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, embeddingAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | İstanbul | İstanbul | İstanbul | İstanbul | İstanbul | İstanbul | İstanbul | İstanbul | İstanbul | ";
		String outputCorrectValue = "Ankara | Bursa | Türkiye | Trabzon | Anadolu | Sivas | Konya | Antalya | Adana | İzmir | ";
		String outputCorrectDistance = "0.632657766342 | 0.895214557648 | 1.0263671875 | 1.0481801033 | 1.08347678185 | 1.10165452957 | 1.06834173203 | 1.04222285748 | 0.979541301727 | 0.799590826035 | ";
		String outputCorrectBegin = "0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | ";
		String outputCorrectEnd = "8 | 8 | 8 | 8 | 8 | 8 | 8 | 8 | 8 | 8 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestDistance = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different EmbeddingAnnotation-Tags and create the UIMA-Output.
		for (EmbeddingAnnotation embedding : select(inputCas, EmbeddingAnnotation.class)) {		
			outputTestToken = outputTestToken + embedding.getCoveredText() + " | ";
			outputTestValue = outputTestValue + embedding.getValue() + " | ";
			outputTestDistance = outputTestDistance + embedding.getDistance() + " | ";
			outputTestBegin = outputTestBegin + embedding.getBegin() + " | ";
			outputTestEnd = outputTestEnd + embedding.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectDistance, outputTestDistance);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the Embedding is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testEmbeddingGerman() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription embeddingAnnotator = createEngineDescription(PolyglotEmbedding.class, PolyglotEmbedding.PARAM_PYTHON_PATH, "/usr/bin/python");
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Das ist ein Test");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, embeddingAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Das | Das | Das | Das | Das | Das | Das | Das | Das | Das | ist | ist | ist | ist | ist | ist | ist | ist | ist | ist | ein | ein | ein | ein | ein | ein | ein | ein | ein | ein | Test | Test | Test | Test | Test | Test | Test | Test | Test | Test | ";
		String outputCorrectValue = "GeschichteDas | DOUBLEDas | InhaltDas | GeographieDas | Fürs | DOUBLEDieses | HintergrundDas | Welches | Jedes | Ins | war | gilt | kommt | ging | scheint | sieht | gehört | wäre | blieb | sei | kein | traditionellem | irgendein | sein | DOUBLEein | dein | Ein | einem | unser | mein | Computer | Podcast | Support | Service | Prozess | Plus | Monitor | Mix | Index | Filter | ";
		String outputCorrectDistance = "3.00948834419 | 3.16462922096 | 3.32779860497 | 3.37165164948 | 3.5030400753 | 3.51446127892 | 3.47742080688 | 3.36886000633 | 3.31532430649 | 3.16177010536 | 1.24638020992 | 1.8853610754 | 2.10549139977 | 2.11906385422 | 2.1361579895 | 2.16822052002 | 2.04022407532 | 1.75557351112 | 1.8490114212 | 1.7011693716 | 2.28548908234 | 3.51742768288 | 3.56523299217 | 3.45461940765 | 3.30121517181 | 3.36318397522 | 3.38644337654 | 3.21090984344 | 3.14471411705 | 3.11619019508 | 1.63393223286 | 1.85646951199 | 1.89169514179 | 1.8948700428 | 1.91993045807 | 1.93044936657 | 1.81176435947 | 1.81528818607 | 1.78591549397 | 1.70006525517 | ";
		String outputCorrectBegin = "0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 4 | 8 | 8 | 8 | 8 | 8 | 8 | 8 | 8 | 8 | 8 | 12 | 12 | 12 | 12 | 12 | 12 | 12 | 12 | 12 | 12 | ";
		String outputCorrectEnd = "3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 3 | 7 | 7 | 7 | 7 | 7 | 7 | 7 | 7 | 7 | 7 | 11 | 11 | 11 | 11 | 11 | 11 | 11 | 11 | 11 | 11 | 16 | 16 | 16 | 16 | 16 | 16 | 16 | 16 | 16 | 16 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestDistance = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different EmbeddingAnnotation-Tags and create the UIMA-Output.
		for (EmbeddingAnnotation embedding : select(inputCas, EmbeddingAnnotation.class)) {		
			outputTestToken = outputTestToken + embedding.getCoveredText() + " | ";
			outputTestValue = outputTestValue + embedding.getValue() + " | ";
			outputTestDistance = outputTestDistance + embedding.getDistance() + " | ";
			outputTestBegin = outputTestBegin + embedding.getBegin() + " | ";
			outputTestEnd = outputTestEnd + embedding.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectDistance, outputTestDistance);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
