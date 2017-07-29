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

import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SuggestedAction;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;

/**
* ZemberekSpellCheckTest
*
* @date 28.07.2016
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Spellchecking Test. Test if the annotation is generated correctly.
*
*/
public class ZemberekSpellCheckTest {
	
	/**
	 * Test with JUnit if the annotation is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "okumuştk";
		
		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		// Create a new Engine Description for the SpellChecker.
		AnalysisEngineDescription spellAnnotator = createEngineDescription(ZemberekSpellChecker.class);
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, spellAnnotator);
		
		// Sample text
		String outputCorrectToken = "okumuştu | Okumuş'ta | okumuşta | okumuştuk | Okumuş'tuk | Okumuş'tu | ";
		
		// Generate text with library
		String outputTestToken = "";
		String visualOutput = "";
		
		// Loop over different anomalies and create the test text.
		for (SpellingAnomaly anomaly : select(inputCas, SpellingAnomaly.class)) {			
			for(int i = 0; i < anomaly.getSuggestions().size(); i++) {
				outputTestToken = outputTestToken + anomaly.getSuggestions(i).getReplacement() + " | ";
				visualOutput = visualOutput + anomaly.getSuggestions(i) + " \n ";
			}			
        }
		
		System.out.println(visualOutput);
		
		// JUnit Test for Anomaly
		assertEquals(outputCorrectToken, outputTestToken);
	}
	
}
