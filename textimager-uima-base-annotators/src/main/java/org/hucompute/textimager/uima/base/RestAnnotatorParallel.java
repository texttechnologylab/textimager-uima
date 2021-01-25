package org.hucompute.textimager.uima.base;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class RestAnnotatorParallel extends JCasAnnotator_ImplBase {
	/**
	 * The endpoint of the rest server. If there are many restEndpoints, please seperate them with an ';'
	 */
	public static final String PARAM_REST_ENDPOINT = "restEndpoint";
	@ConfigurationParameter(name = PARAM_REST_ENDPOINT, mandatory = false)
	protected String restEndpoint;

	protected String getRestRoute() {
		return "";
	}

	// Build request JSON object
	protected abstract JSONObject buildJSON(JCas aJCas);

	// Update CAS with JSON results
	protected abstract void updateCAS(JCas aJCas, JSONArray jsonResults) throws AnalysisEngineProcessException;

	protected List<String> getRestEndpoints() {
		List<String> endpoints = new ArrayList<>();
		for (String endpoint : restEndpoint.split(";")) {
			endpoints.add(endpoint + getRestRoute());
		}
		return endpoints;
	}

	protected List<String> sendRequest(String body) throws IOException {
		List<String> results = getRestEndpoints()
				.stream()
				.map(splitEndpoint -> {
					String result = "";
					try {

						URL url = new URL(splitEndpoint);
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
						String res = IOUtils.toString(connection.getInputStream(), "UTF-8");
						writer.close();

						result = res;
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (ProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					return result;
				})
				.filter(r -> !r.isEmpty())
				.collect(Collectors.toList());

		return results;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			String body = buildJSON(aJCas).toString();
			//System.out.println(body);

			List<String> res = sendRequest(body);
			JSONArray resArray = new JSONArray();
			for (String r : res) {
				resArray.put(new JSONObject(r));
			}

			updateCAS(aJCas, resArray);

		} catch (Exception ex) {
			throw new AnalysisEngineProcessException(ex);
		}
	}
}
