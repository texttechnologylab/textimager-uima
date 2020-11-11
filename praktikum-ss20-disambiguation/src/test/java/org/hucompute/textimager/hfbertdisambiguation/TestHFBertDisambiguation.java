package org.hucompute.textimager.hfbertdisambiguation;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.stanfordnlp.StanfordPosTagger;
import org.hucompute.textimager.uima.util.XmlFormatter;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class TestHFBertDisambiguation {

    public static void main(String[] args) throws UIMAException {

        JCas cas = JCasFactory.createText("This is a test by Barack Obama. And this is another Test by the greatest president of the US - Donald Trump", "en");

        AggregateBuilder builder = new AggregateBuilder();
        builder.add(createEngineDescription(CoreNlpSegmenter.class));
        builder.add(createEngineDescription(StanfordPosTagger.class));
        builder.add(createEngineDescription(HFBertDisambiguation.class));
        SimplePipeline.runPipeline(cas, builder.createAggregate());

        System.out.println(XmlFormatter.getPrettyString(cas.getCas()));

    }
}