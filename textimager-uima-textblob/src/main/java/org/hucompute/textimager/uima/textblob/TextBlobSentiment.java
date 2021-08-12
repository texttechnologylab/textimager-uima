package org.hucompute.textimager.uima.textblob;

import jep.JepException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.type.Sentiment;

import java.util.HashMap;

public class TextBlobSentiment extends DockerRestAnnotator {
	/**
	 * Comma separated list of selection to process in order: "text",
	 * or "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph", ...
	 */
	public static final String PARAM_SELECTION = "selection";
	@ConfigurationParameter(name = PARAM_SELECTION, mandatory = true)
	protected String selection;

	private void processSelection(JCas aJCas, Annotation ref) throws JepException {
		String text;
		int begin;
		int end;

		if (ref != null) {
			text = ref.getCoveredText();
			begin = ref.getBegin();
			end = ref.getEnd();
		} else {
			text = aJCas.getDocumentText();
			begin = 0;
			end = text.length();
		}

		interpreter.set("text", (Object)text);
		interpreter.exec("doc = TextBlob(text)");
		interpreter.exec("doc_sentiment = { \"sentiment\": doc.sentiment.polarity, \"subjectivity\": doc.sentiment.subjectivity }");

		HashMap<String, Double> sentence_sentiment = (HashMap<String, Double>)interpreter.getValue("doc_sentiment");

		Sentiment annoSent = new Sentiment(aJCas, begin, end);
		annoSent.setSentiment((Double)sentence_sentiment.get("sentiment"));
		annoSent.setSubjectivity((Double)sentence_sentiment.get("subjectivity"));
		annoSent.addToIndexes();
	}

	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			if (aJCas.getDocumentLanguage().equals("de")) {
				interpreter.exec("from textblob_de import TextBlobDE as TextBlob");
			}
			else {
				interpreter.exec("from textblob import TextBlob");
			}

			String[] selections = selection.split(",", -1);
			for (String sel : selections) {
				if (sel.equals("text")) {
					processSelection(aJCas, null);
				}
				else {
					Class<Annotation> clazz = (Class<Annotation>) Class.forName(sel);
					for (Annotation selection : JCasUtil.select(aJCas, clazz)) {
						processSelection(aJCas, selection);
					}
				}
			}

		} catch (JepException | ClassNotFoundException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
