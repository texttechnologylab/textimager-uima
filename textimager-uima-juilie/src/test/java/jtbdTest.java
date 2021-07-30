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

public class jtbdTest {
    @Test
    public void tokenizerTestEn() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("X-inactivation, T-cells and CD44 are XYZ! CD44-related stuff is\\t(not).");
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        /*AnalysisEngineDescription engine = createEngineDescription(Jtbd.class, Jtbd.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casOffset = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getBegin() + "-" + a.getEnd()).toArray(String[]::new);

        String[] testOffset = new String[] {
                "0-14","14-15","16-23","24-27","28-32","33-36","37-40","40-41","42-46","46-47","47-54","55-60","61-63","63-64","64-65","65-66","66-69","69-70","70-71"
        };

        assertArrayEquals(testOffset, casOffset);*/

    }

    @Test
    public void tokenizerTestDe() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("Das ist eine einfache Sentiment.");
        jCas.setDocumentLanguage("de");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        /*AnalysisEngineDescription engine = createEngineDescription(Jtbd.class, Jtbd.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casOffset = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getBegin() + "-" + a.getEnd()).toArray(String[]::new);

        String[] testOffset = new String[] {
                "0-3","4-7","8-12","13-21","22-31","31-32"
        };

        assertArrayEquals(testOffset, casOffset);*/

    }
}
