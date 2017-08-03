package org.hucompute.textimager.uima.turkish_deasciifier;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.assertEquals;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.turkish_deasciifier.TurkishDeasciifier;
import org.junit.Test;

import deasciifiedAnnotation.type.DeasciifiedAnnotation;

/**
* TurkishDeasciifierTest
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.1
*
* This class provide several test cases for turkish language. 
*/
public class TurkishDeasciifierTest {
	
	/**
	 * Test with JUnit if the text is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testDeasciifier() throws Exception {
		// Use Deasciifier
		TurkishDeasciifier deasciifier = new TurkishDeasciifier();
		deasciifier.setAsciiString("Hadi bir masal uyduralim, icinde mutlu, doygun, telassiz durdugumuz.");
		// Test with JUnit
		assertEquals(deasciifier.convertToTurkish(), "Hadi bir masal uyduralım, içinde mutlu, doygun, telaşsız durduğumuz.");
	}
	
	/**
	 * Test with JUnit if the text is generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testDeasciifierInPipeline() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "Instanbul, alo! Ne cok az Türk konusabilir yazik.";
		
		// Create new AnalysisEngineDescription
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
		
		// Loop over text and create the UIMA-Output.
		for (DeasciifiedAnnotation deasciifiedText : select(inputCas, DeasciifiedAnnotation.class)) {			
			outputTestToken = outputTestToken + deasciifiedText.getValue() + " | ";
			outputTestBegin = outputTestBegin + deasciifiedText.getBegin() + " | ";
			outputTestEnd = outputTestEnd + deasciifiedText.getEnd() + " | ";
        }
		
		// JUnit Test for Text, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}	
}
