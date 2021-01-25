package org.hucompute.textimager.fasttext.labelannotator;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.fasttext.BaseAnnotator;
import org.hucompute.textimager.fasttext.FastTextResult;
import org.hucompute.textimager.fasttext.ProbabilityLabel;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

import java.util.ArrayList;

public class LabelAnnotatorSimple extends BaseAnnotator {
    /**
     * Score Threshold, only return predictions with score > threshold
     */
    public static final String PARAM_SCORE_THRESHOLD = "scoreThreshold";
    @ConfigurationParameter(name = PARAM_SCORE_THRESHOLD, mandatory = false, defaultValue = "0.0f")
    protected float scoreThreshold;

    @Override
    protected void processCoveredWithFastText(JCas jCas, Annotation ref) throws AnalysisEngineProcessException {
        String documentText = getTextWithDisambig(jCas, ref, useLemma, addPOS, removePunct, removeFunctionwords, "", "", "", ignoreMissingLemmaPOS);
        if (documentText.isEmpty()) {
            return;
        }

        // Begin und End setzen, entweder passend zu Ref oder kompletter Text
        int begin = (ref != null ? ref.getBegin() : 0);
        int end = (ref != null ? ref.getEnd() : jCas.getDocumentText().length());

        try {
            // result is a list, there can be more than one model loaded
            ArrayList<FastTextResult> results = fasttext.input(jCas.getDocumentLanguage(), documentText);

            for (FastTextResult ftResult : results) {
                ArrayList<ProbabilityLabel> labels = ftResult.getSortedResults(cutoff);

                // Insgesamt nur "fasttextK" Tags ausgeben
                int num = 0;
                for (ProbabilityLabel result : labels) {
                    // n/a Outputs weglassen
                    if (!result.getLabel().equals("n/a")) {
                        // Score threshold
                        if (scoreThreshold < 0 || result.getLogProb() > scoreThreshold) {
                            if (num >= fasttextK) {
                                break;
                            }
                            num++;

                            CategoryCoveredTagged cat = new CategoryCoveredTagged(jCas, begin, end);
                            cat.setValue(result.getLabel());
                            cat.setScore(result.getLogProb());
                            cat.setTags(tags);
                            cat.setRef(ref);
                            cat.addToIndexes();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            throw new AnalysisEngineProcessException("error processing: " + ex.getMessage(), null, ex);
        }
    }

}
