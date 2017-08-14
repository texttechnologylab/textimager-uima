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

/**
* PolyglotPartOfSpeechTest
*
* @date 08.08.2017
*
* @author Alexander Sang
* @version 1.0
*
* This class provide several test cases for different languages. 
*/
public class PolyglotPartOfSpeechTest {
	
	/**
	 * Test with JUnit if the POS are generated correctly and if the pipeline is working.
	 * @throws Exception
	 */
	@Test
	public void testPartOfSpeechGerman() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription posAnnotator = createEngineDescription(PolyglotPartOfSpeech.class, PolyglotPartOfSpeech.PARAM_PYTHON_PATH, "/usr/bin/python", PolyglotPartOfSpeech.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/polyglot/lib/pos-default.map");
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Deutschland ist ein Bundesstaat in Mitteleuropa. Er besteht aus 16 Ländern und ist als freiheitlich-demokratischer und sozialer Rechtsstaat verfasst. Die Bundesrepublik Deutschland stellt die jüngste Ausprägung des deutschen Nationalstaates dar.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, posAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Deutschland | ist | ein | Bundesstaat | in | Mitteleuropa | . | Er | besteht | aus | 16 | Ländern | und | ist | als | freiheitlich | - | demokratischer | und | sozialer | Rechtsstaat | verfasst | . | Die | Bundesrepublik | Deutschland | stellt | die | jüngste | Ausprägung | des | deutschen | Nationalstaates | dar | . | ";
		String outputCorrectValue = "PROPN | VERB | DET | NOUN | ADP | NOUN | PUNCT | PRON | VERB | ADP | NUM | NOUN | CONJ | VERB | ADP | ADJ | PUNCT | ADJ | CONJ | ADJ | NOUN | VERB | PUNCT | DET | PROPN | PROPN | VERB | DET | ADJ | NOUN | DET | ADJ | NOUN | ADV | PUNCT | ";
		String outputCorrectBegin = "0 | 12 | 16 | 20 | 32 | 35 | 47 | 49 | 52 | 60 | 64 | 67 | 75 | 79 | 83 | 87 | 99 | 100 | 115 | 119 | 128 | 140 | 148 | 150 | 154 | 169 | 181 | 188 | 192 | 200 | 211 | 215 | 225 | 241 | 244 | ";
		String outputCorrectEnd = "11 | 15 | 19 | 31 | 34 | 47 | 48 | 51 | 59 | 63 | 66 | 74 | 78 | 82 | 86 | 99 | 100 | 114 | 118 | 127 | 139 | 148 | 149 | 153 | 168 | 180 | 187 | 191 | 199 | 210 | 214 | 224 | 240 | 244 | 245 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different POS-Tags and create the UIMA-Output.
		for (POS pos : select(inputCas, POS.class)) {		
			outputTestToken = outputTestToken + pos.getCoveredText() + " | ";
			outputTestValue = outputTestValue + pos.getPosValue() + " | ";
			outputTestBegin = outputTestBegin + pos.getBegin() + " | ";
			outputTestEnd = outputTestEnd + pos.getEnd() + " | ";
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
	public void testPartOfSpeechEnglish() throws Exception {
		// Create a new Engine Description.
		AnalysisEngineDescription languageAnnotator = createEngineDescription(PolyglotLanguage.class, PolyglotLanguage.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription sentenceAnnotator = createEngineDescription(PolyglotSentenceBoundary.class, PolyglotSentenceBoundary.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(PolyglotTokenizer.class, PolyglotTokenizer.PARAM_PYTHON_PATH, "/usr/bin/python");
		AnalysisEngineDescription posAnnotator = createEngineDescription(PolyglotPartOfSpeech.class, PolyglotPartOfSpeech.PARAM_PYTHON_PATH, "/usr/bin/python", PolyglotPartOfSpeech.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/polyglot/lib/pos-default.map");
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText("Germany, officially the Federal Republic of Germany, is a federal parliamentary republic in central-western Europe. It includes 16 constituent states, covers an area of 357,021 square kilometres, and has a largely temperate seasonal climate.");
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, languageAnnotator, sentenceAnnotator, tokenAnnotator, posAnnotator);
		
		// Sample Text
		String outputCorrectToken = "Germany | , | officially | the | Federal | Republic | of | Germany | , | is | a | federal | parliamentary | republic | in | central | - | western | Europe | . | It | includes | 16 | constituent | states | , | covers | an | area | of | 357,021 | square | kilometres | , | and | has | a | largely | temperate | seasonal | climate | . | ";
		String outputCorrectValue = "PROPN | PUNCT | ADV | DET | PROPN | PROPN | ADP | PROPN | PUNCT | VERB | DET | ADJ | PROPN | NOUN | ADP | ADJ | PUNCT | ADJ | PROPN | PUNCT | PRON | VERB | NUM | NOUN | NOUN | PUNCT | VERB | DET | NOUN | ADP | NUM | NOUN | NOUN | PUNCT | CONJ | VERB | DET | ADV | ADJ | ADJ | NOUN | PUNCT | ";
		String outputCorrectBegin = "0 | 7 | 9 | 20 | 24 | 32 | 41 | 44 | 51 | 53 | 56 | 58 | 66 | 80 | 89 | 92 | 99 | 100 | 108 | 114 | 116 | 119 | 128 | 131 | 143 | 149 | 151 | 158 | 161 | 166 | 169 | 177 | 184 | 194 | 196 | 200 | 204 | 206 | 214 | 224 | 233 | 240 | ";
		String outputCorrectEnd = "7 | 8 | 19 | 23 | 31 | 40 | 43 | 51 | 52 | 55 | 57 | 65 | 79 | 88 | 91 | 99 | 100 | 107 | 114 | 115 | 118 | 127 | 130 | 142 | 149 | 150 | 157 | 160 | 165 | 168 | 176 | 183 | 194 | 195 | 199 | 203 | 205 | 213 | 223 | 232 | 240 | 241 | ";
		
		// Generated text with library
		String outputTestToken = "";
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different POS-Tags and create the UIMA-Output.
		for (POS pos : select(inputCas, POS.class)) {		
			outputTestToken = outputTestToken + pos.getCoveredText() + " | ";
			outputTestValue = outputTestValue + pos.getPosValue() + " | ";
			outputTestBegin = outputTestBegin + pos.getBegin() + " | ";
			outputTestEnd = outputTestEnd + pos.getEnd() + " | ";
        }
		
		// JUnit-Test: CoveredText, Value, Begin, End
		assertEquals(outputCorrectToken, outputTestToken);
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
}
