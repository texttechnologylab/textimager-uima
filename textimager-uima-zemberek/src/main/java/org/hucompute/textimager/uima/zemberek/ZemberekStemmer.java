package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;

/**
* ZemberekStemmer
*
* @date 02.06.2017
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
public class ZemberekStemmer extends SegmenterBase {
	
	/**
	 * Constructor
	 */
	public ZemberekStemmer() {
		
	}
	

	/**
	 * Create a Stem for every Token.
	 * @param aJCas
	 * @param text Not needed here.
	 * @param zoneBegin Not needed here.
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Use Zemberek morphology
		TurkishMorphology morphology;
		
		try {
			morphology = TurkishMorphology.createWithDefaults();
		
			// Loop over every Token and create one Stem.
			for (Token token : select(aJCas, Token.class)) {
				List<WordAnalysis> results = morphology.analyze(token.getCoveredText());
				// Result: Create Stem with the first entry of the list.
				if(results.size() > 0) {
					Stem stem = new Stem(aJCas, token.getBegin(), token.getEnd());	
					stem.setValue(results.get(0).getStems().get(0));
					stem.addToIndexes(aJCas);
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
