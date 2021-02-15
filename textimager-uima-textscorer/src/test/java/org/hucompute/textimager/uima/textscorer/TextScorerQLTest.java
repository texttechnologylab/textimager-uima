package org.hucompute.textimager.uima.textscorer;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.textscorer.TextScorerQL;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;

public class TextScorerQLTest {
	@Test
	public void qlTest() throws UIMAException {
		JCas cas = JCasFactory.createText("Ach so. Also ich sehe eine Frau, die den Haushalt macht,"
				+ " die den Abwasch macht. Die gerade Teller abtrocknet. Daneben sehe ich zwei Kinder, "
				+ "die, äh, wie üblich, äh, sich streiten. Und auch, äh, gerade der, das Mädchen sorgt "
				+ "dafür, dass ihr Bruder, äh, mit dem Stuhl umfällt. Das wird bestimmt sehr aufregend "
				+ "sein, wenn die Mutter wordcancel merkt, was hinter ihrem Rücken passiert. multiSecPause "
				+ "Und, äh, das andere, die andere Katastrophe ist, dass, äh, das Wasser aus der Spüle überläuft. "
				+ "multiSecPause Also mehr sehe ich jetzt nich im Moment.", "de");

		AnalysisEngineDescription taQl = createEngineDescription(TextScorerQL.class);

		SimplePipeline.runPipeline(cas, taQl);
		
		for (Token t : JCasUtil.select(cas, Token.class)) {
			System.out.println("!~" + t.getCoveredText() + "!~");
			System.out.println(t);
		}

		int[][] tokens = new int[][] {
			new int[] { 0, 4 },
			new int[] { 5, 7 },
			new int[] { 8, 9 },
			new int[] { 10, 14 },
			new int[] { 14, 15 },
		};
		int[][] casTokens = (int[][]) JCasUtil.select(cas, Token.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

		String[] pos = new String[] {
				"DT", "VBZ", "DT", "NN", ".",
		};
		String[] casPos = (String[]) JCasUtil.select(cas, POS.class).stream().map(p -> p.getPosValue()).toArray(String[]::new);

		String[] deps = new String[] {
				"SB","--", "NK", "PD", "PG","NK", "PUNCT",
		};
		String[] casDeps = (String[]) JCasUtil.select(cas, Dependency.class).stream().map(p -> p.getDependencyType()).toArray(String[]::new);

		String[] ents = new String[] { "MISC","ORG"};
		String[] casEnts = (String[]) JCasUtil.select(cas, NamedEntity.class).stream().map(p -> p.getValue()).toArray(String[]::new);
		
		System.out.println(XmlFormatter.getPrettyString(cas));
		
//		assertArrayEquals(tokens, casTokens);
//		assertArrayEquals(pos, casPos);
//		assertArrayEquals(deps, casDeps);
//		assertArrayEquals(ents, casEnts);
	}
}

