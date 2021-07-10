package org.hucompute.textimager.uima.gervader;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

public class GerVaderSentiment extends DockerRestAnnotator {
    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-gervader";
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
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) {
        JSONArray sentences = new JSONArray();

        for (Sentence s : JCasUtil.select(aJCas, Sentence.class)) {
            JSONObject sentence = new JSONObject();
            sentence.put("text", s.getCoveredText());
            sentence.put("begin", s.getBegin());
            sentence.put("end", s.getEnd());
            sentences.put(sentence);
        }

        JSONObject request = new JSONObject();
        request.put("sentences", sentences);
        return request;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
        if (jsonResult.has("sentences")) {
            jsonResult.getJSONArray("sentences").forEach(s -> {
                JSONObject sentence = (JSONObject) s;
                int begin = sentence.getJSONObject("sentence").getInt("begin");
                int end = sentence.getJSONObject("sentence").getInt("end");

                Sentiment sentiment = new Sentiment(aJCas, begin, end);
                sentiment.setSentiment(sentence.getFloat("compound"));
                sentiment.addToIndexes();

                AnnotationComment commentPos = new AnnotationComment(aJCas);
                commentPos.setReference(sentiment);
                commentPos.setKey("GerVADER_pos");
                commentPos.setValue(String.valueOf(sentence.getFloat("pos")));
                commentPos.addToIndexes();

                AnnotationComment commentNeu = new AnnotationComment(aJCas);
                commentNeu.setReference(sentiment);
                commentNeu.setKey("GerVADER_neu");
                commentNeu.setValue(String.valueOf(sentence.getFloat("neu")));
                commentNeu.addToIndexes();

                AnnotationComment commentNeg = new AnnotationComment(aJCas);
                commentNeg.setReference(sentiment);
                commentNeg.setKey("GerVADER_neg");
                commentNeg.setValue(String.valueOf(sentence.getFloat("neg")));
                commentNeg.addToIndexes();
            });
        }
    }
}
