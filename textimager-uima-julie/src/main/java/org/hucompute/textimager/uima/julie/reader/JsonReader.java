package org.hucompute.textimager.uima.julie.reader;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XCASDeserializer;
import org.apache.uima.cas.impl.XCASSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.json.JSONML;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * JsonReader
 *
 * @date 17.06.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provides de/serialization for jCAS and JsonObject
 *
 * Input: JSON/jCas
 * Output: JSON/jCas*/

public class JsonReader {
    public JsonReader(){};
    /**
     * Deserialize JSONObject to jCas.
     * @return jCas
     */
    public JCas JsonToCas(JSONObject jsonObject) throws UIMAException, IOException, SAXException {
        String tmpString = JSONML.toString(jsonObject);
        ByteArrayInputStream tmpStream = new ByteArrayInputStream(tmpString.getBytes());
        JCas jCas = JCasFactory.createJCas();
        XCASDeserializer.deserialize(tmpStream, jCas.getCas());
        return jCas;
    }
    /**
     * Update the jCas.
     * @param jCas
     */
    public void UpdateJsonToCas(JSONObject jsonObject, JCas jCas) throws UIMAException, IOException, SAXException {
        String tmpString = JSONML.toString(jsonObject);
        ByteArrayInputStream tmpStream = new ByteArrayInputStream(tmpString.getBytes());
        XCASDeserializer.deserialize(tmpStream, jCas.getCas());
    }
    /**
     * Serialize jCas to JSONObject.
     * @return JSONObject
     */
    public JSONObject CasToJson(JCas jCas) throws IOException, SAXException {
        ByteArrayOutputStream tmpOutStream = new ByteArrayOutputStream();
        XCASSerializer.serialize(jCas.getCas(), tmpOutStream);
        String tmpPayload = tmpOutStream.toString();
        tmpOutStream.close();
        JSONObject payload = new JSONObject();
        payload = JSONML.toJSONObject(tmpPayload);
        return payload;
    }
}
