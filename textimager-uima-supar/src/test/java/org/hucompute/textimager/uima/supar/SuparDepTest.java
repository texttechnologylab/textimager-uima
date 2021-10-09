package org.hucompute.textimager.uima.supar;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CASRuntimeException;
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
	public void suparDepBiaffineDepEnTest() throws UIMAException, CASRuntimeException {
		// create document
		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText("I saw Sarah with a telescope. Every morning we look for shells in the sand. I put them in a special place in my room.");
		
		// add sentences
		new Sentence(jCas, 0, 29).addToIndexes();
		new Sentence(jCas, 30, 75).addToIndexes();
		new Sentence(jCas, 76, 117).addToIndexes();
		
		// add token
		int[][] tokensPosition = new int[][] {
				new int[] { 0, 1 },
				new int[] { 2, 5 },
				new int[] { 6, 11 },
				new int[] { 12, 16 },
				new int[] { 17, 18 },
				new int[] { 19, 28 },
				new int[] { 28, 29 },

				new int[] { 30, 35 },
				new int[] { 36, 43 },
				new int[] { 44, 46 },
				new int[] { 47, 51 },
				new int[] { 52, 55},
				new int[] { 56, 62 },
				new int[] { 63, 65 },
				new int[] { 66, 69 },
				new int[] { 70, 74 },
				new int[] { 74, 75 },

				new int[] { 76, 77 },
				new int[] { 78, 81 },
				new int[] { 82, 86 },
				new int[] { 87, 89 },
				new int[] { 90, 91 },
				new int[] { 92, 99 },
				new int[] { 100, 105 },
				new int[] { 106, 108 },
				new int[] { 109, 111 },
				new int[] { 112, 116 },
				new int[] { 116, 117 }
		};
		for (int[] pos : tokensPosition) {
			new Token(jCas, pos[0], pos[1]).addToIndexes();
		}

		// run pipeline with supar

		//AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
		//		SuparDep.PARAM_MODEL_NAME, "biaffine-dep-en"
		//);

		AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
				SuparDep.PARAM_MODEL_NAME, "biaffine-dep-en",
				SuparDep.PARAM_REST_ENDPOINT, "http://localhost:8000"
		);
		SimplePipeline.runPipeline(jCas, depParser);
		System.out.println(XmlFormatter.getPrettyString(jCas));
		
		// test dep positions
		int[][] casDepPoss = (int[][]) JCasUtil.select(jCas, Token.class).stream().map(p -> new int[] { p.getBegin(), p.getEnd() }).toArray(int[][]::new);
		assertArrayEquals(tokensPosition, casDepPoss);
		
		// test types
		String[] deps = new String[] {
				"nsubj","--", "dobj", "prep", "det","pobj", "punct",

				"det", "tmod", "nsubj", "--", "prep",
				"pobj", "prep", "det", "pobj", "punct",

				"nsubj", "--", "dobj", "prep", "det", "amod", "pobj",
				"prep", "poss", "pobj", "punct",
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

				tokensPosition[1 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[4 + 7],
				tokensPosition[5 + 7],
				tokensPosition[8 + 7],
				tokensPosition[6 + 7],
				tokensPosition[3 + 7],


				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[3 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[9 + 7 + 10],
				tokensPosition[7 + 7 + 10],
				tokensPosition[1 + 7 + 10],
		};
		int[][] casDepGovPoss = (int[][]) JCasUtil.select(jCas, Dependency.class).stream().map(p -> new int[] { p.getGovernor().getBegin(), p.getGovernor().getEnd() }).toArray(int[][]::new);
		assertArrayEquals(depGovPoss, casDepGovPoss);
	}



	@Test
	public void suparDepBiaffineRobertaDepEnTest() throws UIMAException, CASRuntimeException {
		// create document
		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText("I saw Sarah with a telescope. Every morning we look for shells in the sand. I put them in a special place in my room.");

		// add sentences
		new Sentence(jCas, 0, 29).addToIndexes();
		new Sentence(jCas, 30, 75).addToIndexes();
		new Sentence(jCas, 76, 117).addToIndexes();

		// add token
		int[][] tokensPosition = new int[][] {
				new int[] { 0, 1 },
				new int[] { 2, 5 },
				new int[] { 6, 11 },
				new int[] { 12, 16 },
				new int[] { 17, 18 },
				new int[] { 19, 28 },
				new int[] { 28, 29 },

				new int[] { 30, 35 },
				new int[] { 36, 43 },
				new int[] { 44, 46 },
				new int[] { 47, 51 },
				new int[] { 52, 55},
				new int[] { 56, 62 },
				new int[] { 63, 65 },
				new int[] { 66, 69 },
				new int[] { 70, 74 },
				new int[] { 74, 75 },

				new int[] { 76, 77 },
				new int[] { 78, 81 },
				new int[] { 82, 86 },
				new int[] { 87, 89 },
				new int[] { 90, 91 },
				new int[] { 92, 99 },
				new int[] { 100, 105 },
				new int[] { 106, 108 },
				new int[] { 109, 111 },
				new int[] { 112, 116 },
				new int[] { 116, 117 }
		};
		for (int[] pos : tokensPosition) {
			new Token(jCas, pos[0], pos[1]).addToIndexes();
		}

		// run pipeline with supar

		//AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
		//		SuparDep.PARAM_MODEL_NAME, "biaffine-dep-en"
		//);

		AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
				SuparDep.PARAM_MODEL_NAME, "biaffine-dep-roberta-en",
				SuparDep.PARAM_REST_ENDPOINT, "http://localhost:8000"
		);
		SimplePipeline.runPipeline(jCas, depParser);
		System.out.println(XmlFormatter.getPrettyString(jCas));

		// test dep positions
		int[][] casDepPoss = (int[][]) JCasUtil.select(jCas, Token.class).stream().map(p -> new int[] { p.getBegin(), p.getEnd() }).toArray(int[][]::new);
		assertArrayEquals(tokensPosition, casDepPoss);

		// test types
		String[] deps = new String[] {
				"nsubj","--", "dobj", "prep", "det","pobj", "punct",

				"det", "tmod", "nsubj", "--", "prep",
				"pobj", "prep", "det", "pobj", "punct",

				"nsubj", "--", "dobj", "prep", "det", "amod", "pobj",
				"prep", "poss", "pobj", "punct",
		};
		String[] casDeps = (String[]) JCasUtil.select(jCas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);
		assertArrayEquals(deps, casDeps);

		// test governors
		int[][] depGovPoss = new int[][] {
				tokensPosition[1],
				tokensPosition[1],
				tokensPosition[1],
				tokensPosition[2],
				tokensPosition[5],
				tokensPosition[3],
				tokensPosition[1],

				tokensPosition[1 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[4 + 7],
				tokensPosition[3 + 7],
				tokensPosition[8 + 7],
				tokensPosition[6 + 7],
				tokensPosition[3 + 7],


				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[3 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[9 + 7 + 10],
				tokensPosition[7 + 7 + 10],
				tokensPosition[1 + 7 + 10],
		};
		int[][] casDepGovPoss = (int[][]) JCasUtil.select(jCas, Dependency.class).stream().map(p -> new int[] { p.getGovernor().getBegin(), p.getGovernor().getEnd() }).toArray(int[][]::new);
		assertArrayEquals(depGovPoss, casDepGovPoss);
	}




	@Test
	public void suparDepCrf2oDepEnTest() throws UIMAException, CASRuntimeException {
		// create document
		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");
		jCas.setDocumentText("I saw Sarah with a telescope. Every morning we look for shells in the sand. I put them in a special place in my room.");

		// add sentences
		new Sentence(jCas, 0, 29).addToIndexes();
		new Sentence(jCas, 30, 75).addToIndexes();
		new Sentence(jCas, 76, 117).addToIndexes();

		// add token
		int[][] tokensPosition = new int[][] {
				new int[] { 0, 1 },
				new int[] { 2, 5 },
				new int[] { 6, 11 },
				new int[] { 12, 16 },
				new int[] { 17, 18 },
				new int[] { 19, 28 },
				new int[] { 28, 29 },

				new int[] { 30, 35 },
				new int[] { 36, 43 },
				new int[] { 44, 46 },
				new int[] { 47, 51 },
				new int[] { 52, 55},
				new int[] { 56, 62 },
				new int[] { 63, 65 },
				new int[] { 66, 69 },
				new int[] { 70, 74 },
				new int[] { 74, 75 },

				new int[] { 76, 77 },
				new int[] { 78, 81 },
				new int[] { 82, 86 },
				new int[] { 87, 89 },
				new int[] { 90, 91 },
				new int[] { 92, 99 },
				new int[] { 100, 105 },
				new int[] { 106, 108 },
				new int[] { 109, 111 },
				new int[] { 112, 116 },
				new int[] { 116, 117 }
		};
		for (int[] pos : tokensPosition) {
			new Token(jCas, pos[0], pos[1]).addToIndexes();
		}

		// run pipeline with supar

		//AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
		//		SuparDep.PARAM_MODEL_NAME, "biaffine-dep-en"
		//);

		AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
				SuparDep.PARAM_MODEL_NAME, "crf2o-dep-en",
				SuparDep.PARAM_REST_ENDPOINT, "http://localhost:8000"
		);
		SimplePipeline.runPipeline(jCas, depParser);
		System.out.println(XmlFormatter.getPrettyString(jCas));

		// test dep positions
		int[][] casDepPoss = (int[][]) JCasUtil.select(jCas, Token.class).stream().map(p -> new int[] { p.getBegin(), p.getEnd() }).toArray(int[][]::new);
		assertArrayEquals(tokensPosition, casDepPoss);

		// test types
		String[] deps = new String[] {
				"nsubj","--", "dobj", "prep", "det","pobj", "punct",

				"det", "tmod", "nsubj", "--", "prep",
				"pobj", "prep", "det", "pobj", "punct",

				"nsubj", "--", "dobj", "prep", "det", "amod", "pobj",
				"prep", "poss", "pobj", "punct",
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

				tokensPosition[1 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[3 + 7],
				tokensPosition[4 + 7],
				tokensPosition[5 + 7],
				tokensPosition[8 + 7],
				tokensPosition[6 + 7],
				tokensPosition[3 + 7],


				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[1 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[3 + 7 + 10],
				tokensPosition[6 + 7 + 10],
				tokensPosition[9 + 7 + 10],
				tokensPosition[7 + 7 + 10],
				tokensPosition[1 + 7 + 10],
		};
		int[][] casDepGovPoss = (int[][]) JCasUtil.select(jCas, Dependency.class).stream().map(p -> new int[] { p.getGovernor().getBegin(), p.getGovernor().getEnd() }).toArray(int[][]::new);
		assertArrayEquals(depGovPoss, casDepGovPoss);
	}



}
