package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;

/**
* ZemberekLemmatizer
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide lemmatization for turkish language. 
* UIMA-Token is needed as input to create lemma.
* UIMA-Standard is used to represent the final lemma.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma" }
		)
public class ZemberekLemmatizer extends SegmenterBase {
	
	/**
	 * Analyze the text and create lemmas for every token. After successfully creation, add lemmas to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			// Create a new morphology
			TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
		
			// Loop over every token and create a corresponding lemma.
			for (Token token : select(aJCas, Token.class)) {
				List<WordAnalysis> results = morphology.analyze(token.getCoveredText());
				// If there are results, use the first element in the list as lemma value.
				if(results.size() > 0) {
					Lemma lemma = new Lemma(aJCas, token.getBegin(), token.getEnd());	
					
					String lemmaValue = results.get(0).getLemma();
					
					if(lemmaValue.equals("UNK")) {
						// lemmaValue is unknown: Insert covered text as value.
						lemmaValue = token.getCoveredText();
					}
					
		        	lemma.setValue(lemmaValue);
		        	lemma.addToIndexes(aJCas);
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
