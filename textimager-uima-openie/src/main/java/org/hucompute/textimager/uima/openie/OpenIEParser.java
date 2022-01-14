package org.hucompute.textimager.uima.openie;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.type.OpenIERelation;
import org.json.JSONObject;

public class OpenIEParser extends DockerRestAnnotator {
	@Override
	protected String getDefaultDockerImage() {
		return "textimager-uima-service-openie";
	}

	@Override
	protected String getDefaultDockerImageTag() {
		return "0.1";
	}

	@Override
	protected int getDefaultDockerPort() {
		return 8080;
	}

	@Override
	protected String getRestRoute() {
		return "/openie/process";
	}

	@Override
	protected String getAnnotatorVersion() {
		return "0.1";
	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
		JSONObject result = new JSONObject();
		result.put("text", aJCas.getDocumentText());
		result.put("language", aJCas.getDocumentLanguage());
		return result;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		if (jsonResult.has("extractions")) {
			for (Object extractionJson : jsonResult.getJSONArray("extractions")) {
				JSONObject extraction = (JSONObject) extractionJson;

				OpenIERelation relation = new OpenIERelation(aJCas);

				relation.setBegin(extraction.getInt("begin"));
				relation.setEnd(extraction.getInt("end"));
				relation.setConfidence(extraction.getDouble("confidence"));

				relation.setBeginArg1(extraction.getInt("beginArg1"));
				relation.setEndArg1(extraction.getInt("endArg1"));
				relation.setValueArg1(extraction.getString("valueArg1"));

				relation.setBeginRel(extraction.getInt("beginRel"));
				relation.setEndRel(extraction.getInt("endRel"));
				relation.setValueRel(extraction.getString("valueRel"));

				relation.setBeginArg2(extraction.getInt("beginArg2"));
				relation.setEndArg2(extraction.getInt("endArg2"));
				relation.setValueArg2(extraction.getString("valueArg2"));

				aJCas.addFsToIndexes(relation);

				addAnnotatorComment(aJCas, relation);
			}
		}
	}
}
