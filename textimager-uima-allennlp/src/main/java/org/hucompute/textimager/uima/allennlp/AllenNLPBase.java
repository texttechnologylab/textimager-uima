package org.hucompute.textimager.uima.allennlp;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import jep.JepException;
import jep.SharedInterpreter;

public abstract class AllenNLPBase extends JepAnnotator {
	protected SharedInterpreter interp ;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		if(interp == null)
			interp = setUpInter(pythonHome, interp);
		try {
			interp.exec("import sys");
			interp.exec("sys.argv=['']");
			interp.exec("from allennlp.predictors.predictor import Predictor");
			interp.exec("import allennlp_models.tagging");
			
		} catch (JepException ex) {
			throw new ResourceInitializationException(ex);
		}
	}
	
	@Override
	public void destroy() {
		try {
			interp.close();
		} catch (JepException e) {
			e.printStackTrace();
		}
		super.destroy();
	}
}