/*
package org.hucompute.textimager.uima.spacy;

import static org.junit.Assert.assertArrayEquals;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class SpaCyMultiTaggerTest {
	
	public void simpleExampleLa() throws UIMAException{
		
		JCas cas = JCasFactory.createText("Das ist ein iPhone von   Apple.");
		
		
		AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger.class,SpaCyMultiTagger.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy");
		
		SimplePipeline.runPipeline(cas, spacyMulti);
		
		
		int[][] tokens = new int[][] {
		new int[] { 0, 3 },
		new int[] { 4, 7 },
		new int[] { 8, 11 },
		new int[] { 12, 18 },
		new int[] { 19, 22 },
		new int[] { 25, 30 },
		new int[] { 30, 31 },
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
	
		String[] ents = new String[] { "MISC"};
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);
		
		assertArrayEquals(tokens, casTokens);
		assertArrayEquals(pos, casPos);
		assertArrayEquals(deps, casDeps);
		assertArrayEquals(ents, casEnts);
}
		
	
}

*/