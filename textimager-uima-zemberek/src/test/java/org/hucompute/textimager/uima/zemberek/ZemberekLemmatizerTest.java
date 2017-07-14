package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.zemberek.ZemberekLemmatizer;
import org.hucompute.textimager.uima.zemberek.ZemberekTokenizerDefault;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;

/**
* ZemberekLemmatizerTest
*
* @date 02.06.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Lemmatizer Test. Test if the lemma is generated correctly.
*
*/
public class ZemberekLemmatizerTest {
	
	/**
	 * Test with JUnit if the lemma is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		// Create a new Engine Description for the Lemmatizer.
		AnalysisEngineDescription lemmaAnnotator = createEngineDescription(ZemberekLemmatizer.class);
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, lemmaAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | , | alo | ! | ne | çok | az | Türk | konuşmak | yazık | . | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generate Text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different lemma and create the test text.
		for (Lemma lemma : select(inputCas, Lemma.class)) {			
			outputTestToken = outputTestToken + lemma.getValue() + " | ";
			outputTestBegin = outputTestBegin + lemma.getBegin() + " | ";
			outputTestEnd = outputTestEnd + lemma.getEnd() + " | ";
        }
		
		// JUnit Test for Lemma, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
}
