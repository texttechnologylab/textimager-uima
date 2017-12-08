package org.hucompute.textimager.uima.uaic;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import static org.junit.Assert.*;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.apache.uima.fit.util.JCasUtil.select;

/**
 * Test the romanian tokenizer
 *
 * @author Dinu Ganea
 */
public class TokenizerTest {

    @Test
    public void testTokenizer() throws UIMAException {
        String testText = "Formarea limbii române reprezintă o permanentă sursă de controverse între lingvişti.";

        // 1st level is the begin idx on the token, 2en level - it's ending position.
        int[][] tokenBoundaries = new int[][]{
                {0, 8}, {9, 15}, {16, 22},
                {23, 33}, {34, 35}, {36, 46},
                {47, 52}, {53, 55}, {56, 67},
                {68, 73}, {74, 83}, {83, 84}
        };

        AnalysisEngineDescription tokenAnnotator = createEngineDescription(Tokenizer.class);

        JCas inputCas = JCasFactory.createJCas();
        inputCas.setDocumentText(testText);

        SimplePipeline.runPipeline(inputCas, tokenAnnotator);

        int tokens = 0;
        for (Token token : select(inputCas, Token.class)) {
            assertArrayEquals(tokenBoundaries[tokens++], new int[]{token.getBegin(), token.getEnd()});
        }
    }

}