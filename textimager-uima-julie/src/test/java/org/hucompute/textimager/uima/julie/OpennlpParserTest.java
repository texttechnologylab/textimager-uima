package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.constituent.Constituent;
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
 * OpennlpParser
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide OpennlpParser test case */
public class OpennlpParserTest {
    public void init_input_dkpro(JCas jcas, String text) {
        de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentence = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(jcas, 0, text.length());
        sentence.addToIndexes();
        //split sentence to tokens
        String[] words = text.split(" ");

        //initialize token index
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
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void testProcess() throws IOException, UIMAException {
        // parameters
        String Text = "A study on the Prethcamide hydroxylation system in rat hepatic microsomes .";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        // input: de.julielab.jcore.types.Sentence
        init_input_dkpro(jCas, Text);

        //AnalysisEngineDescription engine = createEngineDescription(OpennlpParser.class);
        AnalysisEngineDescription engine = createEngineDescription(OpennlpParser.class);
        SimplePipeline.runPipeline(jCas, engine);

//        String[] casParsing = (String[]) JCasUtil.select(jCas, Constituent.class).stream().map(a -> a.getCat()).toArray(String[]::new);
        String[] casParsing = (String[]) JCasUtil.select(jCas, Constituent.class).stream().map(a -> a.getConstituentType()).toArray(String[]::new);
        String[] testParsing = {"NP", "NP", "PP", "NP", "NP", "PP", "NP"};
        assertArrayEquals(testParsing, casParsing);

    }
}
