package org.hucompute.textimager.uima.polyglot;

import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

/**
* PolyglotNamedEntityTest
*
* @date 08.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages. 
*/
public class PolyglotNamedEntityTest {
	
	/**
	 * Test with JUnit if the POS are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testNamedEntityGerman() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		AnalysisEngineDescription nerAnnotator = createEngineDescription(PolyglotNamedEntity.class, PolyglotNamedEntity.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/polyglot/lib/ner-default.map");
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Deutschland ist ein Bundesstaat in Mitteleuropa. Er besteht aus 16 Ländern und ist als freiheitlich-demokratischer und sozialer Rechtsstaat verfasst. Die Bundesrepublik Deutschland stellt die jüngste Ausprägung des deutschen Nationalstaates dar.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, nerAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Deutschland | Mitteleuropa | Bundesrepublik | Deutschland | ";
		String outputCorrectValue = "I-LOC | I-LOC | I-LOC | I-LOC | ";
		String outputCorrectBegin = "0 | 35 | 154 | 169 | ";
		String outputCorrectEnd = "11 | 47 | 168 | 180 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different NE-Tags and create the UIMA-Output.
		for (NamedEntity ne : select(inputCas, NamedEntity.class)) {		
			outputTestToken = outputTestToken + ne.getCoveredText() + " | ";
			outputTestValue = outputTestValue + ne.getValue() + " | ";
			outputTestBegin = outputTestBegin + ne.getBegin() + " | ";
			outputTestEnd = outputTestEnd + ne.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the POS are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testNamedEntityEnglish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		AnalysisEngineDescription nerAnnotator = createEngineDescription(PolyglotNamedEntity.class, PolyglotNamedEntity.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/polyglot/lib/ner-default.map");
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("The Israeli Prime Minister Benjamin Netanyahu has warned that Iran poses a threat to the entire world.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, nerAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Israeli | Benjamin | Netanyahu | Iran | ";
		String outputCorrectValue = "I-ORG | I-PER | I-PER | I-LOC | ";
		String outputCorrectBegin = "4 | 27 | 36 | 62 | ";
		String outputCorrectEnd = "11 | 35 | 45 | 66 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different NE-Tags and create the UIMA-Output.
		for (NamedEntity ne : select(inputCas, NamedEntity.class)) {		
			outputTestToken = outputTestToken + ne.getCoveredText() + " | ";
			outputTestValue = outputTestValue + ne.getValue() + " | ";
			outputTestBegin = outputTestBegin + ne.getBegin() + " | ";
			outputTestEnd = outputTestEnd + ne.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
	/**
	 * Test with JUnit if the POS are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testNamedEntityTurkish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class);
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class);
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class);
		AnalysisEngineDescription nerAnnotator = createEngineDescription(PolyglotNamedEntity.class, PolyglotNamedEntity.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/polyglot/lib/ner-default.map");
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("İstanbul, alo! Ne çok az Türk konuşabilir yazık.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, nerAnnotator);
		
		// Sample Text
		String outputCorrectToken = "İstanbul | ";
		String outputCorrectValue = "I-LOC | ";
		String outputCorrectBegin = "0 | ";
		String outputCorrectEnd = "8 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different NE-Tags and create the UIMA-Output.
		for (NamedEntity ne : select(inputCas, NamedEntity.class)) {		
			outputTestToken = outputTestToken + ne.getCoveredText() + " | ";
			outputTestValue = outputTestValue + ne.getValue() + " | ";
			outputTestBegin = outputTestBegin + ne.getBegin() + " | ";
			outputTestEnd = outputTestEnd + ne.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
