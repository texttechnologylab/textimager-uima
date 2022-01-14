package org.hucompute.textimager.uima.german.emotion.detection;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bitbucket.rkilinger.ged.Emotion;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class GermanEmotionDetectionTest {
    @Test
    public void multiTaggerTest() throws UIMAException {
        JCas jCas = JCasFactory.createJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText("Der Abschluss der Verhandlung war überraschend anstrengend.");

        AnalysisEngineDescription emotionDetection = createEngineDescription(GermanEmotionDetection.class);

        AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        AnalysisEngineDescription lemma = createEngineDescription(LanguageToolLemmatizer.class);

        SimplePipeline.runPipeline(jCas, segmenter, lemma, emotionDetection);


        for (Emotion emotion : JCasUtil.select(jCas, Emotion.class)) {
            if(emotion.getDisgust() == 1) {
                System.out.println(emotion.getCoveredText() +  " was found in disgust list");
            }
            if(emotion.getContempt() == 1) {
                System.out.println(emotion.getCoveredText() +  " was found in contempt list");
            }
            if(emotion.getSurprise() == 1) {
                System.out.println(emotion.getCoveredText() +  " was found in surprise list");
            }
            if(emotion.getFear() == 1) {
                System.out.println(emotion.getCoveredText() +  " was found in fear list");
            }
            if(emotion.getMourning() == 1) {
                System.out.println(emotion.getCoveredText() +  " was found in mourning list");
            }
            if(emotion.getAnger() == 1) {
                System.out.println(emotion.getCoveredText() +  " was found in anger list");
            }
            if(emotion.getJoy() == 1) {
                System.out.println(emotion.getCoveredText() +  " was found in joy list");
            }

        }
    }
}
