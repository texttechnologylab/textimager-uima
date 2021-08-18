package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.EntityMention;
import de.julielab.jcore.types.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;
/**
 * LingpipePorterstemmer
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide LingpipePorterstemmer test case */
public class LingpipePorterstemmerTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void lingpigeTest() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("Three horses were going contemplatively around bushy bushes .");
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(LingpipePorterstemmer.class);
        AnalysisEngineDescription engine = createEngineDescription(LingpipePorterstemmer.class);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casStemmer = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getStemmedForm().getValue()).toArray(String[]::new);

        String[] casStemmerDkpro = (String[]) JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class).stream().map(a -> a.getStemValue()).toArray(String[]::new);


        String[] testStemmer= new String[] {
                "Three","hors","were","go","contempl","around","bushi","bush","."
        };

        assertArrayEquals(casStemmerDkpro, casStemmer);
        assertArrayEquals(testStemmer, casStemmer);

    }
}
