package org.hucompute.textimager.uima.turkish_deasciifier;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.turkish_deasciifier.TurkishDeasciifierText;
import org.junit.Test;

import deasciifiedAnnotation.type.DeasciifiedAnnotation;

/**
* DeasciifierTest
*
* @date 12.07.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Deasciifier Test. Test if the deasciified text is generated correctly.
*
*/
public class TurkishDeasciifierTextTest {
	
	/**
	 * Test with JUnit if the text is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testLemma() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "Instanbul, alo! Ne cok az Türk konusabilir yazik.";
		
		// Create a new Engine Description for the Deasciifier.
		AnalysisEngineDescription deasciifiedAnnotator = createEngineDescription(TurkishDeasciifierText.class);
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, deasciifiedAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Instanbul, alo! Ne çok az Türk konuşabilir yazık. | ";
		String outputCorrectBegin = "0 | ";
		String outputCorrectEnd = "49 | ";
		
		// Generate Text with library
		String outputTestToken = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different text and create the test text.
		for (DeasciifiedAnnotation deasciifiedText : select(inputCas, DeasciifiedAnnotation.class)) {			
			outputTestToken = outputTestToken + deasciifiedText.getValue() + " | ";
			outputTestBegin = outputTestBegin + deasciifiedText.getBegin() + " | ";
			outputTestEnd = outputTestEnd + deasciifiedText.getEnd() + " | ";
        }
		
		// JUnit Test for Lemma, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
}
