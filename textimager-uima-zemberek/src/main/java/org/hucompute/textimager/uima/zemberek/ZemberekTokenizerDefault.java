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
* ZemberekTokenizer
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
public class ZemberekTokenizerDefault extends SegmenterBase {

	/**
	* Tokenizer, to subdivide text into token.
	*
	* @since 1.0
	*/
	private TurkishTokenizer tokenizer = TurkishTokenizer.DEFAULT;
	
	/**
	 * Constructor
	 */
	public ZemberekTokenizerDefault() {
		
	}
	

	/**
	 * Analyze the text for all token and output into UIMA.
	 * @param aJCas
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
