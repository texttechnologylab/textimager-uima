package org.hucompute.textimager.uima.german.emotion.detection;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.bitbucket.rkilinger.ged.Emotion;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class GermanEmotionDetectionTest {
    @Test
    public void multiTaggerTest() throws UIMAException {
        JCas jCas = JCasFactory.createJCas();
        jCas.setDocumentLanguage("de");
        jCas.setDocumentText("Das ist der Text.");

        AnalysisEngineDescription emotionDetection = createEngineDescription(GermanEmotionDetection.class,
                GermanEmotionDetection.PARAM_MODEL_LOCATION, new String[]{
                            "pfad1",
                            "pfad2"
                        }
        );

        AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);

        SimplePipeline.runPipeline(jCas, segmenter, emotionDetection);

        System.out.println(XmlFormatter.getPrettyString(jCas));

        for (Emotion emotion : JCasUtil.select(jCas, Emotion.class)) {
            System.out.println(emotion.getCoveredText() + " -> " + emotion.getSentiment());
        }
    }
}
