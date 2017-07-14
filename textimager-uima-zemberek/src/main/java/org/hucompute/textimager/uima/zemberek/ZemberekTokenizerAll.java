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
* @date 29.05.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Tokenization.
*
*/
@TypeCapability(outputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" })
public class ZemberekTokenizerAll extends SegmenterBase {

	/**
	* Tokenizer, to subdivide text into token. All does not ignore whitespaces.
	*
	* @since 1.0
	*/
	private TurkishTokenizer tokenizer = TurkishTokenizer.ALL;
	
	/**
	 * Constructor
	 */
	public ZemberekTokenizerAll() {
		
	}
	

	/**
	 * Analyze the text for all token and output into UIMA.
	 * @param aJCas
	 * @param text Input to analyze for token.
	 * @param zoneBegin Not needed here.
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();
		// Create a new token iterator	
		Iterator<Token> tokenIterator = tokenizer.getTokenIterator(text); 
				
		// Loop over every token
		while (tokenIterator.hasNext()) {
			// Use Zemberek Token
			Token token = tokenIterator.next();
			
	        // Create Token, offset end by 1 to fit UIMA
	        Annotation tokenUIMA = createToken(aJCas, token.getStartIndex(), token.getStopIndex() + 1);	        
	    }
	}


	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}	
}
