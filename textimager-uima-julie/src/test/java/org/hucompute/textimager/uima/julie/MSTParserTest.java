import de.julielab.jcore.types.DependencyRelation;
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
 * MSTParser
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide MSTParser test case */
public class MSTParserTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void mstParserTest() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("Triptonide acts as a novel antiprostate cancer agent.");
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(MSTParser.class, MSTParser.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(MSTParser.class, MSTParser.PARAM_DOCKER_REGISTRY, "localhost:5000",
                MSTParser.PARAM_DOCKER_NETWORK, "bridge",
                MSTParser.PARAM_DOCKER_HOSTNAME, "localhost",
                MSTParser.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casLabel = (String[]) JCasUtil.select(jCas, DependencyRelation.class).stream().map(a -> a.getLabel()).toArray(String[]::new);


        String[] testcasLabel= new String[] {
                "SBJ", null, "ADV", "NMOD", "NMOD", "NMOD", "NMOD", "PMOD","P"
        };


        assertArrayEquals(casLabel, testcasLabel);


    }
}
