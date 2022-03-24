package org.hucompute.textimager.uima.text2wiki;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class Text2WikiTest {
	@Test
	public void text2wiki_de_hs() throws UIMAException {
		JCas cas = JCasFactory.createText(
				"Das ist ein IPhone von Apple. Und das ist ein iMac.",
				"de"
		);

		int[][] tokens = new int[][]{
				new int[]{0, 3}, 	// Das
				new int[]{4, 7}, 	// ist
				new int[]{8, 11}, 	// ein
				new int[]{12, 18},	// IPhone
				new int[]{19, 22}, 	// von
				new int[]{23, 28}, 	// Apple
				new int[]{28, 29}, 	// .
				new int[]{30, 33}, 	// Und
				new int[]{34, 37}, 	// das
				new int[]{38, 41}, 	// ist
				new int[]{42, 45}, 	// ein
				new int[]{46, 50}, 	// iMac
				new int[]{50, 51} 	// .
		};
		for (int[] t : tokens) {
			Token token = new Token(cas, t[0], t[1]);
			token.addToIndexes();
		}

		AnalysisEngineDescription text2wiki = createEngineDescription(Text2Wiki.class,
				Text2Wiki.PARAM_MODEL_NAME, "hs",
				Text2Wiki.PARAM_K, 10,
				Text2Wiki.PARAM_TH, 0.01f,
				Text2Wiki.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, text2wiki);

		System.out.println(XmlFormatter.getPrettyString(cas));
	}

	@Test
	public void text2wiki_de_ova() throws UIMAException {
		JCas cas = JCasFactory.createText(
				"Das ist ein IPhone von Apple. Und das ist ein iMac.",
				"de"
		);

		int[][] tokens = new int[][]{
				new int[]{0, 3}, 	// Das
				new int[]{4, 7}, 	// ist
				new int[]{8, 11}, 	// ein
				new int[]{12, 18},	// IPhone
				new int[]{19, 22}, 	// von
				new int[]{23, 28}, 	// Apple
				new int[]{28, 29}, 	// .
				new int[]{30, 33}, 	// Und
				new int[]{34, 37}, 	// das
				new int[]{38, 41}, 	// ist
				new int[]{42, 45}, 	// ein
				new int[]{46, 50}, 	// iMac
				new int[]{50, 51} 	// .
		};
		for (int[] t : tokens) {
			Token token = new Token(cas, t[0], t[1]);
			token.addToIndexes();
		}

		AnalysisEngineDescription text2wiki = createEngineDescription(Text2Wiki.class,
				Text2Wiki.PARAM_MODEL_NAME, "ova",
				Text2Wiki.PARAM_K, 10,
				Text2Wiki.PARAM_TH, 0.01f,
				Text2Wiki.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, text2wiki);

		System.out.println(XmlFormatter.getPrettyString(cas));
	}

	@Test
	public void text2wiki_en_hs() throws UIMAException {
		JCas cas = JCasFactory.createText(
				"This is an IPhone by Apple. And this is an iMac.",
				"en"
		);

		int[][] tokens = new int[][]{
				new int[]{0, 4},	// This
				new int[]{5, 7}, 	// is
				new int[]{8, 10}, 	// an
				new int[]{11, 17}, 	// IPhone
				new int[]{18, 20}, 	// by
				new int[]{21, 26}, 	// Apple
				new int[]{26, 27}, 	// .
				new int[]{28, 31}, 	// And
				new int[]{32, 36}, 	// this
				new int[]{37, 39}, 	// is
				new int[]{40, 42}, 	// an
				new int[]{43, 47}, 	// iMac
				new int[]{47, 48} 	// .
		};
		for (int[] t : tokens) {
			Token token = new Token(cas, t[0], t[1]);
			token.addToIndexes();
		}

		AnalysisEngineDescription text2wiki = createEngineDescription(Text2Wiki.class,
				Text2Wiki.PARAM_MODEL_NAME, "hs",
				Text2Wiki.PARAM_K, 10,
				Text2Wiki.PARAM_TH, 0.01f,
				Text2Wiki.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, text2wiki);

		System.out.println(XmlFormatter.getPrettyString(cas));
	}

	@Test
	public void text2wiki_en_ova() throws UIMAException {
		JCas cas = JCasFactory.createText(
				"This is an IPhone by Apple. And this is an iMac.",
				"en"
		);

		int[][] tokens = new int[][]{
				new int[]{0, 4},	// This
				new int[]{5, 7}, 	// is
				new int[]{8, 10}, 	// an
				new int[]{11, 17}, 	// IPhone
				new int[]{18, 20}, 	// by
				new int[]{21, 26}, 	// Apple
				new int[]{26, 27}, 	// .
				new int[]{28, 31}, 	// And
				new int[]{32, 36}, 	// this
				new int[]{37, 39}, 	// is
				new int[]{40, 42}, 	// an
				new int[]{43, 47}, 	// iMac
				new int[]{47, 48} 	// .
		};
		for (int[] t : tokens) {
			Token token = new Token(cas, t[0], t[1]);
			token.addToIndexes();
		}

		AnalysisEngineDescription text2wiki = createEngineDescription(Text2Wiki.class,
				Text2Wiki.PARAM_MODEL_NAME, "ova",
				Text2Wiki.PARAM_K, 10,
				Text2Wiki.PARAM_TH, 0.01f,
				Text2Wiki.PARAM_DOCKER_HOST_PORT, 8000
		);
		SimplePipeline.runPipeline(cas, text2wiki);

		System.out.println(XmlFormatter.getPrettyString(cas));
	}
}

