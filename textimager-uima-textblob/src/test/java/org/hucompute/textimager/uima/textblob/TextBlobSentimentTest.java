package org.hucompute.textimager.uima.textblob;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

public class TextBlobSentimentTest {
	@Test
	public void multiTaggerTestEN() throws UIMAException {
		String[] sentences = new String[] {
				"This is a nice car.",
				"I love this house !",
				"  "
		};

		JCas cas = JCasFactory.createJCas();
		cas.setDocumentLanguage("en");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			sentence.append(s).append(" ");
		};
		cas.setDocumentText(sentence.toString());

		AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);

		AnalysisEngineDescription textblobSentiment = createEngineDescription(TextBlobSentiment.class,
				TextBlobSentiment.PARAM_DOCKER_HOST_PORT, 8001,
				TextBlobSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
		);

		SimplePipeline.runPipeline(cas, segmenter, textblobSentiment);

		for (Sentiment sent : JCasUtil.select(cas, Sentiment.class)) {
			System.out.println("Sentiment:");
			System.out.println(sent.getSentiment());
			System.out.println(sent.getSubjectivity());
			System.out.println(sent.getCoveredText());
			System.out.println("----------------------");
		}

		System.out.println(XmlFormatter.getPrettyString(cas));
	}

	@Test
	public void multiTaggerTestENNaiveBayesAnalyzer() throws UIMAException {
		String[] sentences = new String[] {
				"This is a nice car.",
				"I love this house !",
				"  "
		};

		JCas cas = JCasFactory.createJCas();
		cas.setDocumentLanguage("en");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			sentence.append(s).append(" ");
		};
		cas.setDocumentText(sentence.toString());

		AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);

		AnalysisEngineDescription textblobSentiment = createEngineDescription(TextBlobSentiment.class,
				TextBlobSentiment.PARAM_DOCKER_HOST_PORT, 8001,
				TextBlobSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
				TextBlobSentiment.PARAM_MODEL_NAME, "NaiveBayesAnalyzer"
		);

		SimplePipeline.runPipeline(cas, segmenter, textblobSentiment);

		for (Sentiment sent : JCasUtil.select(cas, Sentiment.class)) {
			System.out.println("Sentiment:");
			System.out.println(sent.getSentiment());
			System.out.println(sent.getSubjectivity());
			System.out.println(sent.getCoveredText());
			System.out.println("----------------------");
		}

		System.out.println(XmlFormatter.getPrettyString(cas));
	}

	@Test
	public void multiTaggerTestFR() throws UIMAException {
		String[] sentences = new String[] {
				"C'est une super voiture.",
				"J'aime cette maison !",
				"  "
		};

		JCas cas = JCasFactory.createJCas();
		cas.setDocumentLanguage("fr");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			sentence.append(s).append(" ");
		};
		cas.setDocumentText(sentence.toString());

		AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);

		AnalysisEngineDescription textblobSentiment = createEngineDescription(TextBlobSentiment.class,
				TextBlobSentiment.PARAM_DOCKER_HOST_PORT, 8001,
				TextBlobSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
		);

		SimplePipeline.runPipeline(cas, segmenter, textblobSentiment);

		for (Sentiment sent : JCasUtil.select(cas, Sentiment.class)) {
			System.out.println("Sentiment:");
			System.out.println(sent.getSentiment());
			System.out.println(sent.getSubjectivity());
			System.out.println(sent.getCoveredText());
			System.out.println("----------------------");
		}

		System.out.println(XmlFormatter.getPrettyString(cas));
	}

	@Test
	public void multiTaggerTestDE() throws UIMAException {
		String[] sentences = new String[] {
				" , ..  ",
				"Das ist ja echt toll!",
				"Das gefällt mir gar nicht.",
				"Ich hasse dieses Auto.",
				"Ich hasse dieses Auto nicht.",
				"Mir egal...",
				"Dieses Tool berechnet die Stimmung pro Satz.",
				"       "
		};

		JCas cas = JCasFactory.createJCas();
		cas.setDocumentLanguage("de");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			sentence.append(s).append(" ");
		};
		cas.setDocumentText(sentence.toString());

		AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);

		AnalysisEngineDescription textblobSentiment = createEngineDescription(TextBlobSentiment.class,
				TextBlobSentiment.PARAM_DOCKER_HOST_PORT, 8001,
				TextBlobSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence");

		SimplePipeline.runPipeline(cas, segmenter, textblobSentiment);

		for (Sentiment sent : JCasUtil.select(cas, Sentiment.class)) {
			System.out.println("Sentiment:");
			System.out.println(sent.getSentiment());
			System.out.println(sent.getSubjectivity());
			System.out.println(sent.getCoveredText());
			System.out.println("----------------------");
		}

		System.out.println(XmlFormatter.getPrettyString(cas));
	}
}
