import com.google.gson.Gson;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.admin.*;
import org.apache.uima.cas.impl.*;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.impl.*;
import org.apache.uima.json.JsonCasSerializer;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.CasIOUtils;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONML;
import org.json.JSONObject;
import org.json.XML;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;

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

        //Serialize
        ByteArrayOutputStream tmpStream = new ByteArrayOutputStream();
        XCASSerializer.serialize(aJCas.getCas(), tmpStream);

        String tmpPayload = tmpStream.toString();
        tmpStream.close();
        JSONObject payload = new JSONObject();
        payload = JSONML.toJSONObject(tmpPayload);


        //------------------------Das funktioniert
        /*JSONObject payload = new JSONObject();
        payload.put("text", aJCas.getDocumentText());
        payload.put("lang", aJCas.getDocumentLanguage());*/
        return payload;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws UIMAException, IOException, SAXException {


        //Mit XMISERializer
        /*
        String test1 = jsonResult.getString("msg");
        ByteArrayOutputStream er = new ByteArrayOutputStream();
        ByteArrayInputStream testStream = new ByteArrayInputStream(test1.getBytes());
        CasIOUtils.load(testStream, aJCas.getCas(), aJCas.getCas().getTypeSystem());*/


        JSONObject jsonObject = new JSONObject(jsonResult.toString());
        String tmpString = JSONML.toString(jsonResult);
        System.out.println(tmpString);
        ByteArrayInputStream tmpStream = new ByteArrayInputStream(tmpString.getBytes());

        //String xml = XML.toString(jsonResult);
        //ByteArrayInputStream imp = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        //InputStream in = org.apache.commons.io.IOUtils.toInputStream(tmpString, "UTF-8");
        //CasIOUtils.load(in, aJCas.getCas());

        XCASDeserializer.deserialize(tmpStream, aJCas.getCas());
        String test = "";
        //Das ist alt und funktioniert (ohne Ser/Des)
        /*if (!jsonResult.has("Sentences")) {
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

        }*/
        ;
    }

}
