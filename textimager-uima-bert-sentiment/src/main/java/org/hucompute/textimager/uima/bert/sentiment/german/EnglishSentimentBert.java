package org.hucompute.textimager.uima.bert.sentiment.german;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.sentiment.base.SentimentBase;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

public class EnglishSentimentBert extends SentimentBase {
    /**
     * Model name
     */
    public static final String PARAM_MODEL_NAME = "modelName";
    @ConfigurationParameter(name = PARAM_MODEL_NAME)
    protected String modelName;

    /**
     * Sentiment mappings
     */
    public static final String PARAM_SENTIMENT_MAPPINGS = "sentimentMappings";
    @ConfigurationParameter(name = PARAM_SENTIMENT_MAPPINGS)
    protected String[] sentimentMappings;

    // cache sentiment mapping in json
    JSONObject sentimentMappingsJson;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        sentimentMappingsJson = new JSONObject();
        for (String mapping : sentimentMappings) {
            String[] fields = mapping.split(";", 2);
            if (fields.length == 2) {
                String label = fields[0].trim();
                float value = Float.parseFloat(fields[1].trim());
                sentimentMappingsJson.put(label, value);
            }
        }
    }

    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-bert-sentiment-en";
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
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
        JSONObject jsonObject = super.buildJSON(aJCas);
        jsonObject.put("model_name", modelName);
        jsonObject.put("sentiment_mapping", sentimentMappingsJson);
        return jsonObject;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
        if (jsonResult.has("selections")) {
            for (Object sels : jsonResult.getJSONArray("selections")) {
                JSONObject selection = (JSONObject) sels;
                String selectionAnnotation = selection.getString("selection");
                JSONArray sentences = selection.getJSONArray("sentences");
                for (Object sen : sentences) {
                    JSONObject sentence = (JSONObject) sen;

                    int begin = sentence.getJSONObject("sentence").getInt("begin");
                    int end = sentence.getJSONObject("sentence").getInt("end");

                    Sentiment sentiment = new Sentiment(aJCas, begin, end);
                    sentiment.setSentiment(sentence.getDouble("sentiment"));
                    sentiment.addToIndexes();

                    AnnotationComment comment = new AnnotationComment(aJCas);
                    comment.setReference(sentiment);
                    comment.setKey("selection");
                    comment.setValue(selectionAnnotation);
                    comment.addToIndexes();

                    AnnotationComment bertModel = new AnnotationComment(aJCas);
                    bertModel.setReference(sentiment);
                    bertModel.setKey("bert_model");
                    bertModel.setValue(modelName);
                    bertModel.addToIndexes();

                    addAnnotatorComment(aJCas, sentiment);
                }
            }
        }
    }
}
