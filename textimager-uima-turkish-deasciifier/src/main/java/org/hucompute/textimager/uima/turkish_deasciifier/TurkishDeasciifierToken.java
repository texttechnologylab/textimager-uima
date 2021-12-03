package org.hucompute.textimager.uima.turkish_deasciifier;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import deasciifiedAnnotation.type.DeasciifiedAnnotation;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import static org.apache.uima.fit.util.JCasUtil.select;

/**
* TurkishDeasciifierToken
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.1
*
* This class provide deasciified text for turkish language.
* UIMA-Token is needed as input to create deasciifiedText.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"deasciifiedAnnotator.type.deasciifiedAnnotator" }
		)
public class TurkishDeasciifierToken extends SegmenterBase {

	/**
	 * Create a deasciified text for the inputText. After successfully creation, add deasciified text to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Create new Turkish-Deasciifier
		TurkishDeasciifier deasciifier = new TurkishDeasciifier();
		// Create DeasciifiedAnnotation
		// Loop over every Token and create deasciified text.
		for (Token token : select(aJCas, Token.class)) {
			deasciifier.setAsciiString(token.getCoveredText());
			// Create DeasciifiedAnnotation
			DeasciifiedAnnotation deasciifiedUIMAText = new DeasciifiedAnnotation(aJCas, token.getBegin(), token.getEnd());
			deasciifiedUIMAText.setValue(deasciifier.convertToTurkish());
			deasciifiedUIMAText.addToIndexes();
        }
	}

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {

	}
}
