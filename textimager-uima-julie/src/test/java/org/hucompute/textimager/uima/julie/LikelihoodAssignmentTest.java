package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.ConceptMention;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;


import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
/**
 * LikelihoodAssignment
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide LikelihoodAssignment test case */
public class LikelihoodAssignmentTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    @Test
    public void likelihoodAssignmentTest() throws IOException, UIMAException {

        JCas jCas = JCasFactory.createText("Mutational p53 oncoprotein may likely block apoptosis in adenocarcinoma.");
        jCas.setDocumentLanguage("en");

        //test zwecke
        //AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //SimplePipeline.runPipeline(jCas, segmenter);

        //AnalysisEngineDescription engine = createEngineDescription(LikelihoodAssignment.class);
        AnalysisEngineDescription engine = createEngineDescription(LikelihoodAssignment.class);
        SimplePipeline.runPipeline(jCas, engine);

        String[] casConceptMention = (String[]) JCasUtil.select(jCas, ConceptMention.class).stream().map(a -> a.getCoveredText()+","+a.getLikelihood().getCoveredText()).toArray(String[]::new);


        String[] testcasConceptMention= new String[] {
                "p53","may","moderate","block","may","moderate;"
        };



    }
}
