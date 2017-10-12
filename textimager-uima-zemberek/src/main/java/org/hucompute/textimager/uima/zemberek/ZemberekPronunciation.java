package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import pronunciationAnnotation.type.PronunciationAnnotation;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

/**
* ZemberekPronunciation
*
* @date 10.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide pronunciation for turkish language. 
* UIMA-Token is needed as input to create analysis.
* UIMA-Standard is used to represent the final disambiguation.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"},
		outputs = {"pronunciationAnnotation.type.PronunciationAnnotation"})
public class ZemberekPronunciation extends SegmenterBase {

	/**
	 * Analyze the text and create pronunciation for every word. After successfully creation, add pronunciation to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String inputText = aJCas.getDocumentText();
		try {
			// Create new morphology
			TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
	        Z3MarkovModelDisambiguator disambiguator = new Z3MarkovModelDisambiguator();		
	        TurkishSentenceAnalyzer sentenceAnalyzer = new TurkishSentenceAnalyzer(morphology, disambiguator);
	        
	        SentenceAnalysis result = sentenceAnalyzer.analyze(inputText);
	        sentenceAnalyzer.disambiguate(result);
	        
		    // Create an ArrayList of all token, because POS-library doesn't output begin/end of POS. Calculate it manually.
			ArrayList<Token> T = new ArrayList<Token>();
			for (Token token : select(aJCas, Token.class)) {
				T.add(token);
			}
			
			int i = 0;
	        
	        for(SentenceAnalysis.Entry entry : result) {
	        	for (WordAnalysis analysis : entry.parses) {
	                // Create PronunciationAnnotation		
	        		PronunciationAnnotation pronunciation = new PronunciationAnnotation(aJCas, T.get(i).getBegin(), T.get(i).getEnd());
					pronunciation.setValue(analysis.getPronunciation());
					pronunciation.addToIndexes();	
					break;
	            }
	        	
	        	i++;
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
