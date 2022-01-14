package org.hucompute.textimager.uima.vader;

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

public class VaderSentimentTest {
	@Test
	public void vaderEnTest() throws UIMAException {
		String[] sentences = new String[] {
				"This is very great!",
				"I really dislike this.",
				"I hate this car.",
				"I don't dislike the car.",
				"I don't care...",
				"This tool computes the sentiment per sentence."
		};

		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("en");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			Sentence anno = new Sentence(jCas, sentence.length(), sentence.length()+s.length());
			anno.addToIndexes();
			sentence.append(s).append(" ");
		}
		jCas.setDocumentText(sentence.toString());

		AnalysisEngineDescription vader = createEngineDescription(VaderSentiment.class,
				VaderSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
		);

		SimplePipeline.runPipeline(jCas, vader);

		System.out.println(XmlFormatter.getPrettyString(jCas));
		for (Sentiment sentiment : JCasUtil.select(jCas, Sentiment.class)) {
			System.out.println(sentiment.getCoveredText() + " -> " + sentiment.getSentiment());
		}
	}

	@Test
	public void vaderFrTest() throws UIMAException {
		String[] sentences = new String[] {
				"C'est très bien!",
				"Je n'aime vraiment pas ça.",
				"Je déteste cette voiture.",
				"Je ne déteste pas la voiture.",
				"Je m'en fiche...",
				"Cet outil calcule le sentiment par phrase."
		};

		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("fr");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			Sentence anno = new Sentence(jCas, sentence.length(), sentence.length()+s.length());
			anno.addToIndexes();
			sentence.append(s).append(" ");
		}
		jCas.setDocumentText(sentence.toString());

		AnalysisEngineDescription vader = createEngineDescription(VaderSentiment.class,
				VaderSentiment.PARAM_SELECTION, "text,de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
		);

		SimplePipeline.runPipeline(jCas, vader);

		System.out.println(XmlFormatter.getPrettyString(jCas));
		for (Sentiment sentiment : JCasUtil.select(jCas, Sentiment.class)) {
			System.out.println(sentiment.getCoveredText() + " -> " + sentiment.getSentiment());
		}
	}
}

