package org.hucompute.textimager.uima.zemberek;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import zemberek.tokenization.TurkishSentenceExtractor;

/**
* ZemberekSentenceBoundary
*
* @date 17.06.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Sentence-Boundary Detection.
*
*/
@TypeCapability(outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" })
public class ZemberekSentenceBoundary extends SegmenterBase {

	/**
	 * Constructor
	 */
	public ZemberekSentenceBoundary() {
		
	}	

	/**
	 * Analyze the text for all sentences and output into UIMA.
	 * @param aJCas
	 * @param text Input to analyze for token.
	 * @param zoneBegin Not needed here.
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();
		// Create a new sentence extractor
		TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
        // List of different sentences
		List<String> sentences = extractor.fromParagraph(text);
			
		int start = 0;
		int end = 0;
		
		// Loop over every sentence
		for (String sentence : sentences) {
			// Create end-Tag
			end = start + sentence.length();
			// Create sentence
            Sentence s = new Sentence(aJCas, start, end);
            // Create next start-Tag
            start = end + 1;
            // Add to JCas-Index
            s.addToIndexes(aJCas);
        }
	}

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
