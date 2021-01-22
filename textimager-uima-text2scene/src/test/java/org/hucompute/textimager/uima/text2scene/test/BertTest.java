package org.hucompute.textimager.uima.text2scene.test;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.stanfordnlp.StanfordPosTagger;
import org.hucompute.textimager.uima.text2scene.BertProcessor;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class BertTest {

    @BeforeClass
    public static void setUpClass() throws ResourceInitializationException {
    }

    @Test
    public void test() throws UIMAException {
        String text = "One morning, when Gregor Samsa woke from troubled dreams, he found himself transformed in his bed into a horrible vermin. He lay on his armour-like back, and if he lifted his head a little he could see his brown belly, slightly domed and divided by arches into stiff sections. The bedding was hardly able to cover it and seemed ready to slide off any moment. His many legs, pitifully thin compared with the size of the rest of him, waved about helplessly as he looked.";
        JCas jCas = JCasFactory.createText(text);

        //int last_index = 0;
        //int index = text.indexOf(".");
        AggregateBuilder builder = new AggregateBuilder();
        // Tokenizer - tokenize the input
        builder.add(createEngineDescription(CoreNlpSegmenter.class));
        // Part-Of-Speech Tagger - tag the tokens for their type
        builder.add(createEngineDescription(StanfordPosTagger.class));
        // Classifier
        builder.add(createEngineDescription(BertProcessor.class));

        AnalysisEngine engine = AnalysisEngineFactory.createEngine(BertProcessor.class, BertProcessor.PARAM_LANGUAGE, "en");

        SimplePipeline.runPipeline(jCas,engine);

        System.out.println(XmlFormatter.getPrettyString(jCas.getCas()));
    }
}
