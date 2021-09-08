package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Token;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;
/**
 * StanfordLemmatizer
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide StanfordLemmatizer test case */
public class StanfordLemmatizerTest {
    public void init_input(JCas jcas, String text, String POSTAG) {
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = POSTAG.split(" ");

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

            POSTag pos = new POSTag(jcas);
            pos.setValue(postags[i]);
            pos.addToIndexes();

            FSArray postagss = new FSArray(jcas, 5);
            postagss.set(0, pos);
            postagss.addToIndexes();
            token.setPosTag(postagss);

            index_start = index_end + 1;
        }
    }
    public void init_input_dkpro(JCas jcas, String text, String POSTAG) {
        String[] words = text.split(" ");
        String[] postags = POSTAG.split(" ");
        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< postags.length; i++) {
            index_end = index_start + words[i].length();
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token token = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(jcas);
            POS pos = new POS(jcas);

            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();

            pos.setBegin(index_start);
            pos.setEnd(index_end);
            pos.setPosValue(postags[i]);
            pos.addToIndexes();

            token.setPos(pos);
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
        String Text = "Plectranthus barbatus is a medicinal plant used to treat a wide range of disorders including seizure .";
        String Postag = "NN NN VBZ DT JJ NN VBN TO VB DT JJ NN IN NNS VBG NN .";

        JCas jCas = JCasFactory.createText(Text);

        // input: de.julielab.jcore.types.Token
        //        de.julielab.jcore.types.POSTag
        init_input_dkpro(jCas, Text, Postag);
        // get postag
        //AnalysisEngineDescription engine_postag = createEngineDescription(OpennlpPostag.class);
//        AnalysisEngineDescription engine_postag = createEngineDescription(OpennlpPostag.class);
//
//        SimplePipeline.runPipeline(jCas, engine_postag);
//
//        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);
//        jCas.reset();
//        jCas.setDocumentText(Text);
//
//        init_jcas(jCas, casPostag);
        //AnalysisEngineDescription engine = createEngineDescription(BioLemmatizer.class);
        //AnalysisEngineDescription engine = createEngineDescription(StanfordLemmatizer.class);
        AnalysisEngineDescription engine = createEngineDescription(StanfordLemmatizerRest.class, StanfordLemmatizerRest.PARAM_REST_ENDPOINT, "http://localhost:8080");
        SimplePipeline.runPipeline(jCas, engine);

        String[] casLemma = (String[]) JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class).stream().map(b -> b.getLemma().getValue()).toArray(String[]::new);
        String[] testLemma = new String[] {"plectranthus", "barbatus", "be", "a",
                "medicinal", "plant", "use", "to", "treat", "a", "wide", "range", "of", "disorder", "include", "seizure", "."};

        assertArrayEquals(testLemma, casLemma);

        System.out.println(XmlFormatter.getPrettyString(jCas));
    }
}
