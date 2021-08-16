import de.julielab.jcore.types.LikelihoodIndicator;
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
 * LikelihoodDetection
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide LikelihoodDetection test case */
public class LikelihoodDetectionTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void likelihoodDetectionTest() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("PML appears to be transcriptionally regulated by class I and II interferons , which raises the possibility that interferons modulate the function and growth and differentiation potential of normal myeloid cells and precursors .");
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(LikelihoodDetection.class, LikelihoodDetection.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(LikelihoodDetection.class, LikelihoodDetection.PARAM_DOCKER_REGISTRY, "localhost:5000",
                LikelihoodDetection.PARAM_DOCKER_NETWORK, "bridge",
                LikelihoodDetection.PARAM_DOCKER_HOSTNAME, "localhost",
                LikelihoodDetection.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casLikelihoodIndicator = (String[]) JCasUtil.select(jCas, LikelihoodIndicator.class).stream().map(a -> a.getCoveredText()).toArray(String[]::new);
        String[] casLikelihoodCategory = (String[]) JCasUtil.select(jCas, LikelihoodIndicator.class).stream().map(a -> a.getLikelihood()).toArray(String[]::new);


        String[] testcasLikelihoodIndicator= new String[] {
                "appears","raises the possibility"
        };

        String[] testcasLikelihoodCategory= new String[] {
                "moderate","moderate"
        };

        assertArrayEquals(casLikelihoodIndicator, testcasLikelihoodIndicator);

    }
}
