package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.zemberek.ZemberekStemmer;
import org.hucompute.textimager.uima.zemberek.ZemberekTokenizerDefault;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;

/**
* ZemberekStemmerTest
*
* @date 23.06.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Stemmer Test. Test if the stem is generated correctly.
*
*/
public class ZemberekStemmerTest {
	
	/**
	 * Test with JUnit if the stem is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		// Create a new Engine Description for the Lemmatizer.
		AnalysisEngineDescription stemAnnotator = createEngineDescription(ZemberekStemmer.class);
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, stemAnnotator);
		
		// Sample Text
		String outputCorrectToken = "istanbul | , | alo | ! | ne | çok | az | türk | konuş | yazık | . | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 18 | 22 | 25 | 30 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 21 | 24 | 29 | 41 | 47 | 48 | ";
		
		// Generate Text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different stems and create the test text.
		for (Stem stem : select(inputCas, Stem.class)) {		
			outputTestToken = outputTestToken + stem.getValue() + " | ";
			outputTestBegin = outputTestBegin + stem.getBegin() + " | ";
			outputTestEnd = outputTestEnd + stem.getEnd() + " | ";
        }
		
		// JUnit Test for Stem, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
}
