package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Stem;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;

/**
* ZemberekStemmer
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
public class ZemberekStemmer extends SegmenterBase {

	/**
	 * Analyze the text and create stems for every token. After successfully creation, add stems to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			// Create new morphology
			TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
		
			// Loop over every token and create a corresponding stem.
			for (Token token : select(aJCas, Token.class)) {
				List<WordAnalysis> results = morphology.analyze(token.getCoveredText());
				// If there are results, use the first element in the list as stem value.
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
