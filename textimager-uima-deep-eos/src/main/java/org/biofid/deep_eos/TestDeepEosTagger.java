package org.biofid.deep_eos;


import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

/**
 * Created on 09.10.19.
 */
public class TestDeepEosTagger {
	
	public static void main(String[] args) throws UIMAException {
		String asdf = "Dr. Baumartz hat es geschafft.";
		System.out.println(asdf.length());
		JCas jCas = JCasFactory.createText("Dr. Baumartz hat es geschafft. Z. B. geht es jetzt.");
		AnalysisEngine analysisEngine = AnalysisEngineFactory.createEngine(DeepEosTagger.class,
				DeepEosTagger.PARAM_MODEL_NAME, "de",
				DeepEosTagger.PARAM_VERBOSE, true);
		SimplePipeline.runPipeline(jCas, analysisEngine);
		
	}
}
