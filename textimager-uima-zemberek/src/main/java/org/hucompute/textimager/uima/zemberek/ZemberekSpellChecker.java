package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SuggestedAction;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.normalization.TurkishSpellChecker;

/**
* ZemberekSpellChecker
*
* @date 23.06.2017
*
* @author Alexander Sang
* @version 1.1
*
* Turkish Spell Checker.
*
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly" }
		)
public class ZemberekSpellChecker extends SegmenterBase {
	
	/**
	 * Constructor
	 */
	public ZemberekSpellChecker() {
		
	}
	

	/**
	 * Create a SpellingAnomaly for every Token.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Use Zemberek morphology
		try {
			TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
			TurkishSpellChecker spellChecker = new TurkishSpellChecker(morphology);
			
			for (Token token : select(aJCas, Token.class)) {
				// Is word incorrect?
				if(!spellChecker.check(token.getCoveredText())) {
					// Give suggestions!
					spellChecker.suggestForWord(token.getCoveredText());
					
					// Spelling Anomaly
					SpellingAnomaly anomaly = new SpellingAnomaly(aJCas, token.getBegin(), token.getEnd());
					FSArray actions = new FSArray(aJCas, spellChecker.suggestForWord(token.getCoveredText()).size());
					
					// Create SuggestedAction
					int i = 0;
					float certainty = (float) (1.0 / (float) spellChecker.suggestForWord(token.getCoveredText()).size());
					for(String string : spellChecker.suggestForWord(token.getCoveredText())) {						
						SuggestedAction action = new SuggestedAction(aJCas, token.getBegin(), token.getEnd());
						action.setReplacement(string);
						action.setCertainty(certainty);
						actions.set(i, action);
						i = i + 1;						
					}
					
					// Set Anomaly
					anomaly.setSuggestions(actions);
					anomaly.addToIndexes(aJCas);
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
