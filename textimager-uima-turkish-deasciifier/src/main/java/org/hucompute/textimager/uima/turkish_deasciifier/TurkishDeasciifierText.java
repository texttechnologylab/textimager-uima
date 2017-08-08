package org.hucompute.textimager.uima.turkish_deasciifier;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import deasciifiedAnnotation.type.DeasciifiedAnnotation;

/**
* TurkishDeasciifierText
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.1
*
* This class provide deasciified text for turkish language. 
*/
@TypeCapability(
		outputs = {"deasciifiedAnnotator.type.deasciifiedAnnotator" }
		)
public class TurkishDeasciifierText extends SegmenterBase {
	
	/**
	 * Create a deasciified text for the inputText. After successfully creation, add deasciified text to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String inputText = aJCas.getDocumentText();
		// Create new Turkish-Deasciifier
		TurkishDeasciifier deasciifier = new TurkishDeasciifier();
		if(deasciifier != null) {
			deasciifier.setAsciiString(inputText);
			// Create DeasciifiedAnnotation		
			DeasciifiedAnnotation deasciifiedUIMAText = new DeasciifiedAnnotation(aJCas, 0, inputText.length());
			deasciifiedUIMAText.setValue(deasciifier.convertToTurkish());
			deasciifiedUIMAText.addToIndexes();
		}		
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
