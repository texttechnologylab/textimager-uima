package org.hucompute.textimager.uima.spacy;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public abstract class SpaCyBase extends JCasAnnotator_ImplBase {
	// Base URL, Port added during initialisation
	private String restEndpointBase = "127.0.0.1:";
	
	private Process dockerProcess = null;

	// Provide Rest Verb for URL
	protected abstract String getRestEndpointVerb();
	
	// Build request JSON object
	protected abstract JSONObject buildJSON(JCas aJCas);
	
	// Update CAS with JSON results
	protected abstract void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException;

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
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		String port = "5000";
		restEndpointBase += port;
		ProcessBuilder builder = new ProcessBuilder("docker", "run", "-p", port + ":80", "--rm", "texttechnologylab/textimager-spacy");
        try {
			dockerProcess = builder.start();
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	@Override
	public void destroy() {
		if (dockerProcess != null) {
			dockerProcess.destroy();
		}

		super.destroy();
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String body = buildJSON(aJCas).toString();
		System.out.println(body);
		
		try {
			URL url = new URL(getRestEndpoint());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(body.length()));
	
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(body);
			writer.flush();
			
			String res = IOUtils.toString(connection.getInputStream());
			System.out.println(res);
	
			writer.close();
			
			updateCAS(aJCas, new JSONObject(res));
			
		} catch (Exception ex) {
			throw new AnalysisEngineProcessException(ex);
		}
	}
	
	private String getRestEndpoint() {
		return restEndpointBase + "/" + getRestEndpointVerb();
	}
}
