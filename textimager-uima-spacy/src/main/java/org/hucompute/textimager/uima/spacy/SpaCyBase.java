package org.hucompute.textimager.uima.spacy;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public abstract class SpaCyBase extends JCasAnnotator_ImplBase {
    /**
     * The docker image for the spacy server
     */
    public static final String PARAM_DOCKER_IMAGE = "dockerImage";
    @ConfigurationParameter(name = PARAM_DOCKER_IMAGE, mandatory = true, defaultValue = "texttechnologylab/textimager-spacy:3")
    protected String dockerImage;
    
    /**
     * The min port
     */
    public static final String PARAM_PORT_MIN = "portMin";
    @ConfigurationParameter(name = PARAM_PORT_MIN, mandatory = true, defaultValue = "5000")
    protected int portMin;
    
    /**
     * The max port
     */
    public static final String PARAM_PORT_MAX = "portMax";
    @ConfigurationParameter(name = PARAM_PORT_MAX, mandatory = true, defaultValue = "5100")
    protected int portMax;
	
	// Base URL, Port added during initialisation
	private String restEndpointIP = "127.0.0.1";
	private String restEndpointBase;
	
	private String dockerPidFile = null;

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

	private boolean isPortFree(String host, int port) {
		Socket s = null;
		try {
			s = new Socket(host, port);
			return false;
		} catch (Exception e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}
	
	private boolean isHTTPOK(String url) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(restEndpointBase).openConnection();
			con.setRequestMethod("HEAD");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("spacy server online");
				return true;
			}
		} catch (Exception e) {
			// ignore
		}
		return false;
	}
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		// wait so that instances do not interrupt
		try {
			Thread.sleep((long) (Math.random() * 1000));
		} catch (InterruptedException e1) {
			// ..
		}

		try {
			File dockerPidFileTemp = File.createTempFile("textimager_ducc_spacy_", "_docker_pid");
			dockerPidFile = dockerPidFileTemp.getAbsolutePath();
			// TODO better solution
			dockerPidFileTemp.delete();
			System.out.println("docker pid file: " + dockerPidFile);
		} catch (Exception ex) {
			throw new ResourceInitializationException(ex);
		}
		
		int portInt = portMin;
		while (!isPortFree(restEndpointIP, portInt)) {
			System.out.println("port " + portInt + " not available, checking next...");
			portInt++;
			if (portInt > portMax) {
				throw new ResourceInitializationException(new Exception("no free ports found"));
			}
		}
		String port = String.valueOf(portInt);
		System.out.println("using port " + port);
		
		restEndpointBase = "http://" + restEndpointIP + ":" + port;
		
		ProcessBuilder builder = new ProcessBuilder("docker", "run", "-d", "--cidfile", dockerPidFile, "--rm", "-p", port + ":80", dockerImage);
        try {
        	Process dockerProcess = builder.start();
			
			// wait until server is ready
			System.out.println("waiting for spacy server...");
			while(!isHTTPOK(restEndpointBase)) {
				System.out.println("still waiting...");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// ignore
				}
			}
			try {
				dockerProcess.waitFor();
			} catch (InterruptedException e) {
				dockerProcess.destroyForcibly();
			}
			int exitValue = dockerProcess.exitValue();
			System.out.println("exit value: " + exitValue);
			if (exitValue != 0) {
				throw new Exception("error starting docker container with spacy rest server.");
			}
		} catch (Exception e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	@Override
	public void destroy() {
		if (dockerPidFile != null) {
			File dockerPidFileTemp = new File(dockerPidFile);
			try {
				String dockerId = FileUtils.readFileToString(dockerPidFileTemp, "UTF-8");
				System.out.println("docker id: " + dockerId);
				new ProcessBuilder("docker", "stop", dockerId).start();
			} catch (IOException e) {
				// ..
			}
			dockerPidFileTemp.delete();
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
