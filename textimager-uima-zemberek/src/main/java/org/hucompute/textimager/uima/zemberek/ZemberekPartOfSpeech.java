package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

/**
* ZemberekPartOfSpeech
*
* @date 13.06.2017
*
* @author Alexander Sang
* @version 1.0
*
* Turkish Part-Of-Speech.
*
*/
@TypeCapability(
		inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS" })
public class ZemberekPartOfSpeech extends SegmenterBase {
	
	/**
	 * Constructor
	 */
	public ZemberekPartOfSpeech() {
		
	}
	

	/**
	 * Analyze the text for all token and output into UIMA as POS.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String text = aJCas.getDocumentText();
		// Objects to create POS-Tags
		TurkishMorphology morphology;
        Z3MarkovModelDisambiguator disambiguator;
        TurkishSentenceAnalyzer sentenceAnalyzer;
        
        try {
        	// Initialize
			morphology = TurkishMorphology.createWithDefaults();
			disambiguator = new Z3MarkovModelDisambiguator();
			sentenceAnalyzer = new TurkishSentenceAnalyzer(
	                morphology,
	                disambiguator
	        );
			
			// Analyze the sentence or text.
			SentenceAnalysis analysis = sentenceAnalyzer.analyze(text);
			sentenceAnalyzer.disambiguate(analysis);
			
			// Current Token
			int i = 0;
			
			// Create an ArrayList of all Token, because POS-library doesn't output begin/end of POS. Calculate it manually.
			ArrayList<Token> T = new ArrayList<Token>();
			for (Token token : select(aJCas, Token.class)) {
				T.add(token);
			}
			
			// Analyze Sentence
	        for (SentenceAnalysis.Entry entry : analysis) {	            
	        	// Analyze current Token
	        	WordAnalysis wa = entry.parses.get(0);
	        	
	        	// Create POS-Tag, only if we have a Token.
	        	if(T.size() > i) {
	        		POS posElement = new POS(aJCas, T.get(i).getBegin(), T.get(i).getEnd());	
	        		
	        		// Filter for secondaryPos
	        		if(wa.dictionaryItem.secondaryPos.toString().equals("None")) {
	        			posElement.setPosValue(wa.dictionaryItem.primaryPos + "");
	        		} else {
	        			posElement.setPosValue(wa.dictionaryItem.primaryPos + ":" + wa.dictionaryItem.secondaryPos);
	        		}
	        		
		    		posElement.addToIndexes(aJCas);
	        	}            
	    		
	    		// Next Token
	        	i = i + 1;
	        }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
	}
}
