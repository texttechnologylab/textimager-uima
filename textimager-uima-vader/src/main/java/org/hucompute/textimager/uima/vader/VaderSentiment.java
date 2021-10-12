package org.hucompute.textimager.uima.vader;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.base.TextImagerBaseAnnotator;
import org.texttechnologylab.annotation.AnnotationComment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VaderSentiment extends TextImagerBaseAnnotator {
    /**
     * Comma separated list of selection to process in order: "text",
     * or "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph", ...
     */
    public static final String PARAM_SELECTION = "selection";
    @ConfigurationParameter(name = PARAM_SELECTION, defaultValue = "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence")
    protected String selection;

    @Override
    protected String getModelName() {
        return "com.github.apanimesh061/vader-sentiment-analyzer";
    }

    @Override
    protected String getModelVersion() {
        return "1.0";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }

    protected org.hucompute.textimager.uima.type.VaderSentiment processSelection(JCas aJCas, Annotation ref, String selection) throws IOException {
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

        if (!text.isEmpty()) {
            SentimentAnalyzer sentimentAnalyzer = new SentimentAnalyzer(text);
            sentimentAnalyzer.analyze();

            org.hucompute.textimager.uima.type.VaderSentiment vaderSentiment = new org.hucompute.textimager.uima.type.VaderSentiment(aJCas, begin, end);
            vaderSentiment.setSentiment(sentimentAnalyzer.getPolarity().getOrDefault("compound", 0.0f));
            vaderSentiment.setPos(sentimentAnalyzer.getPolarity().getOrDefault("positive", 0.0f));
            vaderSentiment.setNeu(sentimentAnalyzer.getPolarity().getOrDefault("neutral", 0.0f));
            vaderSentiment.setNeg(sentimentAnalyzer.getPolarity().getOrDefault("negative", 0.0f));
            vaderSentiment.addToIndexes();

            AnnotationComment comment = new AnnotationComment(aJCas);
            comment.setReference(vaderSentiment);
            comment.setKey("selection");
            comment.setValue(selection);
            comment.addToIndexes();

            addAnnotatorComment(aJCas, vaderSentiment);

            return vaderSentiment;
        }

        return null;
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        try {
            String[] selection_types = selection.split(",", -1);
            for (String sel : selection_types) {
                if (sel.equals("text")) {
                    processSelection(aJCas, null, sel);
                } else {
                    Class<Annotation> clazz = (Class<Annotation>) Class.forName(sel);
                    List<org.hucompute.textimager.uima.type.VaderSentiment> sentiments = new ArrayList<>();
                    for (Annotation ref : JCasUtil.select(aJCas, clazz)) {
                       sentiments.add(processSelection(aJCas, ref, sel));
                    }

                    // calculate average
                    if (sentiments.size() > 1) {
                        float compoundAvg = 0.0f;
                        float posAvg = 0.0f;
                        float neuAvg = 0.0f;
                        float negAvg = 0.0f;
                        for (org.hucompute.textimager.uima.type.VaderSentiment vaderSentiment : sentiments) {
                            compoundAvg += vaderSentiment.getSentiment();
                            posAvg += vaderSentiment.getPos();
                            neuAvg += vaderSentiment.getNeu();
                            negAvg += vaderSentiment.getNeg();
                        }
                        compoundAvg /= sentiments.size();
                        posAvg /= sentiments.size();
                        neuAvg /= sentiments.size();
                        negAvg /= sentiments.size();

                        org.hucompute.textimager.uima.type.VaderSentiment vaderSentiment = new org.hucompute.textimager.uima.type.VaderSentiment(aJCas, 0, aJCas.getDocumentText().length());
                        vaderSentiment.setSentiment(compoundAvg);
                        vaderSentiment.setPos(posAvg);
                        vaderSentiment.setNeu(neuAvg);
                        vaderSentiment.setNeg(negAvg);
                        vaderSentiment.addToIndexes();

                        AnnotationComment comment = new AnnotationComment(aJCas);
                        comment.setReference(vaderSentiment);
                        comment.setKey("selection");
                        comment.setValue(sel);
                        comment.addToIndexes();

                        addAnnotatorComment(aJCas, vaderSentiment);
                    }
                }
            }
        }
        catch (ClassNotFoundException | IOException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }
}
