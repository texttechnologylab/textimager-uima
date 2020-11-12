package org.hucompute.textimager.uima.allennlp;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import jep.JepException;

public class AllenNLPOpenIE extends AllenNLPBase {
	/**
	 * Overwrite CAS Language?
	 */
	public static final String PARAM_LANGUAGE = "language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		try {
			interpreter.exec("predictor = Predictor.from_path('https://storage.googleapis.com/allennlp-public-models/openie-model.2020.03.26.tar.gz')");			
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
			interpreter.set("lang", lang);
			interpreter.set("text", text);
			interpreter.exec("predicted = predictor.predict(sentence=text)");
			interpreter.exec("verbs = predicted.get('verbs')");
			System.out.println(interpreter.getValue("verbs"));
		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}
