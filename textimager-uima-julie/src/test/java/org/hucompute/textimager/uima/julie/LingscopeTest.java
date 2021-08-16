import de.julielab.jcore.types.LikelihoodIndicator;
import de.julielab.jcore.types.Scope;
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
    @Test
    public void lingscopeTest() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("The patient denied leg pain but complained about a headache.");
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(Lingscope.class, Lingscope.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(Lingscope.class, Lingscope.PARAM_DOCKER_REGISTRY, "localhost:5000",
                Lingscope.PARAM_DOCKER_NETWORK, "bridge",
                Lingscope.PARAM_DOCKER_HOSTNAME, "localhost",
                Lingscope.PARAM_DOCKER_HOST_PORT, 8000);
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
