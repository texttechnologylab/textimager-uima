package org.hucompute.textimager.uima.NeuralnetworkNER;

import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
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
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
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
public abstract class NeuralNERBase extends DockerRestAnnotator {
	/**
	 * The model name
	 */
	public static final String PARAM_MODEL_NAME = "modelName";
	@ConfigurationParameter(name = PARAM_MODEL_NAME, mandatory = true, defaultValue = "conll2010-tuebadz")
	protected static String modelName;

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

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		JSONObject json = buildJSON(aJCas);
		json.put("model", modelName);
		String body = json.toString();

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

			writer.close();
			System.out.println(new JSONObject(res));
			updateCAS(aJCas, new JSONObject(res));

		} catch (Exception ex) {
			throw new AnalysisEngineProcessException(ex);
		}
	}

	@Override
	protected String getDefaultDockerImage() {
		return "texttechnologylab/textimager-neuralnetwork-ner:3";
	}
}
