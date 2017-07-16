package org.hucompute.textimager.uima.resha_turkish_stemmer;

import static org.apache.uima.fit.util.JCasUtil.select;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import com.hrzafer.reshaturkishstemmer.Resha;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
* ReshaStemmer
*
* @date 10.07.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Stemmer.
*
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem" }
		)
public class ReshaStemmer extends SegmenterBase {
	
	/**
	 * Constructor
	 */
	public ReshaStemmer() {
		
	}
	

	/**
	 * Create a Stem for every Token.
	 * @param aJCas
	 * @param text Not needed here.
	 * @param zoneBegin Not needed here.
	 */
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		// Use Resha-Turkish-Stemmer
		com.hrzafer.reshaturkishstemmer.Stemmer stemmer = Resha.Instance;

		// Loop over every Token and create one Stem.
		for (Token token : select(aJCas, Token.class)) {
			String stem = stemmer.stem(token.getCoveredText());
			// Create Stem
			Stem stemUIMA = new Stem(aJCas, token.getBegin(), token.getEnd());	
			stemUIMA.setValue(stem);
			stemUIMA.addToIndexes(aJCas);
		}
	}	
}
