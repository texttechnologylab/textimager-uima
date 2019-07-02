package org.hucompute.textimager.uima.bigsense;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.bigsense.implementation.BigSenseENImplementation;
import org.json.JSONObject;

public class BigSenseEN extends DockerRestAnnotator {
	BigSenseENImplementation impl;

	@Override
	protected String getDefaultDockerImage() {
		return "texttechnologylab/textimager-bigsense-en:2";
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		impl = new BigSenseENImplementation();
	}
	
	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		return impl.buildJSON(aJCas);
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		impl.updateCAS(aJCas, jsonResult);
	}

	@Override
	protected String getRestRoute() {
		return "/disambiguate";
	}
}
