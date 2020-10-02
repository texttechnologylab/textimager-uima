package org.hucompute.textimager.uima.allennlp;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
//import org.hucompute.textimager.uima.type.Sentiment;

import jep.JepException;

public class AllenNLPSentiment extends AllenNLPBase {
	/**
	 * Overwrite CAS Language?
	 */
	public static final String PARAM_LANGUAGE = "language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			interp.exec("predictor = Predictor.from_path(\'https://storage.googleapis.com/allennlp-public-models/basic_stanford_sentiment_treebank-2020.06.09.tar.gz\')");			
		}
		catch (JepException e) {
			e.printStackTrace();
		}
    };


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		final CAS cas = aJCas.getCas();
		try {
			final Object lang = aJCas.getDocumentLanguage();
			final Object text = aJCas.getDocumentText();
			interp.set("lang", lang);
			interp.set("text", text);
			interp.exec("predicted = predictor.predict(sentence=text)");
			interp.exec("probs = predicted.get('probs')");
			double positive = (double)interp.getValue("probs[0]");
			double negative = (double)interp.getValue("probs[1]");
//			Sentiment sent = new Sentiment(aJCas, 0, (aJCas.getDocumentText()).length());
//			sent.setSentiment(positive);
//			sent.addToIndexes();
		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}