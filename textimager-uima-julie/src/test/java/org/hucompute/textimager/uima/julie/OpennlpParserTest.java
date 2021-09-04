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
        de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentence = new Sentence(jCas, 0, Text.length());
        sentence.addToIndexes();

        //AnalysisEngineDescription engine = createEngineDescription(OpennlpParser.class);
        AnalysisEngineDescription engine = createEngineDescription(OpennlpParser.class);
        SimplePipeline.runPipeline(jCas, engine);

//        String[] casParsing = (String[]) JCasUtil.select(jCas, Constituent.class).stream().map(a -> a.getCat()).toArray(String[]::new);
        String[] casParsing = (String[]) JCasUtil.select(jCas, Constituent.class).stream().map(a -> a.getConstituentType()).toArray(String[]::new);
        String[] testParsing = {"NP", "NP", "PP", "NP", "NP", "PP", "NP"};
        assertArrayEquals(testParsing, casParsing);

    }
}
