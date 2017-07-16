package org.hucompute.textimager.uima.turkish_deasciifier;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import deasciifiedAnnotation.type.DeasciifiedAnnotation;

/**
* DeasciifierUIMA
*
* @date 12.07.2017
*
* @author Alexander Sang
* @version 1.1
*
* Turkish Deasciifier.
*
*/
@TypeCapability(
		outputs = {"deasciifiedAnnotator.type.deasciifiedAnnotator" }
		)
public class TurkishDeasciifierText extends SegmenterBase {
	
	/**
	 * Constructor
	 */
	public TurkishDeasciifierText() {
		
	}
	

	/**
	 * Create a deasciified Text for the text.
	 * @param aJCas
	 * @param text Not needed here.
	 * @param zoneBegin Not needed here.
	 */
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		// Create new Turkish-Deasciifier
		TurkishDeasciifier deasciifier = new TurkishDeasciifier();
		deasciifier.setAsciiString(text);
		// Create DeasciifiedAnnotation		
		DeasciifiedAnnotation deasciifiedUIMAText = new DeasciifiedAnnotation(aJCas, 0, text.length());
		deasciifiedUIMAText.setValue(deasciifier.convertToTurkish());
		deasciifiedUIMAText.addToIndexes();
	}	
}
