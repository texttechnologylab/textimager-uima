package org.hucompute.textimager.uima.spacy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import jep.JepException;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test_rest_anno extends RestAnnotator {
    ArrayList<HashMap<String, Object>> tokens = new ArrayList<>();
    ArrayList<HashMap<String, Object>> sents = new ArrayList<>();
    ArrayList<HashMap<String, Object>> pos = new ArrayList<>();
    ArrayList<HashMap<String, Object>> deps = new ArrayList<>();
    ArrayList<HashMap<String, Object>> ents = new ArrayList<>();

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
            JSONArray jArray =  (JSONArray) jsonResult.get("tokens");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = (JSONObject) jArray.get(i);
                    HashMap<String, Object> hash = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    tokens.add(hash);
                }
            }
            jArray =  (JSONArray) jsonResult.get("sents");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = (JSONObject) jArray.get(i);
                    HashMap<String, Object> hash = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    sents.add(hash);
                }
            }
            /*
            System.out.println("we inside");
            System.out.println(jsonResult.get("tokens"));
            System.out.println(jsonResult.get("tokens").getClass().getName());
            JSONArray jArray =  (JSONArray) jsonResult.get("tokens");
            Field[] vars = getClass().getFields();
            for(Field var:vars) {
                if (var.toString() equals("")){

                }
                ArrayList<HashMap<String, Object>> list = new ArrayList<>();
                if (jArray != null) {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject json = (JSONObject) jArray.get(i);
                        HashMap<String, Object> hash = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
                        }.getType());
                        list.add(hash);
                        var.set(var, list);
                    }
                }
            }
            System.out.println("new output");
            System.out.println(tokens);

             */
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
    }


    private Map<Integer, Map<Integer, Token>> processToken(JCas aJCas, int beginOffset) {
        Map<Integer, Map<Integer, Token>> tokensMap = new HashMap<>();
        ArrayList<HashMap<String, Object>> output = tokens;
        for (HashMap<String, Object> token : output) {
            if (!(Boolean) token.get("is_space")) {
                int begin = ((Double) token.get("idx")).intValue() + beginOffset;
                int end = begin + ((Double) token.get("length")).intValue();
                Token casToken = new Token(aJCas, begin, end);
                casToken.addToIndexes();
                if (!tokensMap.containsKey(begin)) {
                    tokensMap.put(begin, new HashMap<>());
                }
                if (!tokensMap.get(begin).containsKey(end)) {
                    tokensMap.get(begin).put(end, casToken);
                }
            }
        }
        return tokensMap;
    }





    public static void main(String[] args) throws UIMAException, IOException {
        String bsp_text = "Dies ist ein Beispielsatz und er ist lang.";
        String lang = "de";
        JCas test_cas = JCasFactory.createJCas();
        test_cas.setDocumentLanguage(lang);
        test_cas.setDocumentText(bsp_text);

        test_rest_anno x = new test_rest_anno();
        x.restEndpoint = "http://127.0.0.1:8000/data";
        x.process(test_cas);


        //String body = x.buildJSON(test_cas).toString();
        //System.out.println(x.restEndpoint + x.getRestRoute());
        //String res = x.sendRequest(body);
        //x.updateCAS(test_cas, new JSONObject(res));
        System.out.println("New Output of process token:");
        System.out.println(x.processToken(test_cas, 0));


    }
}
