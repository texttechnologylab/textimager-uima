package org.hucompute.textimager.uima.bert.sentiment.german;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.Sentiment;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class GermanSentimentBertTest {
	@Test
	public void multiTaggerTest() throws UIMAException {
		String[] sentences = new String[] {
				"Das ist ja echt toll!",
				"Das gefällt mir gar nicht.",
				"Ich hasse dieses Auto.",
				"Ich hasse dieses Auto nicht.",
				"Mir egal...",
				"Dieses Tool berechnet die Stimmung pro Satz."
		};

		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("de");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			Sentence anno = new Sentence(jCas, sentence.length(), sentence.length()+s.length());
			anno.addToIndexes();
			sentence.append(s).append(" ");
		};
		jCas.setDocumentText(sentence.toString());

		AnalysisEngineDescription bertSentiment = createEngineDescription(GermanSentimentBert.class,
				GermanSentimentBert.PARAM_DOCKER_REGISTRY, "localhost:5000",
				GermanSentimentBert.PARAM_DOCKER_NETWORK, "bridge",
				GermanSentimentBert.PARAM_DOCKER_HOSTNAME, "localhost",
				GermanSentimentBert.PARAM_DOCKER_HOST_PORT, 8000,
				GermanSentimentBert.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
		);

		SimplePipeline.runPipeline(jCas, bertSentiment);

		System.out.println(XmlFormatter.getPrettyString(jCas));
		for (Sentiment sentiment : JCasUtil.select(jCas, Sentiment.class)) {
			System.out.println(sentiment.getCoveredText() + " -> " + sentiment.getSentiment());
		}
	}
}

