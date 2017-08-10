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
import disambiguationAnnotation.type.DisambiguationAnnotation;

/**
* ZemberekDisambiguationTest
*
* @date 02.06.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Disambiguator Test. Test if the analysis is generated correctly.
*
*/
public class ZemberekDisambiguationTest {
	
	/**
	 * Test with JUnit if the analysis is generated correctly.
	 * @throws Exception
	 */
	@Test
	public void testDisambiguation() throws Exception {
		// Istanbul, hallo! Leider kann ich nur ganz wenig Türkisch.
		String text = "İstanbul, alo! Ne çok az Türk konuşabilir yazık.";
		
		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription tokenAnnotator = createEngineDescription(ZemberekTokenizerDefault.class);
		// Create a new Engine Description for the Disambiguation.
		AnalysisEngineDescription disambAnnotator = createEngineDescription(ZemberekDisambiguation.class);
				
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(text);
		
		// Pipeline
		SimplePipeline.runPipeline(inputCas, tokenAnnotator, disambAnnotator);
		
		// Sample Text
		String outputCorrectValue = "[(İstanbul:istanbul) (Noun,Prop;A3sg+Pnon+Nom)] | [(,:,) (Punc)] | [(alo:alo) (Interj)] | [(!:!) (Punc)] | [(ne:ne) (Adj)] | [(Ne:ne) (Noun,Prop;A3sg+Pnon+Nom)] | [(ne:ne) (Noun;A3sg+Pnon+Nom)] | [(ne:ne) (Conj)] | [(ne:ne) (Interj)] | [(ne:ne) (Pron,Ques)] | [(ne:ne) (Adv)] | [(çok:çok) (Adv)] | [(çok:çok) (Det)] | [(çok:çok) (Postp,PCAbl)] | [(çok:çok) (Adj)] | [(az:az) (Adj)] | [(azmak:az) (Verb;Pos+Imp+A2sg)] | [(Az:az) (Noun,Prop;A3sg+Pnon+Nom)] | [(az:az) (Postp,PCAbl)] | [(az:az) (Adv)] | [(Türk:türk) (Noun,Prop;A3sg+Pnon+Nom)] | [(konuşmak:konuş) (Verb;Pos)(Verb;Abil:abil+Aor:ir+A3sg)] | [(konuşmak:konuş) (Verb;Pos)(Verb;Abil:abil)(Adj;AorPart:ir)] | [(konuşmak:konuş) (Verb;Pos)(Verb;Abil:abil+AorPart:ir)] | [(yazık:yazık) (Noun;A3sg+Pnon+Nom)] | [(yazık:yazık) (Adv)] | [(yazık:yazık) (Interj)] | [(.:.) (Punc)] | ";
		String outputCorrectBegin = "0 | 8 | 10 | 13 | 15 | 15 | 15 | 15 | 15 | 15 | 15 | 18 | 18 | 18 | 18 | 22 | 22 | 22 | 22 | 22 | 25 | 30 | 30 | 30 | 42 | 42 | 42 | 47 | ";
		String outputCorrectEnd = "8 | 9 | 13 | 14 | 17 | 17 | 17 | 17 | 17 | 17 | 17 | 21 | 21 | 21 | 21 | 24 | 24 | 24 | 24 | 24 | 29 | 41 | 41 | 41 | 47 | 47 | 47 | 48 | ";
		
		// Generate Text with library
		String outputTestValue = "";
		String outputTestBegin = "";
		String outputTestEnd = "";
		
		// Loop over different lemma and create the test text.
		for (DisambiguationAnnotation disambiguation : select(inputCas, DisambiguationAnnotation.class)) {			
			outputTestValue = outputTestValue + disambiguation.getValue() + " | ";
			outputTestBegin = outputTestBegin + disambiguation.getBegin() + " | ";
			outputTestEnd = outputTestEnd + disambiguation.getEnd() + " | ";
        }
		
		// JUnit Test for Value, Begin, End
		assertEquals(outputCorrectValue, outputTestValue);
		assertEquals(outputCorrectBegin, outputTestBegin);
		assertEquals(outputCorrectEnd, outputTestEnd);
	}
	
}
