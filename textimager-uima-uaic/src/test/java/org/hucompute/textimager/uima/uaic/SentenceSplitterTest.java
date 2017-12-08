package org.hucompute.textimager.uima.uaic;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;

/**
 * Test the romanian sentence splitter
 *
 * @author Dinu Ganea
 */
public class SentenceSplitterTest {

    @Test
    public void testSentenceSplitter() throws UIMAException {
        String testText = "" +
                "A fost odată ca-n poveşti,\n" +
                "A fost ca niciodată.\n" +
                "Din rude mari împărăteşti,\n" +
                "O prea frumoasă fată.";

        // 1st level is the begin idx on the sentence, 2en level - it's ending position.
        int[][] sentenceBoundaries = new int[][] {
                {0, 47}, {47, 96}
        };

        AnalysisEngineDescription sentenceSplitter = createEngineDescription(SentenceSplitter.class);

        JCas inputCas = JCasFactory.createJCas();
        inputCas.setDocumentText(testText);

        SimplePipeline.runPipeline(inputCas, sentenceSplitter);

        int sentences = 0;
        for (Sentence sentence : select(inputCas, Sentence.class)) {
            assertArrayEquals(sentenceBoundaries[sentences++], new int[]{sentence.getBegin(), sentence.getEnd()});
        }
    }

}