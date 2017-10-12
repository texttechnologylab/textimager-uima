package org.hucompute.textimager.uima.IXA;

import java.util.List;
import java.util.Properties;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import eus.ixa.ixa.pipe.seg.RuleBasedSegmenter;
import eus.ixa.ixa.pipe.tok.RuleBasedTokenizer;
import eus.ixa.ixa.pipe.tok.Token;

public class IXATokenizer extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Get JCas text and language
		String language = aJCas.getDocumentLanguage();
		String originalText = aJCas.getDocumentText();
		
		// Set Properties for pipe
		Properties properties = new Properties();
		properties.setProperty("hardParagraph", "no");
		properties.setProperty("untokenizable", "no");
		properties.setProperty("language", language);
		
		// Segment Text to Sentences
		RuleBasedSegmenter segmenter = new RuleBasedSegmenter(originalText, properties );
		String [] sentences = segmenter.segmentSentence();
		
		// Tokenize Text
		RuleBasedTokenizer tokenizer = new RuleBasedTokenizer(originalText, properties);
		List<List<Token>> TokenList = tokenizer.tokenize(sentences);
		
		// Annotate Sentence and Token
		for(List<Token> sentence: TokenList) {
			if(sentence.size() > 0) {
				int sentenceBegin = sentence.get(0).startOffset();
				int sentenceEnd = sentence.get(sentence.size()-1).startOffset() + sentence.get(sentence.size()-1).tokenLength();
				Sentence casSentence = new Sentence(aJCas, sentenceBegin, sentenceEnd);
				casSentence.addToIndexes();
				
				for(Token token: sentence) {
					int tokenBegin = token.startOffset();
					int tokenEnd = tokenBegin + token.tokenLength();
					de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token casToken = 
							new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(aJCas,tokenBegin,tokenEnd);
					casToken.addToIndexes();
				}
			}
				
		}
		
	}

}
