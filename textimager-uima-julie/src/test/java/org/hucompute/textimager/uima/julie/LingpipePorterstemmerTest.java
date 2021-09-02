package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.EntityMention;
import de.julielab.jcore.types.Token;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
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
    public void init_input(JCas jcas, String text) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            index_end = index_start + words[i].length();
            Token token = new Token(jcas);

            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();

            index_start = index_end + 1;
        }
    }
    public void init_input_dkpro(JCas jcas, String text) {
        String[] words = text.split(" ");
        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            index_end = index_start + words[i].length();
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token token = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(jcas);

            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();

            index_start = index_end + 1;
        }
    }
    @Test
    public void lingpigeTest() throws IOException, UIMAException {
        String Text = "Three horses were going contemplatively around bushy bushes .";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        // input: de.julielab.jcore.types.Token
        init_input_dkpro(jCas, Text);

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
