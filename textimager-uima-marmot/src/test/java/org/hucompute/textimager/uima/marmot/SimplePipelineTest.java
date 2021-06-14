package org.hucompute.textimager.uima.marmot;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.dkpro.core.testing.AssertAnnotations.assertPOS;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class SimplePipelineTest {

   @Test
    public void myPipelineTest() throws UIMAException {

        AggregateBuilder pipeline = new AggregateBuilder();

        //AnalysisEngine pip =  pipeline.createAggregate();

        pipeline.add(createEngineDescription(MarMoTLemma.class));

        AggregateBuilder builder = new AggregateBuilder();

        JCas testCas = JCasFactory.createText("Dies ist ein deutscher Test.", "de");

        SimplePipeline.runPipeline(testCas, builder.createAggregate());

        JCasUtil.selectAll(testCas).forEach(a->{
            System.out.println(a);
        });

       System.out.println("Finish");

    }
}
