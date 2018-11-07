package org.hucompute.textimager.uima.base;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.json.JSONObject;

public abstract class RestAnnotator extends JCasAnnotator_ImplBase {
	/**
	 * The endpoint of the rest server
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
	protected abstract void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException;

	protected String getRestEndpoint() {
		return restEndpoint + getRestRoute();
	}
	
	protected String sendRequest(String body) throws IOException {
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
		String res = IOUtils.toString(connection.getInputStream(), "UTF-8");
		writer.close();
		
		return res;
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			String body = buildJSON(aJCas).toString();
			//System.out.println(body);

			String res = sendRequest(body);
			//System.out.println(res);

			updateCAS(aJCas, new JSONObject(res));

		} catch (Exception ex) {
			throw new AnalysisEngineProcessException(ex);
		}
	}
}
