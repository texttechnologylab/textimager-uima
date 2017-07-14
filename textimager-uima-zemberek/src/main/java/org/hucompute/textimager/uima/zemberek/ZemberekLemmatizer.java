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
public class ZemberekLemmatizer extends SegmenterBase {
	
	/**
	 * Constructor
	 */
	public ZemberekLemmatizer() {
		
	}
	

	/**
	 * Create a Lemma for every Token.
	 * @param aJCas
	 * @param text Not needed here.
	 * @param zoneBegin Not needed here.
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();
		// Use Zemberek morphology
		TurkishMorphology morphology;
		
		try {
			morphology = TurkishMorphology.createWithDefaults();
		
			// Loop over every Token and create one Lemma.
			for (Token token : select(aJCas, Token.class)) {
				List<WordAnalysis> results = morphology.analyze(token.getCoveredText());
				// Result: Create Lemma.
				if(results.size() > 0) {
					Lemma lemma = new Lemma(aJCas, token.getBegin(), token.getEnd());	
					
					String lemmaValue = results.get(0).getLemma();
					
					if(lemmaValue.equals("UNK")) {
						// Insert text as lemma if unknown.
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
