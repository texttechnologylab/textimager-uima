package org.hucompute.textimager.uima.spacy;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class SpaCyMultiTaggerTest {
	@Test
	public void multiTaggerTest() throws UIMAException {
		JCas cas = JCasFactory.createText("Das ist ein IPhone von Apple.", "de");

		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger.class,
				SpaCyMultiTagger.PARAM_CONDA_VERSION, "py37_4.8.3",
				SpaCyMultiTagger.PARAM_CONDA_ENV_NAME, "textimager_spacy37_2",
				SpaCyMultiTagger.PARAM_CONDA_ENV_PYTHON_VERSION, "3.7",
				SpaCyMultiTagger.PARAM_CONDA_ENV_DEPS_CONDA, "",
				SpaCyMultiTagger.PARAM_CONDA_ENV_DEPS_PIP, "spacy==2.3.0",
				SpaCyMultiTagger.PARAM_CONDA_BASH_SCRIPT, "spacy_setup.sh"
		);

		SimplePipeline.runPipeline(cas, spacyMulti);

		int[][] tokens = new int[][] {
			new int[] { 0, 3 },
			new int[] { 4, 7 },
			new int[] { 8, 11 },
			new int[] { 12, 18 },
			new int[] { 19, 22 },
			new int[] { 23, 28 },
			new int[] { 28, 29 },
		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"PDS", "VAFIN", "ART", "NN", "APPR", "NE", "$.",
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"SB","--", "NK", "PD", "PG","NK", "PUNCT",
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "MISC","ORG"};
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);
		System.out.println(XmlFormatter.getPrettyString(cas));
		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
	}
}

