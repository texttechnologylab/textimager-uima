package org.hucompute.textimager.uima.spacy;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class SpaCySentenceSegmenter extends SpaCyBase {
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		super.process(aJCas);
	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		JSONObject json = new JSONObject();
		json.put("lang", aJCas.getDocumentLanguage());
		jsonAddWordsAndSpaces(aJCas, json);
		return json;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		JSONArray sentences = jsonResult.getJSONArray("sents");
		sentences.forEach(s -> {
				JSONObject sentence = (JSONObject)s;
				int begin = sentence.getInt("start_char");
				int end = sentence.getInt("end_char");
				Sentence casSentence = new Sentence(aJCas, begin, end);
				casSentence.addToIndexes();
			});
	}

	@Override
	protected String getRestRoute() {
		return "/sentence";
	}
}