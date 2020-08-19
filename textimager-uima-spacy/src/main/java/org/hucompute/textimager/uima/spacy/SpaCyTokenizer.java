package org.hucompute.textimager.uima.spacy;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import jep.JepException;

public class SpaCyTokenizer extends SpaCyBase {

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		/*try {
			interp.set("lang", aJCas.getDocumentLanguage());
			interp.set("text", aJCas.getDocumentText());
			if(aJCas.getDocumentLanguage().equals("de"))
				interp.exec("nlp = spacy.load('de_core_news_sm')");
			else
				interp.exec("nlp = spacy.load('en_core_web_sm')");
			interp.exec("doc = nlp.tokenizer(text)");
			interp.exec("tokens = [{'idx': token.idx,'length': len(token),'is_space': token.is_space} for token in doc]");
			interp.exec("System.out.println(tokens)");
			ArrayList<HashMap<String, Object>> output = (ArrayList<HashMap<String, Object>>) interp.getValue("tokens");
			for (HashMap<String, Object> token : output) {
				if (!(Boolean)token.get("is_space")) {
					int begin = ((Long)token.get("idx")).intValue();
					int end = begin + ((Long)token.get("length")).intValue();
					Token casToken = new Token(aJCas, begin, end);
					casToken.addToIndexes();
				}
			}
			
		} catch (JepException e) {
			e.printStackTrace();
		}
		*/
	}
}
