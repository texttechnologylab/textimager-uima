package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.ConceptMention;
import de.julielab.jcore.types.LikelihoodIndicator;
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
    public void init_input(JCas jcas,  int[] like_begin, int[] like_end, String[] like_cat) {
        for (int i = 0; i < like_begin.length; i++) {
            LikelihoodIndicator likelihood_indicator = new LikelihoodIndicator(jcas, like_begin[i], like_end[i]);
            likelihood_indicator.setLikelihood(like_cat[i]);
            likelihood_indicator.addToIndexes();
        }
    }
    @Test
    public void likelihoodAssignmentTest() throws IOException, UIMAException {
        // parameters
        String Text = "Mutational p53 oncoprotein may likely block apoptosis in adenocarcinoma.";
        int[] Likelihood_begin = {27, 31};
        int[] Likelihood_end = {30, 37};
        String[] Likelihood_category = {"moderate", "high"};

        JCas jCas = JCasFactory.createText(Text);
        jCas.setDocumentLanguage("en");

        // input: de.julielab.jcore.types.LikelihoodIndicator
        init_input(jCas, Likelihood_begin, Likelihood_end, Likelihood_category);

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
