import de.julielab.jcore.types.Chunk;
import de.julielab.jcore.types.POSTag;
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
 * OpennlpChunk
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide OpennlpChunk test case */
public class OpennlpChunkTest {
    public void init_jcas(JCas jcas, String[] POSTAG) {
        //initialize index
        int index_start = 0;
        int index_end = 0;

        //loop for all words
        for (int i=0; i< POSTAG.length; i++) {
            index_end = index_start + POSTAG[i].length();
            POSTag pos = new POSTag(jcas);

            pos.setBegin(index_start);
            pos.setEnd(index_end);
            pos.setValue(POSTAG[i]);
            pos.addToIndexes();
            index_start = index_end + 1;
        }
    }
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void testProcess() throws IOException, UIMAException {
        String Text = "A study on the Prethcamide hydroxylation system in rat hepatic microsomes .";

        JCas jCas = JCasFactory.createText(Text);
        // get postag
        //AnalysisEngineDescription engine_postag = createEngineDescription(OpennlpPostag.class, OpennlpPostag.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine_postag = createEngineDescription(OpennlpPostag.class, OpennlpPostag.PARAM_DOCKER_REGISTRY, "localhost:5000",
                OpennlpPostag.PARAM_DOCKER_NETWORK, "bridge",
                OpennlpPostag.PARAM_DOCKER_HOSTNAME, "localhost",
                OpennlpPostag.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine_postag);

        String[] casPostag = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getPosTag(0).getValue()).toArray(String[]::new);
        // test chunk

        init_jcas(jCas, casPostag);
        //AnalysisEngineDescription engine = createEngineDescription(OpennlpChunk.class, OpennlpChunk.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(OpennlpChunk.class, OpennlpPostag.PARAM_DOCKER_REGISTRY, "localhost:5000",
                OpennlpPostag.PARAM_DOCKER_NETWORK, "bridge",
                OpennlpPostag.PARAM_DOCKER_HOSTNAME, "localhost",
                OpennlpPostag.PARAM_DOCKER_HOST_PORT, 8000);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casChunks = (String[]) JCasUtil.select(jCas, Chunk.class).stream().map(b -> b.getType().getShortName()).toArray(String[]::new);
        String[] testChunks = new String[]{"ChunkNP","ChunkPP","ChunkNP","ChunkPP","ChunkNP"};

        assertArrayEquals(testChunks, casChunks);
    }
}
