package org.hucompute.textimager.uima.NeuralnetworkNER;

import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;

/**
 * Neuralnetwork NER implementation class.
 *
 * @author Manuel Stoeckel
 */
public class NeuralNER extends NeuralNERBase {
	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		JSONObject json = new JSONObject();
		jsonAddModel(aJCas, json);
		jsonAddWordsAndCharIDs(aJCas, json);
		return json;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
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