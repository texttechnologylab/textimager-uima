import de.julielab.jcore.types.CoordinationElement;
import de.julielab.jcore.types.OntClassMention;
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

public class FeatureValueReplacementTest {
    String Text = "This is an arbitrary document text long enough to hold a few fake-annotations .";
    String Ontology = "entry1 entry2 entry3 entry2 somethingelse";
    String Ontology_begin = "0 0 0 0 0";
    String Ontology_end = "2 2 2 2 2";

    public void init_jcas(JCas jcas, String ontology, String ontology_begin, String ontology_end) {
        //split sentence to tokens
        String[] words = ontology.split(" ");
        String[] begin = ontology_begin.split(" ");
        String[] end = ontology_end.split(" ");

        //loop for all words
        for (int i=0; i< words.length; i++) {
            OntClassMention ocm = new OntClassMention(jcas, Integer.parseInt(begin[i]), Integer.parseInt(end[i]));
            ocm.setSourceOntology(words[i]);
            ocm.addToIndexes();
        }
    }
    @Test
    public void testProcess() throws IOException, UIMAException {
        JCas jCas = JCasFactory.createText(Text);
        init_jcas(jCas, Ontology, Ontology_begin, Ontology_end);
        AnalysisEngineDescription engine = createEngineDescription(FeatureValueReplacement.class, FeatureValueReplacement.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casFVR = (String[]) JCasUtil.select(jCas, OntClassMention.class).stream().map(a -> a.getSourceOntology()).toArray(String[]::new);

        String[] testFVR = new String[]
                {"replacement1", "replacement2", "replacement3", "replacement2", "somethingelse"};
        //String stop = "";0
        assertArrayEquals(casFVR, testFVR);
    }
}
