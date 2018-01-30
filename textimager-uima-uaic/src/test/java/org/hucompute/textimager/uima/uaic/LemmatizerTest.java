package org.hucompute.textimager.uima.uaic;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.*;

/**
 * Test the romanian tokenizer
 *
 * @author Dinu Ganea
 */
public class LemmatizerTest {

    @Test
    public void testLematizer() throws UIMAException {
        String testText = "Limba română are proveniență latină orientală";

        // The correct output
        String[] lemmas = new String[]{
            "limba", "român", "avea", "proveniență", "latină", "oriental"
        };

        AnalysisEngineDescription tokenAnnotator = createEngineDescription(Lemmatizer.class);

        JCas inputCas = JCasFactory.createJCas();
        inputCas.setDocumentText(testText);

        SimplePipeline.runPipeline(inputCas, tokenAnnotator);

        int i = 0;
        for (Lemma lemma : select(inputCas, Lemma.class)) {
            assertEquals(lemmas[i++], lemma.getValue());
        }
    }

}