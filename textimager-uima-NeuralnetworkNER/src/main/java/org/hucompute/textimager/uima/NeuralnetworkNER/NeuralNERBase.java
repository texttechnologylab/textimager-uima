package org.hucompute.textimager.uima.NeuralnetworkNER;

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

/**
 * Neuralnetwork NER base class. Use the analysis engine description parameter PARAM_MODEL_NAME to choose from the four
 * possible models.
 *
 * See 'models' in neuralnetwork_ner_rest.py in the python implementation for more details.
 *
 * @author Manuel Stoeckel
 */
public abstract class NeuralNERBase extends JCasAnnotator_ImplBase {
	/**
	 * The docker image for the neuralnetwork-ner server
	 */
	public static final String PARAM_DOCKER_IMAGE = "dockerImage";
	@ConfigurationParameter(name = PARAM_DOCKER_IMAGE, mandatory = true, defaultValue = "texttechnologylab/textimager-neuralnetwork-ner:3")
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

	/**
	 * The model name
	 */
	public static final String PARAM_MODEL_NAME = "modelName";
	@ConfigurationParameter(name = PARAM_MODEL_NAME, mandatory = true, defaultValue = "conll2010-tuebadz")
	protected static String modelName;

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

	// Adds the "words" and "spaces" arrays for neuralnetwork-ner to the JSON object
	protected void jsonAddWordsAndCharIDs(JCas aJCas, JSONObject json) {
		JSONArray jsonWords = new JSONArray();
		JSONArray jsonBeginEnd = new JSONArray();

		Token lastToken = null;
		for (Token token : JCasUtil.select(aJCas, Token.class)) {
			if (lastToken != null) {
					jsonWords.put(token.getCoveredText());
					jsonBeginEnd.put(new JSONArray(new int[]{token.getBegin(), token.getEnd()}));
			} else {
				// TODO null token case
			}

			lastToken = token;
		}

		json.put("words", jsonWords);
		json.put("begin_end", jsonBeginEnd);
	}

	private boolean isPortFree(String host, int port) {
		try (Socket s = new Socket(host, port)) {
			return false;
		} catch (Exception e) {
			return true;
		}
		// ignore
	}

	private boolean isHTTPOK(String url) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(restEndpointBase).openConnection();
			con.setRequestMethod("HEAD");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("neuralnetwork-ner server online");
				return true;
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
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
			File dockerPidFileTemp = File.createTempFile("textimager_ducc_neuralnetwork-ner_", "_docker_pid");
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
			System.out.println("waiting for neuralnetwork-ner server...");
			while (!isHTTPOK(restEndpointBase)) {
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
				throw new Exception("error starting docker container with neuralnetwork-ner rest server.");
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
		JSONObject json = buildJSON(aJCas);
		json.put("model", modelName);
		String body = json.toString();
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
