package org.hucompute.textimager.uima.julie;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.julie.reader.JsonReader;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;

public abstract class JulieBase extends DockerRestAnnotator {
    /**
     * Docker image name.
     * @return name
     */
    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-julie";
    }

    /**
     * Docker image tag.
     * @return tag
     */
    @Override
    protected String getDefaultDockerImageTag() {
        return "1.3";
    }

    @Override
    protected int getDefaultDockerPort() {
        return 8080;
    }

    /**
     * Convert jCas to Json.
     * @return JSON
     */
    @Override
    protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
        try {
            JsonReader reader = new JsonReader();
            return reader.CasToJson(aJCas);
        }
        catch (IOException | SAXException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
    }
    /**
     * Read Json and update jCas.
     * @param aJCas
     */
    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
        try {
            JsonReader reader = new JsonReader();
            reader.UpdateJsonToCas(jsonResult, aJCas);
        }
        catch (UIMAException | IOException | SAXException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
    }

}
