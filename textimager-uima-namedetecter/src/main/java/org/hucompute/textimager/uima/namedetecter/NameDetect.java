package org.hucompute.textimager.uima.namedetecter;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.Location;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.Person;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.Organization;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

import java.lang.annotation.Annotation;
import java.util.Iterator;

public class NameDetect extends DockerRestAnnotator {

    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-name_detect";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "0.3.2";
    }

    @Override
    protected int getDefaultDockerPort() {
        return 8000;
    }

    @Override
    protected String getRestRoute() {
        return "/tagnames";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
        JSONObject payload = new JSONObject();
        JSONArray tokens = new JSONArray();
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
                JSONObject tokenObj = new JSONObject();
                tokenObj.put("begin", token.getBegin());
                tokenObj.put("end", token.getEnd());
                tokenObj.put("text", token.getCoveredText());
                tokens.put(tokenObj);
        }
        if (tokens.isEmpty()){
            throw new AnalysisEngineProcessException(new Exception("No tokens found. Tokenize your document"));
        }
        payload.put("tokens", tokens);
        String lang = aJCas.getDocumentLanguage();
        if (lang.equals("x-unspecified")) lang = "en";
        payload.put("lang", lang);
        payload.put("label_wikidata", false);

        return payload;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
        if (jsonResult.has("language_in"))
        {
            boolean result = jsonResult.getBoolean("language_in");
            if (!result){
                String lang = jsonResult.getString("lang");
                throw new AnalysisEngineProcessException(new Exception(String.format("Your language %s is not supported", lang)));
            }
        }
        if (jsonResult.has("tokens")) {
            JSONObject tokens = jsonResult.getJSONObject("tokens");
            for (Iterator<String> it = tokens.keys(); it.hasNext(); ) {
                String word = it.next();
                JSONObject token = (JSONObject) tokens.getJSONObject(word);
                int begin = token.getInt("begin");
                int end = token.getInt("end");
                boolean typo = token.getBoolean("typonym");
                boolean proper = token.getBoolean("proper");
                boolean person = token.getBoolean("person");
                boolean organization = token.getBoolean("organization");
                if (typo){
                    Location loc = new Location(aJCas, begin, end);
                    loc.setValue("LOC");
                    loc.addToIndexes();
                    addAnnotatorComment(aJCas, loc);
                    if (proper){
                        AnnotationComment modelAnno = new AnnotationComment(aJCas);
                        modelAnno.setReference(loc);
                        modelAnno.setKey("propername");
                        modelAnno.setValue("1");
                        addAnnotatorComment(aJCas, modelAnno);
                    }
                }
                if (organization){
                    Organization organ_new = new Organization(aJCas, begin, end);
                    organ_new.setValue("ORG");
                    organ_new.addToIndexes();
                    addAnnotatorComment(aJCas, organ_new);
                    if (proper){
                        AnnotationComment modelAnno = new AnnotationComment(aJCas);
                        modelAnno.setReference(organ_new);
                        modelAnno.setKey("propername");
                        modelAnno.setValue("1");
                        addAnnotatorComment(aJCas, modelAnno);
                    }
                }
                if (person){
                    Person person_new = new Person(aJCas, begin, end);
                    person_new.setValue("PER");
                    person_new.addToIndexes();
                    addAnnotatorComment(aJCas, person_new);
                    if (proper){
                        AnnotationComment modelAnno = new AnnotationComment(aJCas);
                        modelAnno.setReference(person_new);
                        modelAnno.setKey("propername");
                        modelAnno.setValue("1");
                        addAnnotatorComment(aJCas, modelAnno);
                    }
                }
                if(proper & (!organization & !typo & !person)){
                    NamedEntity newAnno = new NamedEntity(aJCas, begin, end);
                    newAnno.addToIndexes();
                    addAnnotatorComment(aJCas, newAnno);
                    AnnotationComment modelAnno = new AnnotationComment(aJCas);
                    modelAnno.setReference(newAnno);
                    modelAnno.setKey("propername");
                    modelAnno.setValue("1");
                    addAnnotatorComment(aJCas, modelAnno);
                }
            }
        }
    }

}