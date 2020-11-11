package org.hucompute.textimager.uima.stanza;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import jep.JepException;
import jep.SharedInterpreter;
import jep.SubInterpreter;

public abstract class StanzaBase extends JepAnnotator {

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		// set defaults
		// TODO schönerer Weg?
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "stanza==1.0.1";
		}
		if (envDepsConda == null || envDepsConda.isEmpty()) {
			envDepsConda = "";
		}
		if (envPythonVersion == null || envPythonVersion.isEmpty()) {
			envPythonVersion = "3.7";
		}
		if (envName == null || envName.isEmpty()) {
			envName = "textimager_stanza101_py37";
		}

		initConda();

		try {
			interpreter.exec("import stanza");
		} catch (JepException ex) {
			throw new ResourceInitializationException(ex);
		}
	}

}