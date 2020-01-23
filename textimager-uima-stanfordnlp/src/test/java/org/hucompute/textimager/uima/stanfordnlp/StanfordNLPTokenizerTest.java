package org.hucompute.textimager.uima.stanfordnlp;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class StanfordNLPTokenizerTest {
	/***
	 * TODO Running multiple tests will fail after first test
	 */
	
	@Test
	public void textTokenizer() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist   ein Test. Und noch      einer.  ");
		cas.setDocumentLanguage("de");
		
		AnalysisEngineDescription stanfordnlpTokenizer = createEngineDescription(StanfordNLPTokenizer.class,
				StanfordNLPTokenizer.PARAM_PYTHON_HOME, "/home/stud_homes/baumartz/miniconda3/envs/jeptest",
				StanfordNLPTokenizer.PARAM_LIBJEP_PATH, "/home/stud_homes/baumartz/miniconda3/envs/jeptest/lib/python3.7/site-packages/jep/libjep.so",
				StanfordNLPTokenizer.PARAM_USE_GPU, true,
				StanfordNLPTokenizer.PARAM_MODEL_LOCATION, "/mnt/tb1/baumartz/stanfordnlp_resources/"
		);
		
		SimplePipeline.runPipeline(cas, stanfordnlpTokenizer);
		
		int[][] tokens = new int[][] {
			new int[] { 0, 3 },
			new int[] { 4, 7 },
			new int[] { 10, 13 },
			new int[] { 14, 18 },
			new int[] { 18, 19 },
			new int[] { 20, 23 },
			new int[] { 24, 28 },
			new int[] { 34, 39 },
			new int[] { 39, 40 },
		};

		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);
		
		assertArrayEquals(tokens, casTokens);
	}
	
	@Test
	public void textSenenceSegmenter() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist   ein Test. Und noch      einer.  ");
		cas.setDocumentLanguage("de");

		AnalysisEngineDescription stanfordnlpTokenizer = createEngineDescription(StanfordNLPTokenizer.class,
				StanfordNLPTokenizer.PARAM_PYTHON_HOME, "/home/stud_homes/baumartz/miniconda3/envs/jeptest",
				StanfordNLPTokenizer.PARAM_LIBJEP_PATH, "/home/stud_homes/baumartz/miniconda3/envs/jeptest/lib/python3.7/site-packages/jep/libjep.so",
				StanfordNLPTokenizer.PARAM_USE_GPU, true,
				StanfordNLPTokenizer.PARAM_MODEL_LOCATION, "/mnt/tb1/baumartz/stanfordnlp_resources/",
				StanfordNLPTokenizer.PARAM_WRITE_SENTENCES, true
		);
		
		SimplePipeline.runPipeline(cas, stanfordnlpTokenizer);
		
		String[] sentences = new String[] {
				"Das ist   ein Test.",
				"Und noch      einer."
		};
		
		String[] casSentences = (String[]) JCasUtil.select(cas, Sentence.class).stream().map(s -> s.getCoveredText()).toArray(String[]::new);
		
		assertArrayEquals(sentences, casSentences);
	}
	
	@Test
	public void textSenenceSegmenter2() throws UIMAException{
		JCas cas = JCasFactory.createText("Angela[1] Dorothea Merkel (* 17. Juli 1954 in Hamburg als Angela Dorothea Kasner) ist eine deutsche Politikerin (CDU). Sie ist seit dem 22. November 2005 Bundeskanzlerin der Bundesrepublik Deutschland. Vom 10. April 2000 bis zum 7. Dezember 2018 war sie CDU-Bundesvorsitzende. Im Oktober 2018 erklärte sie, sich spätestens mit Ablauf der Legislaturperiode 2021 aus der Politik zurückzuziehen.");
		cas.setDocumentLanguage("de");

		AnalysisEngineDescription stanfordnlpTokenizer = createEngineDescription(StanfordNLPTokenizer.class,
				StanfordNLPTokenizer.PARAM_PYTHON_HOME, "/home/stud_homes/baumartz/miniconda3/envs/jeptest",
				StanfordNLPTokenizer.PARAM_LIBJEP_PATH, "/home/stud_homes/baumartz/miniconda3/envs/jeptest/lib/python3.7/site-packages/jep/libjep.so",
				StanfordNLPTokenizer.PARAM_USE_GPU, true,
				StanfordNLPTokenizer.PARAM_MODEL_LOCATION, "/mnt/tb1/baumartz/stanfordnlp_resources/",
				StanfordNLPTokenizer.PARAM_WRITE_SENTENCES, true
		);
		
		SimplePipeline.runPipeline(cas, stanfordnlpTokenizer);
		
		String[] sentences = new String[] {
				"Angela[1] Dorothea Merkel (* 17.",
				"Juli 1954 in Hamburg als Angela Dorothea Kasner) ist eine deutsche Politikerin (CDU).",
				"Sie ist seit dem 22.",
				"November 2005 Bundeskanzlerin der Bundesrepublik Deutschland.",
				"Vom 10. April 2000 bis zum 7. Dezember 2018 war sie CDU-Bundesvorsitzende.",
				"Im Oktober 2018 erklärte sie, sich spätestens mit Ablauf der Legislaturperiode 2021 aus der Politik zurückzuziehen."
		};
		
		String[] casSentences = (String[]) JCasUtil.select(cas, Sentence.class).stream().map(s -> s.getCoveredText()).toArray(String[]::new);
		
		assertArrayEquals(sentences, casSentences);
	}
}
