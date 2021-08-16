import de.julielab.jcore.types.Enzyme;
import de.julielab.jcore.types.Organism;
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
 * LinnaeusSpecies
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide LinnaeusSpecies test case */
public class LinnaeusSpeciesTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void testSpecies() throws IOException, UIMAException {
        String Text = "In this text we talk about humans and mice. Because a mouse is no killifish nor a caenorhabditis elegans. Thus, c. elegans is now abbreviated as well as n. furzeri .";

        JCas jCas = JCasFactory.createText(Text);

        //AnalysisEngineDescription engine = createEngineDescription(LinnaeusSpecies.class, LinnaeusSpecies.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(LinnaeusSpecies.class, LinnaeusSpecies.PARAM_DOCKER_REGISTRY, "localhost:5000",
                LinnaeusSpecies.PARAM_DOCKER_NETWORK, "bridge",
                LinnaeusSpecies.PARAM_DOCKER_HOSTNAME, "localhost",
                LinnaeusSpecies.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casSpecies = (String[]) JCasUtil.select(jCas, Organism.class).stream().map(a -> a.getCoveredText()).toArray(String[]::new);
        //String[] casID = (String[]) JCasUtil.select(jCas, Organism.class).stream().map(b -> b.getId()).toArray(String[]::new);

        String[] testSpecies = new String[] {"humans", "mice", "mouse", "killifish", "caenorhabditis elegans", "c. elegans", "n. furzeri"};
        //String[] testID = new String[] {"9606", "10090", "10090", "34780", "6239", "6239", "105023"};

        assertArrayEquals(testSpecies, casSpecies);
        //assertArrayEquals(testID, casID);
    }
//    @Test
//    public void testGeneraSpecies() throws IOException, UIMAException {
//        JCas jCas = JCasFactory.createText("Acetylesterase has number EC 3.1.1.6");
//
//        AnalysisEngineDescription engine = createEngineDescription(ECCode.class, ECCode.PARAM_REST_ENDPOINT, "http://localhost:8080");
//
//        SimplePipeline.runPipeline(jCas, engine);
//
//        String[] casEnzyme = (String[]) JCasUtil.select(jCas, Enzyme.class).stream().map(a -> a.getSpecificType()).toArray(String[]::new);
//
//        String[] testEnzyme = new String[] {"3.1.1.6"};
//
//        assertArrayEquals(testEnzyme, casEnzyme);
//    }
//    @Test
//    public void testProxiesSpecies() throws IOException, UIMAException {
//        JCas jCas = JCasFactory.createText("Acetylesterase has number EC 3.1.1.6");
//
//        AnalysisEngineDescription engine = createEngineDescription(ECCode.class, ECCode.PARAM_REST_ENDPOINT, "http://localhost:8080");
//
//        SimplePipeline.runPipeline(jCas, engine);
//
//        String[] casEnzyme = (String[]) JCasUtil.select(jCas, Enzyme.class).stream().map(a -> a.getSpecificType()).toArray(String[]::new);
//
//        String[] testEnzyme = new String[] {"3.1.1.6"};
//
//        assertArrayEquals(testEnzyme, casEnzyme);
//    }
}