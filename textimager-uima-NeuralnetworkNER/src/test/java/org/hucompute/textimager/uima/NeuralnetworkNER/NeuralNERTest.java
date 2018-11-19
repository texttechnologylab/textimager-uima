package org.hucompute.textimager.uima.NeuralnetworkNER;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

/**
 * Unit-tests for neuralnetwork NER tagging.
 *
 * @author Manuel Stoeckel
 */
public class NeuralNERTest {

	private JCas getExampleJCas() throws UIMAException {
		JCas cas = JCasFactory.createText("Das ist ein iPhone von Apple");
		cas.setDocumentLanguage("de");

		Token t1 = new Token(cas, 0, 3);
		t1.addToIndexes();
		Token t2 = new Token(cas, 4, 7);
		t2.addToIndexes();
		Token t3 = new Token(cas, 8, 11);
		t3.addToIndexes();
		Token t4 = new Token(cas, 12, 18);
		t4.addToIndexes();
		Token t5 = new Token(cas, 19, 22);
		t5.addToIndexes();
		Token t6 = new Token(cas, 23, 28);
		t6.addToIndexes();

		return cas;
	}

	@Test
	public void test_conll2010() throws UIMAException {
		JCas cas = getExampleJCas();

		AnalysisEngineDescription spacyNer = createEngineDescription(NeuralNER.class,
				NeuralNER.PARAM_DOCKER_IMAGE, "textimager-neuralnetwork-ner",
				NeuralNER.PARAM_MODEL_NAME, "conll2010-tuebadz");

		SimplePipeline.runPipeline(cas, spacyNer);

		String[] ents = new String[]{"B-ORG"};

		String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(NamedEntity::getValue).toArray(String[]::new);

		assertArrayEquals(ents, casEnts);
	}

	@Test
	public void test_conll() throws UIMAException {
		JCas cas = getExampleJCas();

		AnalysisEngineDescription spacyNer = createEngineDescription(NeuralNER.class,
				NeuralNER.PARAM_DOCKER_IMAGE, "textimager-neuralnetwork-ner",
				NeuralNER.PARAM_MODEL_NAME, "conll");

		SimplePipeline.runPipeline(cas, spacyNer);

		String[] ents = new String[]{};

		String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(NamedEntity::getValue).toArray(String[]::new);

		assertArrayEquals(ents, casEnts);
	}

	@Test
	public void test_europarl() throws UIMAException {
		JCas cas = getExampleJCas();

		AnalysisEngineDescription spacyNer = createEngineDescription(NeuralNER.class,
				NeuralNER.PARAM_DOCKER_IMAGE, "textimager-neuralnetwork-ner",
				NeuralNER.PARAM_MODEL_NAME, "conll-germeval-tuebadz-europarl");

		SimplePipeline.runPipeline(cas, spacyNer);

		String[] ents = new String[]{"B-MISC", "B-ORG"};

		String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(NamedEntity::getValue).toArray(String[]::new);

		assertArrayEquals(ents, casEnts);
	}

	@Test
	public void test_germeval() throws UIMAException {
		JCas cas = getExampleJCas();

		AnalysisEngineDescription spacyNer = createEngineDescription(NeuralNER.class,
				NeuralNER.PARAM_DOCKER_IMAGE, "textimager-neuralnetwork-ner",
				NeuralNER.PARAM_MODEL_NAME, "germeval");

		SimplePipeline.runPipeline(cas, spacyNer);

		String[] ents = new String[]{"B-OTH", "B-ORG"};

		String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(NamedEntity::getValue).toArray(String[]::new);

		assertArrayEquals(ents, casEnts);
	}
}
