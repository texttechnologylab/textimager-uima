package org.hucompute.textimager.uima.julie;

import org.apache.uima.UIMAException;
import org.junit.Test;
import java.io.IOException;

/**
 * Banner
 *
 * @date 13.08.2021
 *
 * @author Grzegorz Siwiecki, Chieh Kang
 * @version 1.1
 *
 * This class provide banner test case */
public class BannerTest {
    /**
     * Test for simple text.
     * @throws UIMAException
     */
    String Text = "Ten out-patients with pustulosis palmaris et plantaris were examined with direct immunofluorescence (IF) technique for deposition of fibrinogen, fibrin or its degradation products (FR-antigen) in affected and unaffected skin, together with heparin-precipitable fraction (HPF), cryoglobulin and total plasma fibrinogen in the blood.";
    @Test
    public void testProcess() throws IOException, UIMAException {
        /*JCas jCas = JCasFactory.createText(Text);
        AnalysisEngineDescription engine = createEngineDescription(Banner.class);

        SimplePipeline.runPipeline(jCas, engine);*/

        //String stop = "";
    }
}