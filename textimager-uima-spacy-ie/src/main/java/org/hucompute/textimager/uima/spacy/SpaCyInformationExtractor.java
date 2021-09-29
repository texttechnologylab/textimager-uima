package org.hucompute.textimager.uima.spacy;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemArg;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemArgLink;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.SemPred;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import de.unihd.dbs.uima.annotator.heideltime2.HeidelTime;
import de.unihd.dbs.uima.types.heideltime.Timex3;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.lexmorph.pos.POSUtils;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.GeoNamesEntity;
import org.texttechnologylab.annotation.semaf.isobase.Entity;
import org.texttechnologylab.annotation.semaf.semafsr.SrLink;
import org.texttechnologylab.annotation.semaf.semafsr.SrLink_Type;

import java.util.*;

public class SpaCyInformationExtractor extends DockerRestAnnotator {
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
        return "textimager-uima-spacy3-ie";
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
//        Iterator<String> keys = jsonResult.getJSONObject("multitag").keys();
//        System.out.println("");
//        while(keys.hasNext()) {
//            String key = keys.next();
//            System.out.println(key);
//        }
//        System.out.println("");
        JSONArray tokens = jsonResult.getJSONObject("multitag").getJSONArray("tokens");
        JSONArray pos = jsonResult.getJSONObject("multitag").getJSONArray("pos");
        JSONArray deps = jsonResult.getJSONObject("multitag").getJSONArray("deps");
        JSONArray ents = jsonResult.getJSONObject("multitag").getJSONArray("ents");
        JSONArray morphs = jsonResult.getJSONObject("multitag").getJSONArray("morphs");
        JSONArray lemmas = jsonResult.getJSONObject("multitag").getJSONArray("lemmas");
        JSONArray sents = jsonResult.getJSONObject("multitag").getJSONArray("sents");
        JSONArray psrs = jsonResult.getJSONObject("multitag").getJSONArray("psrs");

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

            // (pseudo) semantic roles
            processPSR(aJCas, psrs);
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
                }
            }
        });
    }

    private void processPSR(JCas aJCas, JSONArray psrs) {
        System.out.println("PSRS");
        System.out.println("*************************************");
        psrs.forEach(s -> {
            JSONObject sr = (JSONObject) s;
            // PREDICATE
            JSONObject pred = sr.getJSONObject("PRED");
            int begin_p = pred.getInt("start_char");
            int end_p = pred.getInt("end_char");
            int begin_s = pred.getInt("sentence_begin");
            int end_s = pred.getInt("sentence_end");
            Entity predAnno = new Entity(aJCas, begin_p, end_p);
            String comment = "";
            try {
                comment = pred.getString("comment");
                predAnno.setComment(comment);
            }
            catch (org.json.JSONException exception){
//                System.out.println("No comments");
            }

            predAnno.addToIndexes();

            Iterator<String> keys = sr.keys();

            while(keys.hasNext()){
                String key = keys.next();
                if (!key.equals("PRED")){
                    if (sr.get(key) instanceof JSONObject) {
                        JSONObject sr_arg = (JSONObject) sr.get(key);
                        int begin = sr_arg.getInt("start_char");
                        int end = sr_arg.getInt("end_char");
                        Entity argAnno = new Entity(aJCas, begin, end);
                        try {
                            comment = sr_arg.getString("comment");
                            argAnno.setComment(comment);
                        }
                        catch (org.json.JSONException exception){
//                            System.out.println("No comments");
                        }
                        SrLink srlinkl = new SrLink(aJCas);
                        srlinkl.setRel_type(key);
                        srlinkl.setGround(argAnno);
                        srlinkl.setFigure(predAnno);
                        argAnno.addToIndexes();
                        srlinkl.addToIndexes();
                        JCasUtil.selectCovered(aJCas, GeoNamesEntity.class, begin, end).forEach(a -> {
                            System.out.println("covered GeoNamesEntity" + a.getCoveredText() + " " +
                            a.getBegin() + " " + a.getEnd());
                            SrLink srlinklGeo = new SrLink(aJCas);
                            srlinklGeo.setRel_type("LOC");
                            srlinklGeo.setGround(argAnno);
                            srlinklGeo.setFigure(predAnno);
                            srlinklGeo.addToIndexes();
                        });
                        JCasUtil.selectCovered(aJCas, Timex3.class, begin, end).forEach(a -> {
                            System.out.println("covered GeoNamesEntity" + a.getCoveredText() + " " +
                                    a.getBegin() + " " + a.getEnd());
                            SrLink srlinklTime = new SrLink(aJCas);
                            srlinklTime.setRel_type("TIME");
                            srlinklTime.setGround(argAnno);
                            srlinklTime.setFigure(predAnno);
                            srlinklTime.addToIndexes();
                        });
                    }

                }
            }
            JSONArray mos = sr.getJSONArray("mos");
            System.out.println(mos);
//            Sentence s1 = getRequiredCasInterface().getAnnotationsByType(Sentence.class);
//            List<Sentence> s1 = JCasUtil.selectAt(aJCas, Sentence.class, 0, 249);
//            List<Sentence> s1 = JCasUtil.selectAt(aJCas, Sentence.class, 0, 249);
//            System.out.println(s1);
            System.out.println("************HeidelTime*************************");

            List<Timex3> heidelTimes = JCasUtil.selectCovered(aJCas, Timex3.class, begin_s, end_s);
            System.out.println(heidelTimes);

            JCasUtil.selectCovered(aJCas, Timex3.class, begin_s, end_s).forEach(a ->{
                System.out.println(a.getCoveredText());
                System.out.println(a.getBegin() + " " + a.getEnd());
                Integer beginA = (Integer) a.getBegin();
                Integer endA = (Integer)  a.getEnd();
                mos.forEach(m -> {
                    JSONArray mA = (JSONArray) m;
                    Integer beginM = (Integer) mA.get(1);
                    Integer endM = (Integer)  mA.get(2);
                    if ((beginA == beginM) && (endA == endM)) {
                        System.out.println("    " + beginA + " " + endA);
                        Entity argAnno = new Entity(aJCas, beginA, endA);
                        SrLink srlinkl = new SrLink(aJCas);
                        srlinkl.setRel_type("TIME");
                        srlinkl.setGround(argAnno);
                        srlinkl.setFigure(predAnno);
                        argAnno.addToIndexes();
                        srlinkl.addToIndexes();
                    }
                });
            });
            System.out.println("**************GeoNames***********************");
//            List<GeoNamesEntity> geoNames = JCasUtil.selectAt(aJCas, GeoNamesEntity.class, 34, 41);
            List<GeoNamesEntity> geoNames = JCasUtil.selectCovered(aJCas, GeoNamesEntity.class, begin_s, end_s);
            System.out.println(geoNames);
            JCasUtil.selectCovered(aJCas, GeoNamesEntity.class, begin_s, end_s).forEach(a ->{
                System.out.println(a.getCoveredText());
                System.out.println(a.getBegin() + " " + a.getEnd());
                Integer beginA = (Integer) a.getBegin();
                Integer endA = (Integer)  a.getEnd();
                mos.forEach(m -> {
                    JSONArray mA = (JSONArray) m;
                    Integer beginM = (Integer) mA.get(1);
                    Integer endM = (Integer)  mA.get(2);
                    if ((beginA == beginM) && (endA == endM)) {
                        System.out.println("    " + beginA + " " + endA);
                        Entity argAnno = new Entity(aJCas, beginA, endA);
                        SrLink srlinkl = new SrLink(aJCas);
                        srlinkl.setRel_type("LOC");
                        srlinkl.setGround(argAnno);
                        srlinkl.setFigure(predAnno);
                        argAnno.addToIndexes();
                        srlinkl.addToIndexes();
                    }

                });
            });
            System.out.println("**************HeidelTime/GeoNames END***********************");

        });
    }
    private void processSentences(JCas aJCas, JSONArray sents) {
        sents.forEach(s -> {
            JSONObject sentence = (JSONObject) s;
            int begin = sentence.getInt("begin");
            int end = sentence.getInt("end");
            Sentence sentAnno = new Sentence(aJCas, begin, end);
            sentAnno.addToIndexes();
        });
    }

    private Map<Integer, Map<Integer, Token>> processToken(JCas aJCas, JSONArray tokens) {
        JCasUtil.select(aJCas, Annotation.class).stream().forEach(a->{
//                if (a.getType().getName().equals("de.unihd.dbs.uima.types.heideltime.Timex3")){
            if (a.getType().getName().equals("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token")){
                System.out.println("    " + a.getType().getName() + " " + a.getBegin() + " " + a.getEnd());
                aJCas.removeFsFromIndexes(a);
            }
        });
        Map<Integer, Map<Integer, Token>> tokensMap = new HashMap<>();
        for (Object t : tokens) {
            JSONObject token = (JSONObject) t;
            if (!token.getBoolean("is_space")) {
                int begin = token.getInt("idx");
                int end = begin + token.getInt("length");
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

                // TODO
                JSONObject headToken = dep.getJSONObject("head");
                int beginHead = headToken.getInt("idx");
                int endHead = beginHead + headToken.getInt("length");

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

    private void processNER(JCas aJCas, JSONArray ents) {
        ents.forEach(e -> {
            JSONObject ent = (JSONObject) e;
            int begin = ent.getInt("start_char");
            int end = ent.getInt("end_char");
            String labelStr = ent.getString("label");
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
