package org.hucompute.textimager.uima.sentiment.base;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class SentimentBase extends DockerRestAnnotator {
    /**
     * Comma separated list of selection to process in order: "text",
     * or "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph", ...
     */
    public static final String PARAM_SELECTION = "selection";
    @ConfigurationParameter(name = PARAM_SELECTION, defaultValue = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence")
    protected String selection;

    @Override
    protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
        JSONArray selections = new JSONArray();

        try {
            String[] selection_types = selection.split(",", -1);
            for (String sel : selection_types) {
                JSONObject selection = new JSONObject();
                JSONArray sentences = new JSONArray();

                if (sel.equals("text")) {
                    JSONObject s = buildJSONSelection(aJCas, null);
                    if (s != null) {
                        sentences.put(s);
                    }
                } else {
                    Class<Annotation> clazz = (Class<Annotation>) Class.forName(sel);
                    for (Annotation ref : JCasUtil.select(aJCas, clazz)) {
                        JSONObject s = buildJSONSelection(aJCas, ref);
                        if (s != null) {
                            sentences.put(s);
                        }
                    }
                }

                if (sentences.length() > 0) {
                    selection.put("sentences", sentences);
                    selection.put("selection", sel);
                    selections.put(selection);
                }
            }
        }
        catch (ClassNotFoundException e) {
            throw new AnalysisEngineProcessException(e);
        }

        JSONObject request = new JSONObject();
        request.put("selections", selections);
        request.put("lang", aJCas.getDocumentLanguage());
        return request;
    }

    protected JSONObject buildJSONSelection(JCas aJCas, Annotation ref) {
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
}
