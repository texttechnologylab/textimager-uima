package org.hucompute.textimager.uima.spacy;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.MetaDataStringField;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
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

public class SpaCyMultiTagger3 extends DockerRestAnnotator {
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
        return "textimager-uima-spacy3-tagger";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "0.6";
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
        return "0.0.2";
    }

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        // TODO defaults for de (stts) and en (ptb) are ok, add own language mapping later
        mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext, posMappingLocation, variant, language);
    }

    /**
     * The unique key to identify the annotation of the dynamic batch size inside of the jCas Object
     */
    static public String DYNAMIC_CONFIGURATION_MODEL_NAME_KEY = "spacy.dynamic_configuration.model_name";

    /**
     * Sets the batch size as annotation inside of the jCAS
     * @param aJCAS The jCAS Object to annotate
     * @param model_name The chosen model
     */
    public static void set_model_name(JCas aJCAS, String model_name) {
        MetaDataStringField field = new MetaDataStringField(aJCAS);
        field.setValue(model_name);
        field.setKey(DYNAMIC_CONFIGURATION_MODEL_NAME_KEY);
        field.addToIndexes();
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) {
        JSONObject json = new JSONObject();
        json.put("text", aJCas.getDocumentText());
        json.put("lang", aJCas.getDocumentLanguage());

        String modelName = "";
        for (MetaDataStringField i : JCasUtil.select(aJCas, MetaDataStringField.class)) {
            if (i.getKey().equals(DYNAMIC_CONFIGURATION_MODEL_NAME_KEY)) {
                modelName = i.getValue();
            }
        }
        json.put("model_name", modelName);
        return json;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
        JSONArray tokens = jsonResult.getJSONObject("multitag").getJSONArray("tokens");
        JSONArray pos = jsonResult.getJSONObject("multitag").getJSONArray("pos");
        JSONArray deps = jsonResult.getJSONObject("multitag").getJSONArray("deps");
        JSONArray ents = jsonResult.getJSONObject("multitag").getJSONArray("ents");
        JSONArray morphs = jsonResult.getJSONObject("multitag").getJSONArray("morphs");
        JSONArray lemmas = jsonResult.getJSONObject("multitag").getJSONArray("lemmas");
        JSONArray sents = jsonResult.getJSONObject("multitag").getJSONArray("sents");

        try{
            // Sentences
            processSentences(aJCas, sents);

            // Tokenizer
            Map<Integer, Map<Integer, Token>> tokensMap = processToken(aJCas, tokens);

            // Tagger
            processPOS(aJCas, tokensMap, pos);

            // Lemma
            processLemma(aJCas, tokensMap, lemmas);

            // Morph
            processMorph(aJCas, tokensMap, morphs);

            // PARSER
            processDep(aJCas, tokensMap, deps);

            // NER
            processNER(aJCas, ents);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processLemma(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap, JSONArray lemmas) {
        lemmas.forEach(l -> {
            JSONObject lemma = (JSONObject) l;
            if (!lemma.getBoolean("is_space")) {
                int begin = lemma.getInt("idx");
                int end = begin + lemma.getInt("length");
                String text = lemma.getString("lemma_text");

                Lemma lemmaAnno = new Lemma(aJCas, begin, end);
                lemmaAnno.setValue(text);

                Token tokenAnno = tokensMap.get(begin).get(end);
                tokenAnno.setLemma(lemmaAnno);
                lemmaAnno.addToIndexes();

                addAnnotatorComment(aJCas, lemmaAnno);
            }
        });
    }

    private void processMorph(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap, JSONArray morphs) {
        morphs.forEach(m -> {
            JSONObject morph = (JSONObject) m;
            if (!morph.getBoolean("is_space")) {
                int begin = morph.getInt("idx");
                int end = begin + morph.getInt("length");

                JSONArray ms = morph.getJSONArray("morph");

                // Clean string: remove first and last " char
                List<String> msClean = new ArrayList<>();
                ms.forEach(mm -> {
                    String mVal = (String) mm;
                    msClean.add(mVal);
                });

                MorphologicalFeatures morphAnno = new MorphologicalFeatures(aJCas, begin, end);
                if (!msClean.isEmpty()) {
                    morphAnno.setValue(String.join("|", msClean));

                    for (String mVal : msClean) {
                        String[] fields = mVal.split("=", 2);
                        if (fields.length == 2) {
                            String key = fields[0].trim();
                            String value = fields[1].trim();

                            // TODO: only checked for DE
                            switch (key) {
                                case "Gender":
                                    morphAnno.setGender(value);
                                    break;
                                case "Number":
                                    morphAnno.setNumber(value);
                                    break;
                                case "Case":
                                    morphAnno.setCase(value);
                                    break;
                                case "Degree":
                                    morphAnno.setDegree(value);
                                    break;
                                case "VerbForm":
                                    morphAnno.setVerbForm(value);
                                    break;
                                case "Tense":
                                    morphAnno.setTense(value);
                                    break;
                                case "Mood":
                                    morphAnno.setMood(value);
                                    break;
                                case "Voice": // ?
                                    morphAnno.setVoice(value);
                                    break;
                                case "Definite":
                                    morphAnno.setDefiniteness(value);
                                    break;
                                case "Person":
                                    morphAnno.setPerson(value);
                                    break;
                                case "Aspect": // ?
                                    morphAnno.setAspect(value);
                                    break;
                                case "Animacy": // ?
                                    morphAnno.setAnimacy(value);
                                    break;
                                case "Negative": // ?
                                    morphAnno.setNegative(value);
                                    break;
                                case "NumType": // ?
                                    morphAnno.setNumType(value);
                                    break;
                                case "Possessive": // ?
                                    morphAnno.setPossessive(value);
                                    break;
                                case "PronType":
                                    morphAnno.setPronType(value);
                                    break;
                                case "Reflex":
                                    morphAnno.setReflex(value);
                                    break;
                                case "Transitivity": // ?
                                    morphAnno.setTransitivity(value);
                                    break;
                            }
                        }
                    }

                    Token tokenAnno = tokensMap.get(begin).get(end);
                    tokenAnno.setMorph(morphAnno);
                    morphAnno.addToIndexes();

                    addAnnotatorComment(aJCas, morphAnno);
                }
            }
        });
    }

    private void processSentences(JCas aJCas, JSONArray sents) {
        sents.forEach(s -> {
            JSONObject sentence = (JSONObject) s;

            int begin = sentence.getInt("begin");
            int end = sentence.getInt("end");

            Sentence sentAnno = new Sentence(aJCas, begin, end);
            sentAnno.addToIndexes();

            addAnnotatorComment(aJCas, sentAnno);
        });
    }

    private Map<Integer, Map<Integer, Token>> processToken(JCas aJCas, JSONArray tokens) {
        Map<Integer, Map<Integer, Token>> tokensMap = new HashMap<>();
        for (Object t : tokens) {
            JSONObject token = (JSONObject) t;
            if (!token.getBoolean("is_space")) {
                int begin = token.getInt("idx");
                int end = begin + token.getInt("length");

                Token casToken = new Token(aJCas, begin, end);
                casToken.addToIndexes();

                addAnnotatorComment(aJCas, casToken);

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

    private void processPOS(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap, JSONArray poss) throws AnalysisEngineProcessException {
        mappingProvider.configure(aJCas.getCas());

        poss.forEach(p -> {
            JSONObject pos = (JSONObject) p;
            if (!pos.getBoolean("is_space")) {
                int begin = pos.getInt("idx");
                int end = begin + pos.getInt("length");
                String tagStr = pos.getString("tag");

                Type posTag = mappingProvider.getTagType(tagStr);

                POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
                posAnno.setPosValue(tagStr);
                POSUtils.assignCoarseValue(posAnno);

                Token tokenAnno = tokensMap.get(begin).get(end);
                tokenAnno.setPos(posAnno);
                posAnno.addToIndexes();

                addAnnotatorComment(aJCas, posAnno);
            }
        });
    }

    private void processDep(JCas aJCas, Map<Integer, Map<Integer, Token>> tokensMap, JSONArray deps) {
        deps.forEach(d -> {
            JSONObject dep = (JSONObject) d;
            if (!dep.getBoolean("is_space")) {
                String depStr = dep.getString("dep").toUpperCase();

                int begin = dep.getInt("idx");
                int end = begin + dep.getInt("length");

                JSONObject headToken = dep.getJSONObject("head");
                if (!headToken.getBoolean("is_space")) {
                    int beginHead = headToken.getInt("idx");
                    int endHead = beginHead + headToken.getInt("length");

                    // Note: if no token is found this may be due to empty lines
                    try {
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

                        addAnnotatorComment(aJCas, depAnno);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    private void processNER(JCas aJCas, JSONArray ents) {
        ents.forEach(e -> {
            JSONObject ent = (JSONObject) e;

            int begin = ent.getInt("start_char");
            int end = ent.getInt("end_char");
            String labelStr = ent.getString("label");

            NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
            neAnno.setValue(labelStr);
            neAnno.addToIndexes();

            addAnnotatorComment(aJCas, neAnno);
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
