package org.hucompute.textimager.uima.zemberek;

import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly;
import de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SuggestedAction;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.normalization.TurkishSpellChecker;

import java.io.IOException;

import static org.apache.uima.fit.util.JCasUtil.select;

/**
* ZemberekSpellChecker
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide spell checking for turkish language.
* UIMA-Token is needed as input to create SpellingAnomaly.
* UIMA-Standard is used to represent the final SpellingAnomaly.
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.anomaly.type.SpellingAnomaly" }
		)
public class ZemberekSpellChecker extends SegmenterBase {

	/**
	 * Analyze the text and create suggestedAction for every token. After successfully creation, add spellingAnomaly to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			// Create new morphology
			TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
			TurkishSpellChecker spellChecker = new TurkishSpellChecker(morphology);

			for (Token token : select(aJCas, Token.class)) {
				// Is word incorrect?
				if(!spellChecker.check(token.getCoveredText())) {
					// Give suggestions!
					spellChecker.suggestForWord(token.getCoveredText());

					if(spellChecker.suggestForWord(token.getCoveredText()) != null) {
						// SpellingAnomaly
						SpellingAnomaly anomaly = new SpellingAnomaly(aJCas, token.getBegin(), token.getEnd());
						FSArray actions = new FSArray(aJCas, spellChecker.suggestForWord(token.getCoveredText()).size());

						// Create SuggestedAction
						int i = 0;
						// Uniform probability
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
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {

	}
}
