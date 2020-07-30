package org.hucompute.textimager.uima.textblob;

import java.util.HashMap;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.Sentiment;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import jep.JepException;

public class TextBlobSentiment extends TextBlobBase {
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			if (aJCas.getDocumentLanguage().equals("de")) {
				interpreter.exec("from textblob_de import TextBlobDE as TextBlob");
			}
			else {
				interpreter.exec("from textblob import TextBlob");
			}
			
			// Per sentence senitment
			for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
				interpreter.set("text", (Object)sentence.getCoveredText());
				interpreter.exec("doc = TextBlob(text)");
				interpreter.exec("doc_sentiment = { \"sentiment\": doc.sentiment.polarity, \"subjectivity\": doc.sentiment.subjectivity }");
				HashMap<String, Double> sentence_sentiment = (HashMap<String, Double>)interpreter.getValue("doc_sentiment");
				Sentiment annoSent = new Sentiment(aJCas, sentence.getBegin(), sentence.getEnd());
				annoSent.setSentiment((Double)sentence_sentiment.get("sentiment"));
				annoSent.setSubjectivity((Double)sentence_sentiment.get("subjectivity"));
				annoSent.addToIndexes();
			}
		} catch (JepException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
