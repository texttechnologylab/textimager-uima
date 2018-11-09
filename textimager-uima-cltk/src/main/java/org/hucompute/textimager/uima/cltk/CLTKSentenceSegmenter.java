package org.hucompute.textimager.uima.cltk;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class CLTKSentenceSegmenter extends CLTKBase {
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
		};
	}

	@Override
	protected String getRestRoute() {
		return "/sentence";
	}
}