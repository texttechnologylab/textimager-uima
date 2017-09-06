package org.hucompute.textimager.uima.IXA;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eus.ixa.ixa.pipe.lemma.StatisticalLemmatizer;
import eus.ixa.ixa.pipe.pos.Morpheme;
import eus.ixa.ixa.pipe.pos.StatisticalTagger;

public class IXAPOS extends JCasAnnotator_ImplBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Get JCas text and language
		String language = aJCas.getDocumentLanguage();
		String originalText = aJCas.getDocumentText();

		// Set Properties for pipe
		Properties properties = new Properties();
		properties.setProperty("language", language);
		properties.setProperty("model", 
				"src/main/resources/org/hucompute/textimager/uima/IXA/morph-models-1.5.0/"+language+"/"+language+"-pos-perceptron.bin");
		properties.setProperty("lemmatizerModel", 
				"src/main/resources/org/hucompute/textimager/uima/IXA/morph-models-1.5.0/"+language+"/"+language+"-lemma-perceptron.bin");
		
		StatisticalTagger tagger = new StatisticalTagger(properties);
		
		for (Sentence jCasSentence : JCasUtil.select(aJCas, Sentence.class)) {
			List<Token> jCasTokens = selectCovered(aJCas, Token.class, jCasSentence);
			List<String> tokenList = new ArrayList<String>();
			for(Token jCasToken :jCasTokens) {
				tokenList.add(jCasToken.getCoveredText());				
			}
			List<String> posTags = tagger.posAnnotate(tokenList.toArray(new String[] {}));
			//List<Morpheme> morphTags = tagger.getMorphemesFromStrings(posTags, tokenList.toArray(new String[] {}));
			
			StatisticalLemmatizer lemmatizer= new StatisticalLemmatizer(properties);
			List<String>  lemmaTags = lemmatizer.lemmatize(tokenList.toArray(new String[] {}), posTags.toArray(new String[] {}));
			
			int i = 0;
			for(Token jCasToken :jCasTokens) {
				int begin = jCasToken.getBegin();
				int end = jCasToken.getEnd();
				
				POS pos = new POS(aJCas, begin, end);
				pos.setPosValue(posTags.get(i));
				pos.addToIndexes();
				
				Lemma lemma = new Lemma(aJCas, begin, end);
				lemma.setValue(lemmaTags.get(i));
				lemma.addToIndexes();

				i++;
							
			}
		}
		
		
	}

}
