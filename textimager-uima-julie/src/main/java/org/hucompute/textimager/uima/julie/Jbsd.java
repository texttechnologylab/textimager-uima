package org.hucompute.textimager.uima.julie;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.hucompute.textimager.uima.julie.reader.JsonReader;
import org.json.JSONObject;
import org.xml.sax.SAXException;
import java.io.*;


/**
 * Jbsd
 *
 * @date 04.06.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide connection between julie-api and give the sentence from JULIELab Tagger back as UIMA Sentence type
 *
 * Input: UIMA-JCas
 * Output: Textimager-UIMA-Sentiment*/

public class Jbsd extends DockerRestAnnotator {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/jbsd";
    }


    @Override
    protected String getDefaultDockerImage() {
        return "textimager-juli-api";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "1.0";
    }

    @Override
    protected int getDefaultDockerPort() {
        return 8080;
    }

    @Override
    public void initialize(UimaContext aContext) throws  ResourceInitializationException
    {
        super.initialize(aContext);
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
