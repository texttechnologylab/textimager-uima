import de.julielab.jcore.types.Constituent;
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

public class OpennlpParserTest {
    @Test
    public void testProcess() throws IOException, UIMAException {
        String Text = "A study on the Prethcamide hydroxylation system in rat hepatic microsomes .";
        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        AnalysisEngineDescription engine = createEngineDescription(OpennlpParser.class, OpennlpParser.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casParsing = (String[]) JCasUtil.select(jCas, Constituent.class).stream().map(a -> a.getCat()).toArray(String[]::new);
        String[] testParsing = {"NP", "NP", "PP", "NP", "NP", "PP", "NP"};
        assertArrayEquals(testParsing, casParsing);

    }
}
