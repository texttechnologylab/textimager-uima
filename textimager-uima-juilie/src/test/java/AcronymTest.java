import de.julielab.jcore.types.Abbreviation;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.AnnotationImpl;
import org.apache.uima.fit.factory.AnnotationFactory;
import org.apache.uima.fit.factory.JCasBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.type.Sentiment;
import org.junit.Test;


import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.selectAll;
import static org.junit.Assert.assertArrayEquals;

public class AcronymTest {
    @Test
    public void testProcess() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("In der Bundesrepublik Deutschland(BRD). Viele Mänchen leben aber außer BRD. Christlich Demokratische Union Deutschlands(CDU) gehört zum grösten Parteien im Deutschland. Angela Merkel gehört zu CDU.");
        jCas.setDocumentLanguage("de");

        //test zwecke
        AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        SimplePipeline.runPipeline(jCas, segmenter);

        AnalysisEngineDescription engine = createEngineDescription(Acronym.class, Jbsd.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String[] casAbbreviation = (String[]) JCasUtil.select(jCas, Abbreviation.class).stream().map(a -> a.getCoveredText()).toArray(String[]::new);
        String[] casAbbreviationExpan = (String[]) JCasUtil.select(jCas, Abbreviation.class).stream().map(a -> a.getExpan()).toArray(String[]::new);

        String[] testAbbreviation = new String[] {
                "BRD", "BRD", "CDU", "CDU"
        };

        String[] testAbbreviationExpan = new String[] {
                "Bundesrepublik Deutschland", "Bundesrepublik Deutschland", "Christlich Demokratische Union Deutschlands", "Christlich Demokratische Union Deutschlands"
        };

        assertArrayEquals(testAbbreviation, casAbbreviation);
        assertArrayEquals(testAbbreviationExpan, casAbbreviationExpan);
        String stop = "";

    }
}
