package org.hucompute.textimager.uima.spacy;

import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public abstract class SpaCyBase extends DockerRestAnnotator {
	@Override
    protected String getDefaultDockerImage() {
    	return "texttechnologylab/textimager-spacy:3";
    }
	
	// Adds the "words" and "spaces" arrays for spaCy to the JSON object
	protected void jsonAddWordsAndSpaces(JCas aJCas, JSONObject json) {
		JSONArray jsonWords = new JSONArray();
		JSONArray jsonSpaces = new JSONArray();
		
		Token lastToken = null;
		for (Token token : JCasUtil.select(aJCas, Token.class)) {
			// Recreate spaCy Doc Text: Add "space" token if more than 1 space between words
			if (lastToken != null) {
				if (lastToken.getEnd() == token.getBegin()) {
					// No space
					jsonWords.put(token.getCoveredText());
					jsonSpaces.put(false);
				} else {
					int num = token.getBegin() - lastToken.getEnd();
					if (num > 1) {
						// Add space to last word
						jsonSpaces.put(true);
						// Add "space" token with num-1 spaces
						jsonWords.put(new String(new char[num-1]).replace("\0", " "));
						// ... followed by no space and the next word
						jsonSpaces.put(false);
						jsonWords.put(token.getCoveredText());
					} else {
						jsonWords.put(token.getCoveredText());
						jsonSpaces.put(true);
					}
				}
			} else {
				jsonWords.put(token.getCoveredText());
			}
			
			lastToken = token;
		}
		
		// Handle last token
		if (lastToken != null) {
			if (aJCas.getDocumentText().length() == lastToken.getEnd())	{
				jsonSpaces.put(false);
			} else {
				int num = aJCas.getDocumentText().length() - lastToken.getEnd();
				if (num > 1) {
					jsonSpaces.put(true);
					jsonWords.put(new String(new char[num-1]).replace("\0", " "));
					jsonSpaces.put(false);
				} else {
					jsonSpaces.put(true);
				}
			}
		}
		
		json.put("words", jsonWords);
		json.put("spaces", jsonSpaces);
	}
}
