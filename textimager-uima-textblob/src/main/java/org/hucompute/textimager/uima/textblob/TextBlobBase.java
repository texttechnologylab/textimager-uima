package org.hucompute.textimager.uima.textblob;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

public abstract class TextBlobBase extends JepAnnotator {
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		System.out.println("initializing textblob base class...");

		// set defaults
		// TODO schönerer Weg?
		if (condaBashScript == null || condaBashScript.isEmpty()) {
			condaBashScript = "textblob_0.15.3_v2_setup.sh";
		}
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "spacy==2.3.0 textblob==0.15.3 textblob-de==0.4.3";
		}
		if (envDepsConda == null || envDepsConda.isEmpty()) {
			envDepsConda = "";
		}
		if (envPythonVersion == null || envPythonVersion.isEmpty()) {
			envPythonVersion = "3.7";
		}
		if (envName == null || envName.isEmpty()) {
			envName = "textimager_textblob0.15.3_py37_v6";
		}
		if (condaVersion == null || condaVersion.isEmpty()) {
			condaVersion = "py37_4.8.3";
		}		

		System.out.println("initializing textblob base class: conda");
		
		initConda();		

		System.out.println("initializing textblob base class done");
	}
}
