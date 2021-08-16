package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.Abbreviation;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class AcronymTest {
    @Test
    public void testProcess() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("In der Bundesrepublik Deutschland(BRD). Viele Mänchen leben aber außer BRD." +
                "Sozialdemokratische Partei Deutschland (SPD) und Christlich Demokratische Union Deutschlands(CDU) gehören zum grösten Parteien im Deutschland. Angela Merkel gehört zu CDU.");

        AnalysisEngineDescription engine = createEngineDescription(Acronym.class, Jbsd.PARAM_REST_ENDPOINT, "http://localhost:8080");

        SimplePipeline.runPipeline(jCas, engine);

        String stop = "";
    }
}
