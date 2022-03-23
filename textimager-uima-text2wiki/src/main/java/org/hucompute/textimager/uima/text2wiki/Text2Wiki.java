package org.hucompute.textimager.uima.text2wiki;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

public class Text2Wiki extends DockerRestAnnotator {
    // Model language, defaults to document language
    public static final String PARAM_LANGUAGE = "language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    // Model name:
    // - hs (hierarchical softmax): sum of 1 probability distribution over all labels
    // - ova (one-vs-all): 0-1 probability per each label
    public static final String PARAM_MODEL_NAME = "modelName";
    @ConfigurationParameter(name = PARAM_MODEL_NAME)
    protected String modelName;

    // Max amount of labels to return
    public static final String PARAM_K = "k";
    @ConfigurationParameter(name = PARAM_K, mandatory = false, defaultValue = "100")
    protected int k;

    // Minimal score threshold of each prediction
    public static final String PARAM_TH = "th";
    @ConfigurationParameter(name = PARAM_TH, mandatory = false, defaultValue = "0.01")
    protected float th;

    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-text2wiki";
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
        return "/process";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) {
        String docLang = aJCas.getDocumentLanguage();
        if (language != null && !language.isEmpty()) {
            System.out.println("Overwriting document language to: " + language);
            docLang = language;
        }

        StringBuilder text = new StringBuilder();
        for (Token token : JCasUtil.select(aJCas, Token.class)) {
            String pos = token.getPosValue();
            if (!docLang.equalsIgnoreCase("de") || (pos == null || !pos.equalsIgnoreCase("PUNCT"))) {
                text
                        .append(token.getCoveredText())
                        .append(" ");
            }
        }

        JSONObject json = new JSONObject();
        json.put("text", text.toString());
        json.put("lang", docLang);
        json.put("model", modelName);
        json.put("k", k);
        json.put("th", th);
        return json;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
        String lang = "";
        if (jsonResult.has("lang")) {
            lang = jsonResult.getString("lang");
        }

        if (jsonResult.has("labels_scores")) {
            String finalLang = lang;

            jsonResult.getJSONArray("labels_scores").forEach(ls -> {
                JSONObject labelScore = (JSONObject) ls;

                String label = labelScore.getString("label");
                double score = labelScore.getDouble("score");

                CategoryCoveredTagged cat = new CategoryCoveredTagged(
                        aJCas,
                        0,
                        aJCas.getDocumentText().length()
                );
                cat.setValue(label);
                cat.setScore(score);
                cat.setTags("text2wiki_2");
                cat.addToIndexes();

                addAnnotatorComment(aJCas, cat);

                AnnotationComment commentLang = new AnnotationComment(aJCas);
                commentLang.setReference(cat);
                commentLang.setKey("text2wiki_lang");
                commentLang.setValue(finalLang);
                commentLang.addToIndexes();

                AnnotationComment commentModel = new AnnotationComment(aJCas);
                commentModel.setReference(cat);
                commentModel.setKey("text2wiki_model");
                commentModel.setValue(modelName);
                commentModel.addToIndexes();

                AnnotationComment commentK = new AnnotationComment(aJCas);
                commentK.setReference(cat);
                commentK.setKey("text2wiki_k");
                commentK.setValue(String.valueOf(k));
                commentK.addToIndexes();

                AnnotationComment commentTh = new AnnotationComment(aJCas);
                commentTh.setReference(cat);
                commentTh.setKey("text2wiki_th");
                commentTh.setValue(String.valueOf(th));
                commentTh.addToIndexes();
            });
        }
    }
}
