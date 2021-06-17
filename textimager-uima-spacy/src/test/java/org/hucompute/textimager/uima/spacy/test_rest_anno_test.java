package org.hucompute.textimager.uima.spacy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class test_rest_anno_test extends RestAnnotator {
    @Override
    protected JSONObject buildJSON(JCas aJCas) {
        JSONObject json = new JSONObject();
        json.put("text", aJCas.getDocumentText());
        json.put("lang", aJCas.getDocumentLanguage());
        return json;
    }

    @Override
    protected void updateCAS(JCas test_cas, JSONObject jsonResult) {
        try{
            System.out.println("we inside");
            System.out.println(jsonResult.get("tokens"));
            System.out.println(jsonResult.get("tokens").getClass().getName());
            JSONArray jArray =  (JSONArray) jsonResult.get("tokens");
            ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            if (jArray != null) {
                for (int i=0;i<jArray.length();i++){
                    JSONObject json = (JSONObject) jArray.get(i);
                    HashMap<String, Object> hash = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
                    list.add(hash);
                }
            }
            System.out.println("new output");
            System.out.println(list);
            //ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
            /*
            JSONArray jsonArray = (JSONArray) jsonResult.get("tokens");
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    String tmp = jsonArray.get(i).toString();
                    tmp = tmp.replace("{", "<");
                    tmp = tmp.replace("}", ">");
                    HashMap<String, Object> hash = (HashMap<String, Object>) tmp_2;


                    list.add(jsonArray.get(i).toString());}}
                    */



        }
        catch (Exception e){
            e.printStackTrace();
        }

        String text = (String) test_cas.getDocumentText();
        String lang = (String) test_cas.getDocumentLanguage();


    }

    public static void main(String[] args) throws UIMAException, IOException {
        String bsp_text = "Dies ist ein Beispielsatz und er ist lang.";
        String lang = "de";
        JCas test_cas = JCasFactory.createText(bsp_text, lang);

        test_rest_anno_test x = new test_rest_anno_test();
        x.restEndpoint = "http://127.0.0.1:8000/data";
        String body = x.buildJSON(test_cas).toString();
        //System.out.println(x.restEndpoint + x.getRestRoute());
        String res = x.sendRequest(body);
        System.out.println(res);
        x.updateCAS(test_cas, new JSONObject(res));


    }
}
