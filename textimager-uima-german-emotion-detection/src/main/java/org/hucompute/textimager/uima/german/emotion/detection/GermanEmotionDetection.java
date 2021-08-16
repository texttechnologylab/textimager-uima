package org.hucompute.textimager.uima.german.emotion.detection;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.hucompute.textimager.uima.base.TextImagerBaseAnnotator;

import java.io.File;

public class GermanEmotionDetection extends TextImagerBaseAnnotator {
    /**
     *
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION)
    protected File[] modelLoaction;

    @Override
    protected String getAnnotatorVersion() {
        return "0.1";
    }

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);

        // load lists
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        // check lists
        //for () {
        //    addAnnotatorComment(aJCas, annotation);
        //}
    }
}
