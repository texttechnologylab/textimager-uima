import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.*;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.impl.*;
import org.apache.uima.json.JsonCasSerializer;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;

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

public class Jbsd extends  RestAnnotator {

    @Override
    protected String getRestRoute() {
        return "/jbsd";
    }


    @Override
    public void initialize(UimaContext aContext) throws  ResourceInitializationException
    {
        super.initialize(aContext);
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) throws IOException, SAXException {
        //Try serialise jcas
        //JsonCasSerializer serializer = new JsonCasSerializer();

        //XCASSerializer serializer = new XCASSerializer();
        //TODO Create the jsonArray
        ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
        //serializer.serialize(aJCas.getCas(), sw);
        XCASSerializer.serialize(aJCas.getCas(), tmpStream);
        String tmpPayload = tmpStream.toString();
        tmpStream.close();
        JSONObject payload = new JSONObject();
        payload = JSONML.toJSONObject(tmpPayload);
        //Das funktioniert
        /*JSONObject payload = new JSONObject();
        payload.put("text", aJCas.getDocumentText());
        payload.put("lang", aJCas.getDocumentLanguage());*/
        return payload;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {

        if (!jsonResult.has("Sentences")) {
            System.out.println("no sentences in response?!");
            System.out.println(jsonResult.toString());
            return;
        }
        //TODO convet to uima
        JSONArray jsonSentences = jsonResult.getJSONArray("Sentences");

        for (Object object : jsonSentences) {

            JSONObject jsObj = (JSONObject)object;

            Sentiment sentiment = new Sentiment(aJCas,(Integer)jsObj.get("begin"),(Integer)jsObj.get("end"));

            sentiment.addToIndexes();

        }
        ;
    }

}
