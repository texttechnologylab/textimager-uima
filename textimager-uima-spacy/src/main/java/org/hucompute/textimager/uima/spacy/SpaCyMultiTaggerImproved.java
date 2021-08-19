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
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.lexmorph.pos.POSUtils;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpaCyMultiTaggerImproved extends DockerRestAnnotator {
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
    protected String getDefaultDockerImage() {
        return "textimager-uima-spacy-tagger";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "0.1";
    }

    @Override
    protected int getDefaultDockerPort() {
        return 8000;
    }

    @Override
    protected String getRestRoute() {
        return "/multi";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }

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
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
        List<Map<String, Object>> tokens = new ArrayList<>();
        List<Map<String, Object>> sents = new ArrayList<>();
        List<Map<String, Object>> pos = new ArrayList<>();
        List<Map<String, Object>> deps = new ArrayList<>();
        List<Map<String, Object>> ents = new ArrayList<>();

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

            // Sentences
            processSentences(aJCas, sents);

            // Tokenizer
            Map<Integer, Map<Integer, Token>> tokensMap = processToken(aJCas, tokens);

            // Tagger
            processPOS(aJCas, tokensMap, pos);

            // PARSER
            processDep(aJCas, tokensMap, deps);

            // NER
            processNER(aJCas, ents);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processSentences(JCas aJCas, List<Map<String, Object>> sents) {
        sents.forEach(p -> {
            int begin = ((Double) p.get("begin")).intValue();
            int end = ((Double) p.get("end")).intValue();
            Sentence sentAnno = new Sentence(aJCas, begin, end);
            sentAnno.addToIndexes();
        });
    }

    private Map<Integer, Map<Integer, Token>> processToken(JCas aJCas, List<Map<String, Object>> tokens) {
        Map<Integer, Map<Integer, Token>> tokensMap = new HashMap<>();
        for (Map<String, Object> token : tokens) {
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

    private void processPOS(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap, List<Map<String, Object>> pos) throws AnalysisEngineProcessException {
        mappingProvider.configure(aJCas.getCas());

        pos.forEach(p -> {
            if (!(Boolean) p.get("is_space")) {
                int begin = ((Double) p.get("idx")).intValue();
                int end = begin + ((Double) p.get("length")).intValue();
                String tagStr = p.get("tag").toString();

                Type posTag = mappingProvider.getTagType(tagStr);

                POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
                posAnno.setPosValue(tagStr);
                POSUtils.assignCoarseValue(posAnno);

                Token tokenAnno = tokensMap.get(begin).get(end);
                tokenAnno.setPos(posAnno);
                posAnno.addToIndexes();
            }
        });
    }

    private void processDep(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap, List<Map<String, Object>> deps) {
        deps.forEach(dep -> {
            if (!(Boolean) dep.get("is_space")) {
                String depStr = dep.get("dep").toString().toUpperCase();

                int begin = ((Double) dep.get("idx")).intValue();
                int end = begin + ((Double) dep.get("length")).intValue();

                @SuppressWarnings("unchecked")
                Map<String, Object> headToken = (Map<String, Object>) dep.get("head");
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

    private void processNER(JCas aJCas, List<Map<String, Object>> ents) {
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
        long textLength = aJCas.getDocumentText().length();
        System.out.println("text length: " + textLength);

        // abort on empty
        if (textLength < 1) {
            System.out.println("skipping spacy due to text length < 1");
            return;
        }

        super.process(aJCas);
    }
}
