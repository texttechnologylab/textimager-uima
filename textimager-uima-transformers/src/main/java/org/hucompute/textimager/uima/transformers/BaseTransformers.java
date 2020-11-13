package org.hucompute.textimager.uima.transformers;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import jep.JepException;
import jep.SharedInterpreter;

public abstract class BaseTransformers extends JepAnnotator {

	public static SharedInterpreter interp;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		
		super.initialize(aContext);
		System.out.println("initializing transformers base class...");

		// set defaults
		// TODO schönerer Weg?
		if (condaBashScript == null || condaBashScript.isEmpty()) {
			condaBashScript = "transformers310_setup.sh";
		}
		if (envDepsPip == null || envDepsPip.isEmpty()) {
			envDepsPip = "transformers==3.1.0 ";
		}
		if (envDepsConda == null || envDepsConda.isEmpty()) {
			envDepsConda = "";
		}
		if (envPythonVersion == null || envPythonVersion.isEmpty()) {
			envPythonVersion = "3.7";
		}
		if (envName == null || envName.isEmpty()) {
			envName = "textimager_transformers310_py37";
		}
		if (condaVersion == null || condaVersion.isEmpty()) {
			condaVersion = "py37_4.8.3";
		}
		
		System.out.println("initializing transformers base class: conda");
		
		initConda();
		
		System.out.println("initializing transformers base class: interprter extras...");
		
		try {
			interpreter.exec("import os");
			interpreter.exec("import sys");
			interpreter.exec("from tranformers import pipeline, AutoTokenizer"); 
			interpreter.exec("from java.lang import System");
		} catch (JepException ex) {
			throw new ResourceInitializationException(ex);
		}
		
		System.out.println("initializing transformers base class done");

	}

	// Adds the "words" and "spaces" arrays for spaCy to the JSON object
	protected void jsonAddWordsAndSpaces(JCas aJCas, HashMap<String, Object> json) {
		ArrayList<String> jsonWords = new ArrayList<>();
		ArrayList<Boolean> jsonSpaces = new ArrayList<>();

		Token lastToken = null;
		for (Token token : JCasUtil.select(aJCas, Token.class)) {
			// Recreate spaCy Doc Text: Add "space" token if more than 1 space between words
			if (lastToken != null) {
				if (lastToken.getEnd() == token.getBegin()) {
					// No space
					jsonWords.add(token.getCoveredText());
					jsonSpaces.add(false);
				} else {
					int num = token.getBegin() - lastToken.getEnd();
					if (num > 1) {
						// Add space to last word
						jsonSpaces.add(true);
						// Add "space" token with num-1 spaces
						jsonWords.add(new String(new char[num-1]).replace("\0", " "));
						// ... followed by no space and the next word
						jsonSpaces.add(false);
						jsonWords.add(token.getCoveredText());
					} else {
						jsonWords.add(token.getCoveredText());
						jsonSpaces.add(true);
					}
				}
			} else {
				jsonWords.add(token.getCoveredText());
			}

			lastToken = token;
		}

		// Handle last token
		if (lastToken != null) {
			if (aJCas.getDocumentText().length() == lastToken.getEnd())	{
				jsonSpaces.add(false);
			} else {
				int num = aJCas.getDocumentText().length() - lastToken.getEnd();
				if (num > 1) {
					jsonSpaces.add(true);
					jsonWords.add(new String(new char[num-1]).replace("\0", " "));
					jsonSpaces.add(false);
				} else {
					jsonSpaces.add(true);
				}
			}
		}
		json.put("words", jsonWords);
		json.put("spaces", jsonSpaces);
	}


	protected HashMap<String, Object>  buildJSON(JCas aJCas) {
		HashMap<String, Object> json = new HashMap<>();
		json.put("lang", aJCas.getDocumentLanguage());
		jsonAddWordsAndSpaces(aJCas, json);
		return json;
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
