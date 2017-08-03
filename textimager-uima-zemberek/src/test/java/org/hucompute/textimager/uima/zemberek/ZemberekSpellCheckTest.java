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

import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly;

/**
* ZemberekSpellCheckTest
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide several test cases for turkish language. 
*/
public class ZemberekSpellCheckTest {
	
	/**
	 * Test with JUnit if the annotation is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		// Wir hatten gelesen (falsch)
		String text = "okumuştk";
		
		// Create new AnalysisEngineDescription
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		AnalysisEngineDescription spellAnnotator = createEngineDescription(ZemberekSpellChecker.class);
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, spellAnnotator);
		
		// Sample Text
		String outputCorrectToken = "okumuştu | Okumuş'ta | okumuşta | okumuştuk | Okumuş'tuk | Okumuş'tu | ";
		
		// Generated text with library
		String outputTestToken = "";
		String visualOutput = "";
		
		// Loop over different anomalies and create the UIMA-Output.
		for (SpellingAnomaly anomaly : select(inputCas, SpellingAnomaly.class)) {			
			for(int i = 0; i < anomaly.getSuggestions().size(); i++) {
				outputTestToken = outputTestToken + anomaly.getSuggestions(i).getReplacement() + " | ";
				visualOutput = visualOutput + anomaly.getSuggestions(i) + " \n ";
			}			
        }
		
		// Visual Output
		System.out.println(visualOutput);
		
		// JUnit Test for Anomaly
		assertEquals(outputCorrectToken, outputTestToken);
	}
}
