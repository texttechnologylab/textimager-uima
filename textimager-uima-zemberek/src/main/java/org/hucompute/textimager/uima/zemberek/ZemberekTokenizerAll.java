package org.hucompute.textimager.uima.zemberek;

import java.util.Iterator;

import org.antlr.v4.runtime.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import zemberek.tokenization.TurkishTokenizer;

/**
* ZemberekTokenizerAll
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide special tokenization for turkish language. 
* UIMA-Standard is used to represent the final token.
*/
@TypeCapability(outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" })
public class ZemberekTokenizerAll extends SegmenterBase {

	/**
	* Tokenizer to subdivide text into token.
	* @since 1.0
	*/
	private TurkishTokenizer tokenizer = TurkishTokenizer.ALL;
	
	/**
	 * Analyze the text and create tokens for every word. After successfully creation, add tokens to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String inputText = aJCas.getDocumentText();
		// Create a new token iterator
		Iterator<Token> tokenIterator = tokenizer.getTokenIterator(inputText); 
				
		// Loop over every token
		while (tokenIterator.hasNext()) {
			// Zemberek-Token
			Token token = tokenIterator.next();
			
	        // Create UIMA-Token: Offset stopIndex by 1 to fit UIMA-Standard.
	        Annotation tokenUIMA = createToken(aJCas, token.getStartIndex(), token.getStopIndex() + 1);	        
	        if(tokenUIMA != null) tokenUIMA.addToIndexes(aJCas);
		}
	}

	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
