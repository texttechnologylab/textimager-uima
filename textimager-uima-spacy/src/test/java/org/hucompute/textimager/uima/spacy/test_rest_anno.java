package org.hucompute.textimager.uima.spacy;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.json.JSONObject;

import java.io.IOException;

public class test_rest_anno extends RestAnnotator {
    @Override
    protected JSONObject buildJSON(JCas aJCas) {
        JSONObject json = new JSONObject();
        json.put("text", aJCas.getDocumentText());
        json.put("lang", aJCas.getDocumentLanguage());

        return json;
    }

    @Override
    protected void updateCAS(JCas test_cas, JSONObject jsonResult) {
        String text = (String) jsonResult.get("text");
        String lang = (String) jsonResult.get("lang");
        try {
            test_cas = JCasFactory.createJCas(text, lang);
            System.out.println(test_cas);
        } catch (UIMAException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws UIMAException, IOException {
        String bsp_text = "Dies ist ein Beispielsatz und er ist lang.";
        String lang = "de";
        JCas test_cas = JCasFactory.createText(bsp_text, lang);

        test_rest_anno x = new test_rest_anno();
        x.restEndpoint = "http://127.0.0.1:8000/data";
        String body = x.buildJSON(test_cas).toString();
        //System.out.println(x.restEndpoint + x.getRestRoute());
        String res = x.sendRequest(body);
        System.out.println(res);
        x.updateCAS(test_cas, new JSONObject(res));


    }
}
