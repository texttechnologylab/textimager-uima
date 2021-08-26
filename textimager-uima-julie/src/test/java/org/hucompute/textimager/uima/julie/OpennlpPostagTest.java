package org.hucompute.textimager.uima.julie;

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
 * OpennlpPostag
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide OpennlpPostag test case */
public class OpennlpPostagTest {
    /**
     * Test for simple english text.
     * @throws UIMAException
     */
    public void init_input(JCas jcas, String text) {
        Sentence sentence = new Sentence(jcas, 0, text.length());
        sentence.addToIndexes();
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (String word : words) {
            Token token = new Token(jcas);
            index_end = index_start + word.length();
            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();
            index_start = index_end + 1;
        }
    }
    @Test
    public void opennlpPOSTestEN() throws IOException, UIMAException {
        // parameters
        String Text = "A study on the Prethcamide.";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        // input: de.julielab.jcore.types.Sentence
        //        de.julielab.jcore.types.Token
        init_input(jCas, Text);

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class);
        AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);

        String[] casPostagDkpro = (String[]) JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class).stream().map(a -> a.getPosValue()).toArray(String[]::new);


        String[] testPos= new String[] {
                "DT","NN","IN","DT","NNP"
        };

        assertArrayEquals(casPostag, casPostagDkpro);
        assertArrayEquals(testPos, casPostag);

    }
    /**
     * Test for simple german text.
     * @throws UIMAException
     */
    @Test
    public void opennlpPOSTestDE() throws IOException, UIMAException {
        // parameters
        String Text = "Kleinere hilusnahe Fistelungen werden mit Tacho-Comb-Vlies abgedeckt.";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("de");

        // input: de.julielab.jcore.types.Sentence
        //        de.julielab.jcore.types.Token
        init_input(jCas, Text);

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class);
        AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);

        String[] casPostagDkpro = (String[]) JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class).stream().map(a -> a.getPosValue()).toArray(String[]::new);


        String[] testPos= new String[] {
                "ADJA","ADJD","NN","VAFIN","APPR","NN", "ADJA"
        };

        assertArrayEquals(casPostag, casPostagDkpro);
        assertArrayEquals(testPos, casPostag);

    }
}
