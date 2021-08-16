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
 * OpennlpPostag
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide OpennlpPostag test case */
public class OpennlpPostagTest {
    /**
     * Test for simple english text.
     * @throws UIMAException
     */
    @Test
    public void opennlpPOSTestEN() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("A study on the Prethcamide.");
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class, OpennlpPostag.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class, OpennlpPostag.PARAM_DOCKER_REGISTRY, "localhost:5000",
                Jbsd.PARAM_DOCKER_NETWORK, "bridge",
                Jbsd.PARAM_DOCKER_HOSTNAME, "localhost",
                Jbsd.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);

        String[] casPostagDkpro = (String[]) JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class).stream().map(a -> a.getPosValue()).toArray(String[]::new);


        String[] testPos= new String[] {
                "DT","NN","IN","DT","NN","."
        };

        assertArrayEquals(casPostag, casPostagDkpro);
        assertArrayEquals(testPos, casPostag);

    }
    /**
     * Test for simple german text.
     * @throws UIMAException
     */
    @Test
    public void opennlpPOSTestDE() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("Kleinere hilusnahe Fistelungen werden mit Tacho-Comb-Vlies abgedeckt.");
        jCas.setDocumentLanguage("de");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class, OpennlpPostag.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(OpennlpPostag.class, OpennlpPostag.PARAM_DOCKER_REGISTRY, "localhost:5000",
                OpennlpPostag.PARAM_DOCKER_NETWORK, "bridge",
                OpennlpPostag.PARAM_DOCKER_HOSTNAME, "localhost",
                OpennlpPostag.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);

        String[] casPostagDkpro = (String[]) JCasUtil.select(jCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class).stream().map(a -> a.getPosValue()).toArray(String[]::new);


        String[] testPos= new String[] {
                "ADJA","ADJD","NN","VAFIN","APPR","NN", "VVPP", "$."
        };

        assertArrayEquals(casPostag, casPostagDkpro);
        assertArrayEquals(testPos, casPostag);

    }
}
