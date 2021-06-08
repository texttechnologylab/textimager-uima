import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.Sentiment;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

/**
 * Jbsd
 *
 * @date 04.06.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide several test cases for different languages and inputs types*/

public class jbsdTest {
    @Test
    public void basicTestEn() throws UIMAException {

        //JCas jCas = JCasFactory.createText("This is a simple test sentence to test this tool. Using a second sentence.","en");
        JCas jCas = null;
        try {
            jCas = JCasFactory.createText(FileUtils.readFileToString(new File("src/main/resources/test.txt"),"UTF-8"),"en");
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnalysisEngineDescription jbsdEngine = createEngineDescription(Jbsd.class, Jbsd.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, jbsdEngine);

        int[][] sentence = new int[][] {
                new int[] { 0, 49 },
                new int[] { 50, 74 }
        };
        int[][] casSentence = (int[][]) JCasUtil.select(jCas, Sentiment.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

        String[] senText = new String[] {
                "This is a simple test sentence to test this tool.", "Using a second sentence."
        };
        String[] casSenTex = (String[]) JCasUtil.select(jCas, Sentiment.class).stream().map(s -> s.getCoveredText()).toArray(String[]::new);

        Assert.assertTrue(2 == JCasUtil.select(jCas, Sentiment.class).size() );
        assertArrayEquals(sentence, casSentence);
        assertArrayEquals(senText, casSenTex);
    }

    @Test
    public void basicTestDe() throws UIMAException {

        JCas jCas = JCasFactory.createText("Das ist erste Satz. Das ist zweite Satz.",
                "de");

        AnalysisEngineDescription jbsdEngine = createEngineDescription(Jbsd.class, Jbsd.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, jbsdEngine);

        int[][] sentence = new int[][] {
                new int[] { 0, 19 },
                new int[] { 20, 40 }
        };
        int[][] casSentence = (int[][]) JCasUtil.select(jCas, Sentiment.class).stream().map(s -> new int[] { s.getBegin(), s.getEnd() }).toArray(int[][]::new);

        String[] senText = new String[] {
                "Das ist erste Satz.", "Das ist zweite Satz."
        };
        String[] casSenTex = (String[]) JCasUtil.select(jCas, Sentiment.class).stream().map(s -> s.getCoveredText()).toArray(String[]::new);

        Assert.assertTrue(2 == JCasUtil.select(jCas, Sentiment.class).size() );
        assertArrayEquals(sentence, casSentence);
        assertArrayEquals(senText, casSenTex);
    }
}
