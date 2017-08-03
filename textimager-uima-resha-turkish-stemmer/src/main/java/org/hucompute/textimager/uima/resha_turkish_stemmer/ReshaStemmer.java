package org.hucompute.textimager.uima.resha_turkish_stemmer;

import static org.apache.uima.fit.util.JCasUtil.select;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import com.hrzafer.reshaturkishstemmer.Resha;
import com.hrzafer.reshaturkishstemmer.Stemmer;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
* ReshaStemmer
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide stemming for turkish language. 
* UIMA-Token is needed as input to create stem.
* UIMA-Standard is used to represent the final stem.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem" }
		)
public class ReshaStemmer extends SegmenterBase {

	/**
	 * Analyze the text and create stems for every token. After successfully creation, add stems to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Use Resha-Turkish-Stemmer
		Stemmer stemmer = Resha.Instance;

		// Loop over every token and create a corresponding stem.
		for (Token token : select(aJCas, Token.class)) {
			String stem = stemmer.stem(token.getCoveredText());
			// Create stem
			Stem stemUIMA = new Stem(aJCas, token.getBegin(), token.getEnd());	
			stemUIMA.setValue(stem);
			stemUIMA.addToIndexes(aJCas);
		}
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
