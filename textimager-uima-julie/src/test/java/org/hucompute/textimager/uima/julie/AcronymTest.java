package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ABBREV;
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
 * Acronym
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide acronym test case */
public class AcronymTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void testProcess() throws IOException, UIMAException {
        // Parameters
        String Text = "In der Bundesrepublik Deutschland(BRD). Viele Mänchen leben aber außer BRD. Christlich Demokratische Union Deutschlands(CDU) gehört zum grösten Parteien im Deutschland. Angela Merkel gehört zu CDU.";

        JCas jCas = JCasFactory.createText(Text);
        // Input: de.julielab.jcore.types.Sentence
        de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence sentence = new de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence(jCas, 0, Text.length());
        sentence.addToIndexes();

        jCas.setDocumentLanguage("de");


        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(Acronym.class, Acronym.PARAM_REST_ENDPOINT, "http://localhost:8080");
        AnalysisEngineDescription engine = createEngineDescription(Acronym.class);

        SimplePipeline.runPipeline(jCas, engine);

        String[] casAbbreviation = (String[]) JCasUtil.select(jCas, ABBREV.class).stream().map(a -> a.getCoveredText()).toArray(String[]::new);
//        String[] casAbbreviationExpan = (String[]) JCasUtil.select(jCas, ABBREV.class).stream().map(a -> a.getExpan()).toArray(String[]::new);

        String[] testAbbreviation = new String[] {
                "BRD", "BRD", "CDU", "CDU"
        };

        String[] testAbbreviationExpan = new String[] {
                "Bundesrepublik Deutschland", "Bundesrepublik Deutschland", "Christlich Demokratische Union Deutschlands", "Christlich Demokratische Union Deutschlands"
        };

        assertArrayEquals(testAbbreviation, casAbbreviation);
//        assertArrayEquals(testAbbreviationExpan, casAbbreviationExpan);
        String stop = "";

    }
}
