package org.hucompute.textimager.uima.gervader;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

public class GerVaderSentiment extends DockerRestAnnotator {
    /**
     * Comma separated list of selection to process in order: "text",
     * or "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph", ...
     */
    public static final String PARAM_SELECTION = "selection";
    @ConfigurationParameter(name = PARAM_SELECTION, defaultValue = "text")
    protected String selection;

    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-gervader";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "0.2";
    }

    @Override
    protected int getDefaultDockerPort() {
        return 8000;
    }

    @Override
    protected String getRestRoute() {
        return "/process";
    }

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
        JSONArray selections = new JSONArray();

        try {
            String[] selection_types = selection.split(",", -1);
            for (String sel : selection_types) {
                JSONObject selection = new JSONObject();
                JSONArray sentences = new JSONArray();

                if (sel.equals("text")) {
                    sentences.put(buildJSONSelection(aJCas, null));
                } else {
                    Class<Annotation> clazz = (Class<Annotation>) Class.forName(sel);
                    for (Annotation ref : JCasUtil.select(aJCas, clazz)) {
                        sentences.put(buildJSONSelection(aJCas, ref));
                    }
                }

                selection.put("sentences", sentences);
                selection.put("selection", sel);
                selections.put(selection);
            }
        }
        catch (ClassNotFoundException e) {
            throw new AnalysisEngineProcessException(e);
        }

        JSONObject request = new JSONObject();
        request.put("selections", selections);
        return request;
    }

    private JSONObject buildJSONSelection(JCas aJCas, Annotation ref) {
        String text;
        int begin;
        int end;

        if (ref != null) {
            text = ref.getCoveredText();
            begin = ref.getBegin();
            end = ref.getEnd();
        } else {
            text = aJCas.getDocumentText();
            begin = 0;
            end = aJCas.getDocumentText().length();
        }

        JSONObject sentence = new JSONObject();
        sentence.put("text", text);
        sentence.put("begin", begin);
        sentence.put("end", end);
        return sentence;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
        if (jsonResult.has("selections")) {
            for (Object sels : jsonResult.getJSONArray("selections")) {
                JSONObject selection = (JSONObject) sels;
                String selectionAnnotation = selection.getString("selection");
                JSONArray sentences = selection.getJSONArray("sentences");
                for (Object sen : sentences) {
                    JSONObject sentence = (JSONObject) sen;

                    int begin = sentence.getJSONObject("sentence").getInt("begin");
                    int end = sentence.getJSONObject("sentence").getInt("end");

                    org.hucompute.textimager.uima.type.GerVaderSentiment sentiment = new org.hucompute.textimager.uima.type.GerVaderSentiment(aJCas, begin, end);
                    sentiment.setSentiment(sentence.getDouble("compound"));
                    sentiment.setPos(sentence.getDouble("pos"));
                    sentiment.setNeu(sentence.getDouble("neu"));
                    sentiment.setNeg(sentence.getDouble("neg"));
                    sentiment.addToIndexes();

                    AnnotationComment comment = new AnnotationComment(aJCas);
                    comment.setReference(sentiment);
                    comment.setKey("selection");
                    comment.setValue(selectionAnnotation);
                    comment.addToIndexes();
                }
            }
        }
    }
}
