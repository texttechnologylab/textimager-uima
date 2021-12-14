package org.hucompute.textimager.uima.gnfinder;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

public class GNfinder extends DockerRestAnnotator {
    public static final String PARAM_NAMED_ENTITY_MAPPING_LOCATION = ComponentParameters.PARAM_NAMED_ENTITY_MAPPING_LOCATION;
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    public static final String PARAM_VERIFICATION = "verification";
    public static final String PARAM_VERIFICATION_SOURCES = "verificationSources";
    public static final String PARAM_WORDS_AROUND = "wordsAround";
    public static final String PARAM_NO_BAYES = "noBayes";
    public static final String PARAM_ODD_DETAILS = "oddDetails";
    @ConfigurationParameter(name = PARAM_NAMED_ENTITY_MAPPING_LOCATION, mandatory = false)
    protected String mappingProviderLocation;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;
    @ConfigurationParameter(name = PARAM_VERIFICATION, mandatory = false, defaultValue = "false")
    protected boolean verification;
    @ConfigurationParameter(name = PARAM_VERIFICATION_SOURCES, mandatory = false, defaultValue = "")
    protected String verificationSources;
    @ConfigurationParameter(name = PARAM_WORDS_AROUND, mandatory = false, defaultValue = "0")
    protected int wordsAround;
    @ConfigurationParameter(name = PARAM_NO_BAYES, mandatory = false, defaultValue = "false")
    protected boolean noBayes;
    @ConfigurationParameter(name = PARAM_ODD_DETAILS, mandatory = false, defaultValue = "true")
    protected boolean oddDetails;

    protected MappingProvider namedEntityMappingProvider;

    @Override
    protected String getDefaultDockerImage() {
        return "gnames/gnfinder";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "v0.16.3-1-g134c453";
    }

    @Override
    protected int getDefaultDockerPort() {
        return 8999;
    }

    @Override
    protected String getRestRoute() {
        return "/api/v1/find";
    }

    @Override
    protected String getReadyRoute() {
        return "/api/v1/ping";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.2";
    }

    @Override
    protected boolean isReadyCheck(String result) {
        return result.equals("pong");
    }

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        namedEntityMappingProvider = MappingProviderFactory.createNerMappingProvider(this, mappingProviderLocation, language, variant);
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) {
        JSONObject json = new JSONObject();
        json.put("text", aJCas.getDocumentText());

        json.put("noBayes", noBayes);
        json.put("oddDetails", oddDetails);
        json.put("wordsAround", wordsAround);

        // lang for bayes, only supports "detect", "eng" and "deu"
        String lang = "eng";
        if (aJCas.getDocumentLanguage().equals("de")) {
            lang = "deu";
        }
        json.put("language", lang);

        // TODO https://verifier.globalnames.org/data_sources
        json.put("verification", verification);
        JSONArray sources = new JSONArray();
        if (verificationSources != null && !verificationSources.isEmpty()) {
            for (String source : verificationSources.split(",", -1)) {
                if (!source.isEmpty()) {
                    sources.put(Integer.parseInt(source));
                }
            }
        }
        json.put("sources", sources);

        return json;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
        try {
            namedEntityMappingProvider.configure(aJCas.getCas());
        } catch (AnalysisEngineProcessException e) {
            e.printStackTrace();
        }

        if (jsonResult.has("names") && !jsonResult.isNull("names")) {
            for (Object n : jsonResult.getJSONArray("names")) {
                try {
                    JSONObject gname = (JSONObject) n;

                    String annotationNomenType = gname.getString("annotationNomenType");
                    String name = gname.getString("name");
                    String verbatim = gname.getString("verbatim");
                    int start = gname.getInt("start");
                    int end = gname.getInt("end");
                    int cardinality = gname.getInt("cardinality");

                    // TODO Type mappings?
                    Type type = namedEntityMappingProvider.getTagType(annotationNomenType);
                    NamedEntity neAnno = (NamedEntity) aJCas.getCas().createAnnotation(type, start, end);

                    // TODO values?
                    neAnno.setValue(annotationNomenType);
                    neAnno.setIdentifier(name);

                    neAnno.addToIndexes();

                    AnnotationComment comment1 = new AnnotationComment(aJCas);
                    comment1.setReference(neAnno);
                    comment1.setKey("verbatim");
                    comment1.setValue(verbatim);
                    comment1.addToIndexes();

                    AnnotationComment comment2 = new AnnotationComment(aJCas);
                    comment2.setReference(neAnno);
                    comment2.setKey("cardinality");
                    comment2.setValue(String.valueOf(cardinality));
                    comment2.addToIndexes();

                    // TODO verification
                    if (gname.has("verification")) {
                        JSONObject verification = gname.getJSONObject("verification");
                        if (verification.has("bestResult")) {
                            JSONObject best = verification.getJSONObject("bestResult");
                            String recordId = best.getString("recordId");
                            String outlink = best.getString("outlink");
                            String matchedName = best.getString("matchedName");

                            AnnotationComment c1 = new AnnotationComment(aJCas);
                            c1.setReference(neAnno);
                            c1.setKey("recordId");
                            c1.setValue(recordId);
                            c1.addToIndexes();

                            AnnotationComment c2 = new AnnotationComment(aJCas);
                            c2.setReference(neAnno);
                            c2.setKey("outlink");
                            c2.setValue(outlink);
                            c2.addToIndexes();

                            AnnotationComment c3 = new AnnotationComment(aJCas);
                            c3.setReference(neAnno);
                            c3.setKey("matchedName");
                            c3.setValue(matchedName);
                            c3.addToIndexes();
                        }
                    }

                    addAnnotatorComment(aJCas, neAnno);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
