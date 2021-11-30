package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.Lemma;
import de.julielab.jcore.types.LikelihoodIndicator;
import de.julielab.jcore.types.Sentence;
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
 * LikelihoodDetection
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide LikelihoodDetection test case */
public class LikelihoodDetectionTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    public void init_input(JCas jcas, String text, String[] lemmas) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = words.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            Token token = new Token(jcas);
            index_end = index_start + words[i].length();
            token.setBegin(index_start);
            token.setEnd(index_end);

            Lemma lemma = new Lemma(jcas);
            lemma.setBegin(index_start);
            lemma.setEnd(index_end);
            lemma.setValue(lemmas[i]);

            token.setLemma(lemma);
            token.addToIndexes();
            index_start = index_end + 1;
        }

    }
    public void init_input_dkpro(JCas jcas, String text, String[] lemmas) {
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = words.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token token = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(jcas);
            index_end = index_start + words[i].length();
            token.setBegin(index_start);
            token.setEnd(index_end);

            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma lemma = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma(jcas);
            lemma.setBegin(index_start);
            lemma.setEnd(index_end);
            lemma.setValue(lemmas[i]);

            token.setLemma(lemma);
            token.addToIndexes();
            index_start = index_end + 1;
        }

    }
    @Test
    public void likelihoodDetectionTest() throws IOException, UIMAException {
        // parameters
        String Text = "PML appears to be transcriptionally regulated by class I and II interferons , which raises the possibility that interferons modulate the function and growth and differentiation potential of normal myeloid cells and precursors .";
        String[] Lemmas = {"pml", "appear", "to", "be", "transcriptionally", "regulate", "by", "class", "i", "and", "ii",
                "interferon", ",", "which", "raise", "the", "possibility", "that", "interferon", "modulate", "the", "function", "and", "growth",
                "and", "differentiation", "potential", "of", "normal", "myeloid", "cell", "and", "precursor", "."};
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        init_input_dkpro(jCas, Text, Lemmas);


        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(LikelihoodDetection.class);
        AnalysisEngineDescription engine = createEngineDescription(LikelihoodDetection.class);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casLikelihoodIndicator = (String[]) JCasUtil.select(jCas, LikelihoodIndicator.class).stream().map(a -> a.getCoveredText()).toArray(String[]::new);
        String[] casLikelihoodCategory = (String[]) JCasUtil.select(jCas, LikelihoodIndicator.class).stream().map(a -> a.getLikelihood()).toArray(String[]::new);

        String[] testcasLikelihoodIndicator= new String[] {
                "appears","raises the possibility"
        };

        String[] testcasLikelihoodCategory= new String[] {
                "moderate","moderate"
        };

        assertArrayEquals(casLikelihoodIndicator, testcasLikelihoodIndicator);

    }
}
