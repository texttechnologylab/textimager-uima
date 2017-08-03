package org.hucompute.textimager.uima.zemberek;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

/**
* ZemberekBeforeDisambiguation
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide disambiguation for turkish language. 
* UIMA-Sentence is needed as input to create analysis.
* UIMA-Standard is used to represent the final disambiguation.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma" }
		)
public class ZemberekBeforeDisambiguation extends SegmenterBase {

	/**
	 * Analyze the text and create disambiguation for every sentence. After successfully creation, add disambiguation to JCas.
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
	        
	        for(SentenceAnalysis.Entry entry : result) {
	        	System.out.println("Word = " + entry.input);
	        	for (WordAnalysis analysis : entry.parses) {
	                System.out.println(analysis.formatLong());
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
