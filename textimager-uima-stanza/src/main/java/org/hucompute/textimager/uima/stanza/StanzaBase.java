package org.hucompute.textimager.uima.stanza;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import jep.JepException;

public abstract class StanzaBase extends JepAnnotator {

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		// set defaults
		// TODO schönerer Weg?
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "stanza==1.1.1";
		}
		if (envDepsConda == null || envDepsConda.isEmpty()) {
			envDepsConda = "";
		}
		if (envPythonVersion == null || envPythonVersion.isEmpty()) {
			envPythonVersion = "3.8";
		}
		if (envName == null || envName.isEmpty()) {
			envName = "textimager_stanza111_py38";
		}

		initConda();

		try {
			interpreter.exec("import stanza");

			// install models, existing files are automatically detected
			interpreter.exec("stanza.download('en')");
			interpreter.exec("stanza.download('de')");
		} catch (JepException ex) {
			throw new ResourceInitializationException(ex);
		}
	}

}