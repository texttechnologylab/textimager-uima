package org.hucompute.textimager.uima.spacy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import jep.JepException;
import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.lexmorph.pos.POSUtils;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test_rest_anno extends RestAnnotator {
    ArrayList<HashMap<String, Object>> tokens = new ArrayList<>();
    ArrayList<HashMap<String, Object>> sents = new ArrayList<>();
    ArrayList<HashMap<String, Object>> pos = new ArrayList<>();
    ArrayList<HashMap<String, Object>> deps = new ArrayList<>();
    ArrayList<HashMap<String, Object>> ents = new ArrayList<>();

    public static final String PARAM_VARIANT = "variant";
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;

    public static final String PARAM_LANGUAGE = "language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    public static final String PARAM_POS_MAPPING_LOCATION = "posMappingLocation";
    @ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
    protected String posMappingLocation;

    private MappingProvider mappingProvider;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        // TODO defaults for de (stts) and en (ptb) are ok, add own language mapping later
        mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext, posMappingLocation, variant, language);

    }


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

            jArray =  (JSONArray) jsonResult.get("pos");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = (JSONObject) jArray.get(i);
                    HashMap<String, Object> hash = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    pos.add(hash);
                }
            }

            jArray =  (JSONArray) jsonResult.get("ents");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = (JSONObject) jArray.get(i);
                    HashMap<String, Object> hash = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    ents.add(hash);
                }
            }

            jArray =  (JSONArray) jsonResult.get("deps");
            if (jArray != null) {
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject json = (JSONObject) jArray.get(i);
                    HashMap<String, Object> hash = new Gson().fromJson(json.toString(), new TypeToken<HashMap<String, Object>>() {
                    }.getType());
                    deps.add(hash);
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

    private void processSentences(JCas aJCas) throws JepException {
        sents.forEach(p -> {
            int begin = ((Double) p.get("begin")).intValue();
            int end = ((Double) p.get("end")).intValue();
            Sentence sentAnno = new Sentence(aJCas, begin, end);
            sentAnno.addToIndexes();
        });
    }

    private Map<Integer, Map<Integer, Token>> processToken(JCas aJCas) {

        Map<Integer, Map<Integer, Token>> tokensMap = new HashMap<>();
        ArrayList<HashMap<String, Object>> output = tokens;
        for (HashMap<String, Object> token : output) {
            if (!(Boolean) token.get("is_space")) {
                int begin = ((Double) token.get("idx")).intValue();
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

    private void processPOS(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap) throws AnalysisEngineProcessException {


        mappingProvider.configure(aJCas.getCas());

        System.out.println(pos);

        pos.forEach(p -> {
            if (!(Boolean) p.get("is_space")) {
                int begin = ((Double) p.get("idx")).intValue();
                int end = begin + ((Double) p.get("length")).intValue();
                String tagStr = p.get("tag").toString();

                Type posTag = mappingProvider.getTagType(tagStr);

                //System.out.println("Erfolg");

                //System.out.println(posTag);

                POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
                posAnno.setPosValue(tagStr);
                POSUtils.assignCoarseValue(posAnno);

                Token tokenAnno = tokensMap.get(begin).get(end);
                tokenAnno.setPos(posAnno);
                posAnno.addToIndexes();

            }
        });
    }

    private void processDep(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap) throws JepException {
        deps.forEach(dep -> {
            if (!(Boolean) dep.get("is_space")) {
                String depStr = dep.get("dep").toString().toUpperCase();

                int begin = ((Double) dep.get("idx")).intValue();
                int end = begin + ((Double) dep.get("length")).intValue();

                @SuppressWarnings("unchecked")
                HashMap<String, Object> headToken = (HashMap<String, Object>) dep.get("head");
                int beginHead = ((Double) headToken.get("idx")).intValue();
                int endHead = beginHead + ((Double) headToken.get("length")).intValue();

                Token dependent = tokensMap.get(begin).get(end);
                Token governor = tokensMap.get(beginHead).get(endHead);

                Dependency depAnno;
                if (depStr.equals("ROOT")) {
                    depAnno = new ROOT(aJCas, begin, end);
                    depAnno.setDependencyType("--");
                } else {
                    depAnno = new Dependency(aJCas, begin, end);
                    depAnno.setDependencyType(depStr);
                }
                depAnno.setDependent(dependent);
                depAnno.setGovernor(governor);
                depAnno.setFlavor(DependencyFlavor.BASIC);
                depAnno.addToIndexes();
            }
        });
    }

    private void processNER(JCas aJCas) throws JepException {
        ents.forEach(p -> {
            int begin = ((Double) p.get("start_char")).intValue();
            int end = ((Double) p.get("end_char")).intValue();
            String labelStr = p.get("label").toString();
            NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
            neAnno.setValue(labelStr);
            neAnno.addToIndexes();
        });
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

        //process(aJCas);
        long textLength = aJCas.getDocumentText().length();
        System.out.println("text length: " + textLength);
        // abort on empty
        if (textLength < 1) {
            System.out.println("skipping spacy due to text length < 1");
            return;
        }

        restEndpoint = "http://127.0.0.1:8000/data";

        try {
            String body = buildJSON(aJCas).toString();
            //System.out.println(body);

            String res = sendRequest(body);
            //System.out.println(res);

            updateCAS(aJCas, new JSONObject(res));


        } catch (Exception ex) {
            throw new AnalysisEngineProcessException(ex);
        }

        System.out.println(tokens);

        try {
            /*
            List<String> texts = new ArrayList<>();
            if (textLength > spacyMaxLength) {
                int textLimit = spacyMaxLength / 2;
                System.out.println("Text limit is: " + textLimit);
                // split text on "." near "nlp.max_length (= " characters
                StringBuilder sb = new StringBuilder();
                String[] textParts = aJCas.getDocumentText().split("\\.", 0);
                for (String textPart : textParts) {
                    if (sb.length() >= textLimit) {
                        texts.add(sb.toString());
                        sb.setLength(0);
                    }
                    sb.append(textPart).append(".");
                }
                // handle rest
                if (sb.length() > 0) {
                    if(!aJCas.getDocumentText().endsWith("."))
                        sb.setLength(sb.length()-1);
                    texts.add(sb.toString());
                }
            }
            else {
                texts.add(aJCas.getDocumentText());
            }
            */
            // Sentences
            processSentences(aJCas);

            // Tokenizer
            Map<Integer, Map<Integer, Token>> tokensMap = processToken(aJCas);

            // Tagger
            processPOS(aJCas, tokensMap);

            // PARSER
            //processDep(aJCas, tokensMap);

            // NER
            processNER(aJCas);

        } catch (JepException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

/*
    public static void main(String[] args) throws UIMAException, IOException {

        String bsp_text = "Dies ist ein Beispielsatz und er ist lang.";
        String lang = "de";
        JCas test_cas = JCasFactory.createJCas();
        test_cas.setDocumentLanguage(lang);
        test_cas.setDocumentText(bsp_text);

        test_rest_anno x = new test_rest_anno();
        x.restEndpoint = "http://127.0.0.1:8000/data";
        x.process_data(test_cas);





        //String body = x.buildJSON(test_cas).toString();
        //System.out.println(x.restEndpoint + x.getRestRoute());
        //String res = x.sendRequest(body);
        //x.updateCAS(test_cas, new JSONObject(res));
        //System.out.println("New Output of process token:");
        //System.out.println(x.processToken(test_cas, 0));


    }*/

}
