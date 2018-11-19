package org.hucompute.textimager.uima.spacy;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

public class SpaCyNER extends SpaCyBase {
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}
	
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
		JSONArray ents = jsonResult.getJSONArray("ents");
		ents.forEach(e -> {
			JSONObject ent = (JSONObject) e;
			int begin = ent.getInt("start_char");
			int end = ent.getInt("end_char");
			String labelStr = ent.getString("label");
			NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
			neAnno.setValue(labelStr);
			neAnno.addToIndexes();
		});
	}

	@Override
	protected String getRestRoute() {
		return "/ner";
	}
}