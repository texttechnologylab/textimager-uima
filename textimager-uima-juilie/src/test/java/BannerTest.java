import de.julielab.jcore.types.Abbreviation;
import de.julielab.jcore.types.Gene;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BannerTest {

    @Test
    public void testProcess() throws UIMAException {


        JCas jCas = JCasFactory.createText("Ten out-patients with pustulosis palmaris et plantaris were examined with direct immunofluorescence (IF) technique for deposition of fibrinogen, fibrin or its degradation products (FR-antigen) in affected and unaffected skin, together with heparin-precipitable fraction (HPF), cryoglobulin and total plasma fibrinogen in the blood.");
        jCas.setDocumentLanguage("de");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        AnalysisEngineDescription engine = createEngineDescription(Banner.class, Banner.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        //String[] Gene = {"fibrinogen;fibrin;FR-antigen;cryoglobulin;fibrinogen;"};
        String[] Gene = new String[] {
                "fibrinogen", "fibrin", "FR-antigen", "cryoglobulin", "fibrinogen"
        };
        String[] casGene = (String[]) JCasUtil.select(jCas, de.julielab.jcore.types.Gene.class).stream().map(a -> a.getCoveredText()).toArray(String[]::new);


            //compare the result
            assertEquals(Gene, casGene);
        }

}
