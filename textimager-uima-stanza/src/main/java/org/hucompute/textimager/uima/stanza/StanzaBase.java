package org.hucompute.textimager.uima.stanza;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import jep.JepException;

public abstract class StanzaBase extends JepAnnotator {
	@Override
	
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		try {
			interp.exec("import stanza"); 
			interp.exec("from stanza.utils.conll import CoNLL");
			
		} catch (JepException ex) {
			throw new ResourceInitializationException(ex);
		}
		
	}
}
