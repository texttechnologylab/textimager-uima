package org.hucompute.textimager.uima.stanza;

import org.apache.uima.UimaContext;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import jep.JepException;
import jep.SharedInterpreter;
import jep.SubInterpreter;

public abstract class StanzaBase extends JepAnnotator {
	protected SubInterpreter interp ;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		if(interp == null)
			interp =setUpInter(pythonHome, interp);
		try {
			interp.exec("import stanza");
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