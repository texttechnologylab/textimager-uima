package org.hucompute.textimager.uima.fastsense;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONObject;

public class FastSense extends DockerRestAnnotator {
	private FastSenseEN fsEn = null;
		
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		fsEn = new FastSenseEN();
		fsEn.initialize(aContext);
	}
	
	@Override
	public void destroy() {
		fsEn.destroy();
		super.destroy();
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String lang = aJCas.getDocumentLanguage();
		if (lang.equalsIgnoreCase("en")) {
			fsEn.process(aJCas);
		} else if (lang.equalsIgnoreCase("de")) {
			// TODO integrate from old FastSense project
		} else {	
			throw new AnalysisEngineProcessException(new Exception("fastSense only supports EN and DE languages."));
		}
	}

	@Override
	protected String getDefaultDockerImage() {
		return null;
	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		return null;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
	}
	
}
