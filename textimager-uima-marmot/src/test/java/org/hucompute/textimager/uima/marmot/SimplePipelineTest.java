package org.hucompute.textimager.uima.marmot;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.dkpro.core.testing.AssertAnnotations.assertPOS;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class SimplePipelineTest {

   @Test
    public void myPipelineTest() throws UIMAException {

       JCas cas = JCasFactory.createText("Das ist ein guter Test.", "de");

       AggregateBuilder builder = new AggregateBuilder();
       builder.add(createEngineDescription(LanguageToolSegmenter.class));
       builder.add(createEngineDescription(
               MarMoTLemma.class,
               MarMoTLemma.PARAM_VARIANT,"hucompute"
       ));
       SimplePipeline.runPipeline(cas,builder.createAggregate());

       for (Lemma lemma : JCasUtil.select(cas,Lemma.class)) { System.out.println(lemma.getCoveredText()); }
       System.out.println("Finish");

    }
}
