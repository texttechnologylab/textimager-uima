package org.hucompute.textimager.uima.julie;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.julie.helper.Converter;
import org.hucompute.textimager.uima.julie.reader.JsonReader;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;

public class OpennlpToken extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/opennlpToken";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
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

            //Convert, create and remove
            Converter conv = new Converter();
            conv.ConvertCreateRemoveTokens(aJCas);

            //Clear input : Sentence
            conv.RemoveSentence(aJCas);
        }
        catch (IOException | SAXException | UIMAException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
    }
}
