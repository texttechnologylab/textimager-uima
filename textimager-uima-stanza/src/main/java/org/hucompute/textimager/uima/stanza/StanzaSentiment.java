package org.hucompute.textimager.uima.stanza;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.sentiment.base.SentimentBase;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

import java.util.Collection;


public class StanzaSentiment extends SentimentBase {
    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-stanza";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "0.3";
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
    protected JSONObject buildJSONSelection(JCas aJCas, Annotation ref) {
        // build list of tokens for stanza instead of only raw text
        int begin;
        int end;

        Collection<Token> tokens;
        if (ref != null) {
            tokens = JCasUtil.selectCovered(Token.class, ref);
            begin = ref.getBegin();
            end = ref.getEnd();
        } else {
            tokens = JCasUtil.select(aJCas, Token.class);
            begin = 0;
            end = aJCas.getDocumentText().length();
        }

        if (tokens.isEmpty()) {
            return null;
        }

        JSONArray tokensJson = new JSONArray();
        for (Token token : tokens) {
            tokensJson.put(token.getCoveredText());
        }

        JSONObject sentence = new JSONObject();
        sentence.put("tokens", tokensJson);
        sentence.put("begin", begin);
        sentence.put("end", end);
        return sentence;
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

                    addAnnotatorComment(aJCas, sentiment);
                }
            }
        }
    }
}
