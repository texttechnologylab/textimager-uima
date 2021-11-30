package org.hucompute.textimager.uima.gnfinder;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class GNfinderTest {
	@Test
	public void gnfinderTest() throws UIMAException {
		//JCas cas = JCasFactory.createText("Pomatomus saltator und Parus major im Beispiel.", "de");
		JCas cas = JCasFactory.createText("Homo sapiens Linnaeus", "de");

		AnalysisEngineDescription gnFinder = createEngineDescription(GNfinder.class,
				GNfinder.PARAM_DOCKER_HOST_PORT, 8888,
				GNfinder.PARAM_VERIFICATION, true,
				GNfinder.PARAM_VERIFICATION_SOURCES, "1"
		);

		SimplePipeline.runPipeline(cas, gnFinder);

		System.out.println(XmlFormatter.getPrettyString(cas));

		for (NamedEntity ne : JCasUtil.select(cas, NamedEntity.class)) {
			System.out.println("!" + ne.getCoveredText() + "!");
		}
	}
}

