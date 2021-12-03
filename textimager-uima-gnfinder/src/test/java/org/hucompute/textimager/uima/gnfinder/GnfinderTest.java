package org.hucompute.textimager.uima.gnfinder;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class GnfinderTest {
	@Test
	public void gnfinderTest() throws UIMAException {
		JCas cas = JCasFactory.createText("Pomatomus saltator und Parus major.", "de");
		//JCas cas = JCasFactory.createText("This is a test sentence.", "en");

		AnalysisEngineDescription gnFinder = createEngineDescription(Gnfinder.class);
//				SpaCyMultiTagger3.PARAM_DOCKER_REGISTRY, "localhost:5000",
//				SpaCyMultiTagger3.PARAM_DOCKER_NETWORK, "bridge",
//				SpaCyMultiTagger3.PARAM_DOCKER_HOSTNAME, "localhost",
//				SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
//		);
		SimplePipeline.runPipeline(cas, gnFinder);
	}
}

