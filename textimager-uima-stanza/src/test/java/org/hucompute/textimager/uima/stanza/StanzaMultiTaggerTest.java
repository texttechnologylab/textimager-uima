package org.hucompute.textimager.uima.stanza;

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

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class StanzaMultiTaggerTest {

	@Test
	public void sentimentDeTest() throws UIMAException {
		String[] sentences = new String[] {
				" . ",
				"Das ist ja echt toll!",
				"Das gefällt mir gar nicht.",
				"Ich hasse dieses Auto.",
				"Ich hasse dieses Auto nicht.",
				"Mir egal...",
				"Dieses Tool berechnet die Stimmung pro Satz.  ",
				"   "
		};

		JCas jCas = JCasFactory.createJCas();
		jCas.setDocumentLanguage("de");

		StringBuilder sentence = new StringBuilder();
		for (String s : sentences) {
			sentence.append(s).append(" ");
		};
		jCas.setDocumentText(sentence.toString());

		AnalysisEngineDescription stanzaMultiTagger = createEngineDescription(StanzaMultiTagger.class);

		SimplePipeline.runPipeline(jCas, stanzaMultiTagger);

		System.out.println(XmlFormatter.getPrettyString(jCas));
	}
}

