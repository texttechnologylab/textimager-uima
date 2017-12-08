package org.hucompute.textimager.uima.uaic;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.*;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;
import static org.junit.Assert.assertArrayEquals;

/**
 * Test the romanian PoS recognizer
 *
 * @author Dinu Ganea
 */
public class PosTaggerTest {

    @Test
    public void testPosTagger() throws UIMAException {
        String testText = "Luceafarul este un poem romantic scris de Mihai Eminescu";

        Object[][] results = new Object[][] {
                {"Luceafarul", NN.type}, {"este", V.type}, {"un", ART.type},
                {"poem", NN.type}, {"romantic", ADJ.type}, {"scris", ADJ.type},
                {"de", PP.type}, {"Mihai Eminescu", NP.type}
        };

        //  AnalysisEngineDescription sentenceSplitter = createEngineDescription(SentenceSplitter.class);
        AnalysisEngineDescription posAnnotator = createEngineDescription(PosTagger.class, PosTagger.PARAM_POS_MAPPING_LOCATION, "src/main/resources/org/hucompute/textimager/uima/uaic/pos-default.map");

        JCas inputCas = JCasFactory.createJCas();
        inputCas.setDocumentText(testText);

        SimplePipeline.runPipeline(inputCas, posAnnotator);

        int posIdx = 0;
        for (POS pos : select(inputCas, POS.class)) {
            assertArrayEquals(results[posIdx++], new Object[]{pos.getCoveredText(), pos.getTypeIndexID()});
        }
    }

}