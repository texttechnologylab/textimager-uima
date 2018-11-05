package org.hucompute.textimager.uima.spacy;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class SpaCyTokenizer extends SpaCyBase {
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		super.process(aJCas);
	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		return new JSONObject()
			.put("lang", aJCas.getDocumentLanguage())
			.put("text", aJCas.getDocumentText());
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		JSONArray tokens = jsonResult.getJSONArray("token");
		tokens.forEach(t -> {
				JSONObject token = (JSONObject)t;
				if (!token.getBoolean("is_space")) {
					int begin = token.getInt("idx");
					int end = begin + token.getInt("length");
					Token casToken = new Token(aJCas, begin, end);
					casToken.addToIndexes();
				}
			});
	}

	@Override
	protected String getRestRoute() {
		return "/tokenizer";
	}
}
