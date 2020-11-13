package org.hucompute.textimager.uima.flair.ner;

import java.io.IOException;
import java.nio.file.Paths;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.hucompute.textimager.uima.flair.FlairNERBiofid;

import com.google.common.io.Files;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class TestFlairNERBiofid {

	@BeforeClass
	public static void setUpClass() throws ResourceInitializationException {
	}

	@Test
	public void test() throws UIMAException, IOException {
		String text = "Das ist ein Test mit Angela Merkel.\n"
				+ "Mehr Merkel Tests...\n";
		JCas jCas = JCasFactory.createText(text);

		int last_index = 0;
		int index = text.indexOf("\n");
		while (index > 0) {
			Sentence sentence = new Sentence(jCas, last_index, index);
			jCas.addFsToIndexes(sentence);

			last_index = index + 1;
			index = text.indexOf("\n", last_index);
		}

		AnalysisEngine engine = AnalysisEngineFactory.createEngine(FlairNERBiofid.class,
				FlairNERBiofid.PARAM_LANGUAGE, "de",
				FlairNERBiofid.PARAM_MODEL_LOCATION, "/home/ahemati/model.pt"
		);

		SimplePipeline.runPipeline(jCas, engine);
		
		JCasUtil.select(jCas, NamedEntity.class).forEach(ner -> {
			System.out.println(ner.getCoveredText() + ": " + ner);
		});
		
		assert JCasUtil.select(jCas, NamedEntity.class).size() > 0;
	}
}
