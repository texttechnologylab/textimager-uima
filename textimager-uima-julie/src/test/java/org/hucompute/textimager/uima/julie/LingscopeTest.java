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
import static org.junit.Assert.assertArrayEquals;
/**
 * Lingscope
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide Lingscope test case */
public class LingscopeTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    public void init_input(JCas jcas, String text, String POSTAG) {
        //split sentence to tokens
        String[] tok = text.split(" ");
        String[] postags = POSTAG.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = tok.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            index_end = index_start + tok[i].length();
            Token token = new Token(jcas, index_start, index_end);

            //PennBioIEPOSTag pennpostag = new PennBioIEPOSTag(jcas, index_start, index_end);
            //pennpostag.setValue(pos[i]);

            //token.setPosTag(JCoReTools.addToFSArray(null, pennpostag));
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
        de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentence = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(jcas, 0, text.length());
        sentence.addToIndexes();
        //split sentence to tokens
        String[] tok = text.split(" ");
        String[] postags = POSTAG.split(" ");

        //initialize index
        int index_start = 0;
        int index_end = 0;
        int len = tok.length;

        //loop for all words
        for (int i=0; i < len; i++) {
            index_end = index_start + tok[i].length();
            de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token token = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token(jcas, index_start, index_end);

            //PennBioIEPOSTag pennpostag = new PennBioIEPOSTag(jcas, index_start, index_end);
            //pennpostag.setValue(pos[i]);

            //token.setPosTag(JCoReTools.addToFSArray(null, pennpostag));
            token.addToIndexes();

            POS pos = new POS(jcas);
            pos.setPosValue(postags[i]);
            pos.addToIndexes();

            index_start = index_end + 1;
        }

    }
    @Test
    public void lingscopeTest() throws IOException, UIMAException {
        // parameters
        String Text = "The patient denied leg pain but complained about a headache .";
        String PosTags = "DT NN VBD NN NN CC VBD IN DT NN .";

        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        // input: de.julielab.jcore.types.Sentence
        //        de.julielab.jcore.types.Token
        //        de.julielab.jcore.types.PennBioIEPOSTag
        init_input_dkpro(jCas, Text, PosTags);

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(Lingscope.class);
        AnalysisEngineDescription engine = createEngineDescription(Lingscope.class);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casLikelihoodIndicator = (String[]) JCasUtil.select(jCas, LikelihoodIndicator.class).stream().map(a -> a.getLikelihood() + " ; "
        + a.getBegin() + " ; "+a.getEnd()).toArray(String[]::new);

        String[] casScope = (String[]) JCasUtil.select(jCas, Scope.class).stream().map(a -> a.getBegin() + " ; "+a.getEnd()).toArray(String[]::new);

        //test Pennbios
        //

        String[] testcasLikelihoodIndicator= new String[] {
                "negation ; 12 ; 18"
        };

        String[] testcasScope= new String[] {
                "12 ; 59"
        };

        assertArrayEquals(casLikelihoodIndicator, testcasLikelihoodIndicator);
        assertArrayEquals(casScope, testcasScope);


    }
}
