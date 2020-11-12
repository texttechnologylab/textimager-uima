package org.hucompute.textimager.uima.allennlp;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import jep.JepException;
import jep.SharedInterpreter;

public abstract class AllenNLPBase extends JepAnnotator {

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		System.out.println("initializing allennlp base class...");

		// set defaults
		// TODO schönerer Weg?
//		if (condaBashScript == null || condaBashScript.isEmpty()) {
//			condaBashScript = "spacy230_v2_setup.sh";
//		}
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "allennlp==1.2.1 textblob==0.15.3 textblob-de==0.4.3";
		}
		if (envDepsConda == null || envDepsConda.isEmpty()) {
			envDepsConda = "";
		}
		if (envPythonVersion == null || envPythonVersion.isEmpty()) {
			envPythonVersion = "3.7";
		}
		if (envName == null || envName.isEmpty()) {
			envName = "textimager_allennlp121_py37_v1";
		}
		if (condaVersion == null || condaVersion.isEmpty()) {
			condaVersion = "py37_4.8.3";
		}
		
		System.out.println("initializing spacy base class: conda");
		
		initConda();
		
		System.out.println("initializing spacy base class: interprter extras...");
		
		try {
			interpreter.exec("import sys");
			interpreter.exec("sys.argv=['']");
			interpreter.exec("from allennlp.predictors.predictor import Predictor");
			interpreter.exec("import allennlp_models.structured_prediction");
			interpreter.exec("import allennlp_models.tagging");
		} catch (JepException ex) {
			throw new ResourceInitializationException(ex);
		}
		
		System.out.println("initializing allennlp base class done");
		
	}
}