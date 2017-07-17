package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.normalization.TurkishSpellChecker;

/**
* ZemberekLemmatizer
*
* @date 23.06.2017
*
* @author Alexander Sang
* @version 1.1
*
* Turkish Lemmatization.
*
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma" }
		)
public class ZemberekSpellChecker extends SegmenterBase {
	
	/**
	 * Constructor
	 */
	public ZemberekSpellChecker() {
		
	}
	

	/**
	 * Create a Lemma for every Token.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();
		// Use Zemberek morphology
		try {
			TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
			TurkishSpellChecker spellChecker = new TurkishSpellChecker(morphology);
			
			for (Token token : select(aJCas, Token.class)) {
				// Is word correct?
				spellChecker.check(token.getCoveredText());
				
				// Give suggestions!
				spellChecker.suggestForWord(token.getCoveredText());
				// spellChecker.rankWithUnigramProbability(arg0, arg1)
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
