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
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide sentence detection for turkish language. 
* UIMA-Standard is used to represent the final sentence.
*/
@TypeCapability(outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" })
public class ZemberekSentenceBoundary extends SegmenterBase {

	/**
	 * Analyze the text and create sentences. After successfully creation, add sentences to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String inputText = aJCas.getDocumentText();
		// Create new sentence extractor
		TurkishSentenceExtractor extractor = TurkishSentenceExtractor.DEFAULT;
        // List of sentences
		List<String> sentences = extractor.fromParagraph(inputText);
			
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
