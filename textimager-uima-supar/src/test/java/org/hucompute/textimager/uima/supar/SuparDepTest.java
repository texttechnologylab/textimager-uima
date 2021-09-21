package org.hucompute.textimager.uima.supar;

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

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class SuparDepTest {
	// TODO add tests for other models
	
	@Test
	public void suparDepBiaffineDepEnTest() throws UIMAException {
		// create document
		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText("I saw Sarah with a telescope.");
		
		// add sentences
		new Sentence(jCas, 0, 29).addToIndexes();
		
		// add token
		int[][] tokensPosition = new int[][] {
			new int[] { 0, 1 },
			new int[] { 2, 5 },
			new int[] { 6, 11 },
			new int[] { 12, 16 },
			new int[] { 17, 18 },
			new int[] { 19, 28 },
			new int[] { 28, 29 },
		};
		for (int[] pos : tokensPosition) {
			new Token(jCas, pos[0], pos[1]).addToIndexes();	
		}

		// run pipeline with supar
		AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
				SuparDep.PARAM_MODEL_NAME, "biaffine-dep-en"
		);
		SimplePipeline.runPipeline(jCas, depParser);
		System.out.println(XmlFormatter.getPrettyString(jCas));
		
		// test dep positions
		int[][] casDepPoss = (int[][]) JCasUtil.select(jCas, Token.class).stream().map(p -> new int[] { p.getBegin(), p.getEnd() }).toArray(int[][]::new);
		assertArrayEquals(tokensPosition, casDepPoss);
		
		// test types
		String[] deps = new String[] {
				"nsubj","--", "dobj", "prep", "det","pobj", "punct",
		};
		String[] casDeps = (String[]) JCasUtil.select(jCas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);
		assertArrayEquals(deps, casDeps);
		
		// test governors
		int[][] depGovPoss = new int[][] {
			tokensPosition[1],
			tokensPosition[1],
			tokensPosition[1],
			tokensPosition[1],
			tokensPosition[5],
			tokensPosition[3],
			tokensPosition[1],
		};
		int[][] casDepGovPoss = (int[][]) JCasUtil.select(jCas, Dependency.class).stream().map(p -> new int[] { p.getGovernor().getBegin(), p.getGovernor().getEnd() }).toArray(int[][]::new);
		assertArrayEquals(depGovPoss, casDepGovPoss);
	}
}
