import de.julielab.jcore.types.Sentence;
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

public class OpennlpSentenceTest {

    @Test
    public void testEN() throws IOException, UIMAException {
        String Text = "First sentence. Second sentence!";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        AnalysisEngineDescription engine = createEngineDescription(OpennlpSentence.class, OpennlpSentence.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casSentence = (String[]) JCasUtil.select(jCas, Sentence.class).stream().map(a -> a.getBegin() + "-" + a.getEnd()).toArray(String[]::new);
        String[] testSentence = new String[] {"0-15", "16-32"};

        assertArrayEquals(testSentence, casSentence);

    }

    @Test
    public void testDE() throws IOException, UIMAException {
        String Text = "Erster Satz. Zweiter Satz!";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("de");

        AnalysisEngineDescription engine = createEngineDescription(OpennlpSentence.class, OpennlpSentence.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casSentence = (String[]) JCasUtil.select(jCas, Sentence.class).stream().map(a -> a.getBegin() + "-" + a.getEnd()).toArray(String[]::new);
        String[] testSentence = new String[] {"0-12", "13-26"};

        assertArrayEquals(testSentence, casSentence);

    }
}
