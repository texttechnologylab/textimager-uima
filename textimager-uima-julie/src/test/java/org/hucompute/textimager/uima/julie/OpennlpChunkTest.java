package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.POSTag;
import de.julielab.jcore.types.Sentence;
import de.julielab.jcore.types.Token;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.chunk.Chunk;
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
 * OpennlpChunk
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide OpennlpChunk test case */
public class OpennlpChunkTest {
    public void init_input(JCas jcas, String text, String postag) {
        Sentence sentence = new Sentence(jcas, 0, text.length());
        sentence.addToIndexes();
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = postag.split(" ");

        //initialize token index
        int index_start_token = 0;
        int index_end_token = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
            Token token = new Token(jcas);
            index_end_token = index_start_token + words[i].length();
            token.setBegin(index_start_token);
            token.setEnd(index_end_token);
            token.addToIndexes();
            index_start_token = index_end_token + 1;

            POSTag pos = new POSTag(jcas);
            pos.setValue(postags[i]);
            pos.addToIndexes();

            FSArray postagss = new FSArray(jcas, 5);
            postagss.set(0, pos);
            postagss.addToIndexes();
            token.setPosTag(postagss);
            System.out.println(token.getPosTag(0));
        }
    }
    public void init_input_dkpro(JCas jcas, String text, String postag) {
        de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentence = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(jcas, 0, text.length());
        sentence.addToIndexes();
        //split sentence to tokens
        String[] words = text.split(" ");
        String[] postags = postag.split(" ");

        //initialize token index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< words.length; i++) {
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

            index_start = index_end + 1;
//            FSArray postagss = new FSArray(jcas, 5);
//            postagss.set(0, pos);
//            postagss.addToIndexes();
//            token.setPosTag(postagss);
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
        String Postags = "DT NN IN DT NN NN NN IN NN JJ NNS .";
        JCas jCas = JCasFactory.createText(Text);

        // input: de.julielab.jcore.types.Sentence
        //        de.julielab.jcore.types.Token
        //        de.julielab.jcore.types.POSTag
        init_input_dkpro(jCas, Text, Postags);
        // get postag
        //AnalysisEngineDescription engine_postag = createEngineDescription(OpennlpPostag.class);
//        AnalysisEngineDescription engine_postag = createEngineDescription(OpennlpPostag.class);
//        SimplePipeline.runPipeline(jCas, engine_postag);
//
//        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);
//        // test chunk
//
//        init_jcas(jCas, casPostag);
        //AnalysisEngineDescription engine = createEngineDescription(OpennlpChunk.class);
        AnalysisEngineDescription engine = createEngineDescription(OpennlpChunk.class);
        SimplePipeline.runPipeline(jCas, engine);

//        String[] casChunks = (String[]) JCasUtil.select(jCas, Chunk.class).stream().map(b -> b.getType().getShortName()).toArray(String[]::new);
        String[] casChunks = (String[]) JCasUtil.select(jCas, Chunk.class).stream().map(b -> b.getType().getShortName()).toArray(String[]::new);
        String[] testChunks = new String[]{"ChunkNP","ChunkPP","ChunkNP","ChunkPP","ChunkNP"};

        assertArrayEquals(testChunks, casChunks);
    }
}
