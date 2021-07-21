package org.hucompute.textimager.uima.stanza;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONObject;

public class StanzaSentiment extends DockerRestAnnotator {
    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-stanza";
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
        return "/sentiment";
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
        JSONArray sentences = new JSONArray();

        for (Sentence sentenceCas : JCasUtil.select(aJCas, Sentence.class)) {
            JSONObject sentence = new JSONObject();
            sentence.put("begin", sentenceCas.getBegin());
            sentence.put("end", sentenceCas.getEnd());

            JSONArray tokens = new JSONArray();
            for (Token tokenCas : JCasUtil.selectCovered(Token.class, sentenceCas)) {
                JSONObject token = new JSONObject();
                token.put("text", tokenCas.getCoveredText());
                token.put("begin", tokenCas.getBegin());
                token.put("end", tokenCas.getEnd());
                tokens.put(token);
            }
            sentence.put("tokens", tokens);

            sentences.put(sentence);
        }

        JSONObject request = new JSONObject();
        request.put("lang", aJCas.getDocumentLanguage());
        request.put("sentences", sentences);
        return request;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
        if (jsonResult.has("sentences")) {
            for (Object sen : jsonResult.getJSONArray("sentences")) {
                JSONObject sentence = (JSONObject) sen;

                int begin = sentence.getJSONObject("sentence").getInt("begin");
                int end = sentence.getJSONObject("sentence").getInt("end");

                Sentiment sentiment = new Sentiment(aJCas, begin, end);
                sentiment.setSentiment(sentence.getDouble("sentiment"));
                sentiment.addToIndexes();
            }
        }
    }
}
