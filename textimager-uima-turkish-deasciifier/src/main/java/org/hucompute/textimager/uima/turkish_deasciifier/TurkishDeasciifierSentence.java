package org.hucompute.textimager.uima.turkish_deasciifier;

import static org.apache.uima.fit.util.JCasUtil.select;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import deasciifiedAnnotation.type.DeasciifiedAnnotation;

/**
* TurkishDeasciifierSentence
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.1
*
* This class provide deasciified text for turkish language. 
* UIMA-Sentence is needed as input to create deasciifiedText.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" },
		outputs = {"deasciifiedAnnotator.type.deasciifiedAnnotator" }
		)
public class TurkishDeasciifierSentence extends SegmenterBase {

	/**
	 * Create a deasciified text for the inputText. After successfully creation, add deasciified text to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Create new Turkish-Deasciifier
		TurkishDeasciifier deasciifier = new TurkishDeasciifier();
		// Create DeasciifiedAnnotation		
		// Loop over every sentence and create deasciified text.
		for (Sentence sentence : select(aJCas, Sentence.class)) {				
			deasciifier.setAsciiString(sentence.getCoveredText());
			// Create DeasciifiedAnnotation		
			DeasciifiedAnnotation deasciifiedUIMAText = new DeasciifiedAnnotation(aJCas, sentence.getBegin(), sentence.getEnd());
			deasciifiedUIMAText.setValue(deasciifier.convertToTurkish());
			deasciifiedUIMAText.addToIndexes();
        }	
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}
}
