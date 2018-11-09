package org.hucompute.textimager.uima.cltk;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class CLTKSegmenter extends CLTKBase {
	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		return new JSONObject()
			.put("lang", aJCas.getDocumentLanguage())
			.put("text", aJCas.getDocumentText());
	}
	
	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		String docText = aJCas.getDocumentText();
		
		int last_search_pos = 0;
		JSONArray sentences = jsonResult.getJSONArray("sents");
		for (Object s : sentences) {
			String sentence = (String)s;
			
			int pos = docText.indexOf(sentence, last_search_pos);
			if (pos != -1) {				
				int begin = pos;
				int end = pos + sentence.length();
				
				Sentence casSentence = new Sentence(aJCas, begin, end);
				casSentence.addToIndexes();
				
				last_search_pos = pos + 1;
			}
		}
		
		JSONArray tokens = jsonResult.getJSONArray("token");
		
		last_search_pos = 0;
		int last_end = 0;
		
		for (Object t : tokens) {
			String token = (String) t;

			int begin = -1;
			int end = -1;
			
			// TODO deckt das alle Fälle ab?
			if (token.length() > 1 && token.startsWith("-")) {
				begin = last_end;
				end = begin;
				while (end < docText.length() && (Character.isAlphabetic(docText.charAt(end)) || Character.isDigit(docText.charAt(end)))) {
					end++;
				}
			} else {
				int pos = docText.indexOf(token, last_search_pos);
				if (pos != -1) {
					begin = pos;
					end = pos + token.length();
					last_search_pos = pos + 1;
				}
			}

			if (begin != -1 && end != -1) {
				Token casToken = new Token(aJCas, begin, end);
				casToken.addToIndexes();
				last_end = end;
			}
		}
	}

	@Override
	protected String getRestRoute() {
		return "/segmenter";
	}
}