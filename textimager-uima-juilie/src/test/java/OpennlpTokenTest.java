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

public class OpennlpTokenTest {
    @Test
    public void testEN() throws IOException, UIMAException {
        String Text = "CD44, at any stage, is a XYZ";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        AnalysisEngineDescription engine = createEngineDescription(OpennlpToken.class, OpennlpToken.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casToken = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getBegin() + "-" + a.getEnd()).toArray(String[]::new);
        String[] testtoken = new String[] {"0-4","4-5","6-8","9-12","13-18","18-19","20-22","23-24","25-28"};

        assertArrayEquals(testtoken, casToken);

    }
    @Test
    public void testDE() throws IOException, UIMAException {
        String Text = "CD44 ist in jedem Stadium ein XYZ";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("de");

        AnalysisEngineDescription engine = createEngineDescription(OpennlpToken.class, OpennlpToken.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casToken = (String[]) JCasUtil.select(jCas, Token.class).stream().map(a -> a.getBegin() + "-" + a.getEnd()).toArray(String[]::new);
        String[] testtoken = new String[] {"0-4","5-8","9-11","12-17","18-25","26-29","30-33"};

        assertArrayEquals(testtoken, casToken);

    }
}
