package org.hucompute.textimager.flairdisambiguation;

import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.stanfordnlp.StanfordPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.hucompute.textimager.uima.util.XmlFormatter;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;

/**
 * <p>This class implements a test for the flair disambiguation interface class.</\p>
 *
 * @author Tim Rosenkranz
 *
 */

public class TestFlairDisambiguation {
    /**
     * Initialized the python conda environment, temp Folder and the interface with the python script.
     *
     * @param args Standard main arguments
     * @throws UIMAException if the Builder / Pipeline fails
     */
    public static void main(String[] args) throws UIMAException {

        JCas cas = JCasFactory.createText("This is a test by Barrack Obama.","en");

        AggregateBuilder builder = new AggregateBuilder();
        // Tokenizer - tokenize the input
        builder.add(createEngineDescription(CoreNlpSegmenter.class));
        // Part-Of-Speech Tagger - tag the tokens for their type
        builder.add(createEngineDescription(StanfordPosTagger.class));
        // Classifier
        builder.add(createEngineDescription(FlairDisambiguation.class));
        SimplePipeline.runPipeline(cas,builder.createAggregate());

        System.out.println(XmlFormatter.getPrettyString(cas.getCas()));

    }
}