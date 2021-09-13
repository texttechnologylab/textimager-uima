package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.*;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.assertArrayEquals;
/**
 * MSTParser
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide MSTParser test case */
public class MSTParserTest {
    public void init_input(JCas jcas, String text, String POSTAG, String LEMMA) {
        Sentence sentence = new Sentence(jcas, 0, text.length());
        sentence.addToIndexes();
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = POSTAG.split(" ");
        String[] lemmas = LEMMA.split(" ");


        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
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

            Lemma lemma = new Lemma(jcas);
            lemma.setBegin(index_start);
            lemma.setEnd(index_end);
            lemma.setValue(lemmas[i]);

            token.setLemma(lemma);
            token.addToIndexes();

            index_start = index_end + 1;
        }

    }

    public void init_input_dkpro(JCas jcas, String text, String POSTAG, String LEMMA) {
        de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentence = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(jcas, 0, text.length());
        sentence.addToIndexes();
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = POSTAG.split(" ");
        String[] lemmas = LEMMA.split(" ");


        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        //loop for all words
        for (int i=0; i< words.length; i++) {
            index_end = index_start + words[i].length();
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token token = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(jcas);

            token.setBegin(index_start);
            token.setEnd(index_end);
            token.addToIndexes();

            POS pos = new POS(jcas);
            pos.setPosValue(postags[i]);
            pos.addToIndexes();

            token.setPos(pos);

            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma lemma = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma(jcas);
            lemma.setBegin(index_start);
            lemma.setEnd(index_end);
            lemma.setValue(lemmas[i]);

            token.setLemma(lemma);
            token.addToIndexes();

            index_start = index_end + 1;
        }

    }
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void mstParserTest() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("Triptonide acts as a novel antiprostate cancer agent.");
        jCas.setDocumentLanguage("en");

        String Postag = "NN VBZ IN DT JJ NN NN NN";
        String Lemma = "triptonide act as a novel antiprostate cancer agent";
        // input: de.julielab.jcore.types.Sentence
        //        de.julielab.jcore.types.Token
        //        de.julielab.jcore.types.POSTag
        //        de.julielab.jcore.types.Lemma

        init_input_dkpro(jCas, jCas.getDocumentText(), Postag, Lemma);

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        AnalysisEngineDescription engine = createEngineDescription(MSTParser.class);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casLabel = (String[]) JCasUtil.select(jCas, DependencyRelation.class).stream().map(a -> a.getLabel()).toArray(String[]::new);


        String[] testcasLabel= new String[] {
                "SBJ", null, "ADV", "NMOD", "NMOD", "NMOD", "NMOD", "PMOD"
        };

        for (Token objekt : select(jCas, Token.class)) {
            System.out.println("Lemma : " + objekt.getLemma().getValue());
            System.out.println("POS : " + objekt.getPosTag(0).getValue());
        }

        assertArrayEquals(casLabel, testcasLabel);


    }
}
