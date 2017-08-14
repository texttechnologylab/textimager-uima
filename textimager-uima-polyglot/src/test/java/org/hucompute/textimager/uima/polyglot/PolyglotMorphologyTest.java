package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import morphemeAnnotation.type.MorphemeAnnotation;

/**
* PolyglotMorphologyTest
*
* @date 09.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages. 
*/
public class PolyglotMorphologyTest {
	
	/**
	 * Test with JUnit if the morphemes are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testMorphologyGerman() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription morphologyAnnotator = createEngineDescription(PolyglotMorphology.class, PolyglotMorphology.PARAM_PYTHON_PATH, "/usr/bin/python");
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Deutschland ist ein Bundesstaat in Mitteleuropa.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, morphologyAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Deutschland | ist | ein | Bundesstaat | in | Mitteleuropa | . | ";
		String outputCorrectValue = "[Deutsch][land] | [ist] | [ein] | [Bundes][staat] | [in] | [Mittel][europa] | [.] | ";
		String outputCorrectBegin = "0 | 12 | 16 | 20 | 32 | 35 | 47 | ";
		String outputCorrectEnd = "11 | 15 | 19 | 31 | 34 | 47 | 48 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different MorphemeAnnotation-Tags and create the UIMA-Output.
		for (MorphemeAnnotation morpheme : select(inputCas, MorphemeAnnotation.class)) {		
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
	 * Test with JUnit if the morphemes are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testMorphologyEnglish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription morphologyAnnotator = createEngineDescription(PolyglotMorphology.class, PolyglotMorphology.PARAM_PYTHON_PATH, "/usr/bin/python");
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("The Israeli Prime Minister Benjamin Netanyahu has warned that Iran poses a threat to the entire world.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, morphologyAnnotator);
		
		// Sample Text
		String outputCorrectToken = "The | Israeli | Prime | Minister | Benjamin | Netanyahu | has | warned | that | Iran | poses | a | threat | to | the | entire | world | . | ";
		String outputCorrectValue = "[The] | [Israeli] | [Prim][e] | [Mini][ster] | [Ben][jam][in] | [Net][any][ahu] | [ha][s] | [warn][ed] | [th][at] | [I][ran] | [pose][s] | [a] | [threat] | [to] | [the] | [entire] | [world] | [.] | ";
		String outputCorrectBegin = "0 | 4 | 12 | 18 | 27 | 36 | 46 | 50 | 57 | 62 | 67 | 73 | 75 | 82 | 85 | 89 | 96 | 101 | ";
		String outputCorrectEnd = "3 | 11 | 17 | 26 | 35 | 45 | 49 | 56 | 61 | 66 | 72 | 74 | 81 | 84 | 88 | 95 | 101 | 102 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different MorphemeAnnotation-Tags and create the UIMA-Output.
		for (MorphemeAnnotation morpheme : select(inputCas, MorphemeAnnotation.class)) {		
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
	 * Test with JUnit if the morphemes are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testMorphologyTurkish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription morphologyAnnotator = createEngineDescription(PolyglotMorphology.class, PolyglotMorphology.PARAM_PYTHON_PATH, "/usr/bin/python");
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("İstanbul, alo! Ne çok az Türk konuşabilir yazık.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, morphologyAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | Ne | çok | az | Türk | konuşabilir | yazık | . | ";
		String outputCorrectValue = "[İstanbul] | [,] | [al][o] | [!] | [Ne] | [çok] | [az] | [Türk] | [konuş][abilir] | [yazı][k] | [.] | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different MorphemeAnnotation-Tags and create the UIMA-Output.
		for (MorphemeAnnotation morpheme : select(inputCas, MorphemeAnnotation.class)) {		
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
