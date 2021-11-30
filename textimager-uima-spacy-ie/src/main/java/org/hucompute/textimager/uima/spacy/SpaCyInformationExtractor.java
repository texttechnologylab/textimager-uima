package org.hucompute.textimager.uima.spacy;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.unihd.dbs.uima.types.heideltime.Timex3;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.GeoNamesEntity;
import org.texttechnologylab.annotation.semaf.isobase.Entity;
import org.texttechnologylab.annotation.semaf.semafsr.SrLink;

import java.util.Iterator;

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
        JSONArray psrs = jsonResult.getJSONObject("multitag").getJSONArray("psrs");

        try{
            processPSR(aJCas, psrs);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void processPSR(JCas aJCas, JSONArray psrs) {
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
                        }
                        SrLink srlinkl = new SrLink(aJCas);
                        srlinkl.setRel_type(key);
                        srlinkl.setGround(argAnno);
                        srlinkl.setFigure(predAnno);
                        argAnno.addToIndexes();
                        srlinkl.addToIndexes();
                        JCasUtil.selectCovered(aJCas, Timex3.class, begin, end).forEach(a -> {
                            System.out.println("covered Timex3Entity" + a.getCoveredText() + " " +
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
            JSONArray geos = sr.getJSONArray("geos");

            JCasUtil.selectCovered(aJCas, Timex3.class, begin_s, end_s).forEach(a ->{
                Integer beginA = (Integer) a.getBegin();
                Integer endA = (Integer)  a.getEnd();
                JCasUtil.selectCovered(aJCas, Token.class, beginA, endA).forEach(token -> {
                        Integer beginT = (Integer) token.getBegin();
                        Integer endT = (Integer)  token.getEnd();
                        System.out.println("    Token: " + token.getCoveredText() + " " + token.getBegin() +
                                " " + token.getEnd());
                        mos.forEach(m -> {
                            JSONArray mA = (JSONArray) m;
                            Integer beginM = (Integer) mA.get(1);
                            Integer endM = (Integer)  mA.get(2);
                            if ((beginT == beginM) && (endT == endM)) {
                                Entity argAnno = new Entity(aJCas, beginT, endT);
                                SrLink srlinkl = new SrLink(aJCas);
                                srlinkl.setRel_type("TIME");
                                srlinkl.setGround(argAnno);
                                srlinkl.setFigure(predAnno);
                                argAnno.addToIndexes();
                                srlinkl.addToIndexes();
                            }
                        });
                });
            });
            JCasUtil.selectCovered(aJCas, GeoNamesEntity.class, begin_s, end_s).forEach(a ->{
                Integer beginA = (Integer) a.getBegin();
                Integer endA = (Integer)  a.getEnd();
                JCasUtil.selectCovered(aJCas, Token.class, beginA, endA).forEach(token -> {
                    Integer beginT = (Integer) token.getBegin();
                    Integer endT = (Integer)  token.getEnd();
                    System.out.println("    Token: " + token.getCoveredText() + " " + token.getBegin() +
                            " " + token.getEnd());
                    geos.forEach(m -> {
                    JSONArray mA = (JSONArray) m;
                    Integer beginM = (Integer) mA.get(1);
                    Integer endM = (Integer)  mA.get(2);
                    if ((beginA == beginM) && (endA == endM)) {
                        System.out.println("        annotating " + mA.getString(0));
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
            });
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
