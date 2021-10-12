package org.hucompute.textimager.uima.util;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.io.xmi.XmiWriter;
import org.junit.jupiter.api.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class WriterTest {

    @Test
    public void checkWriter() throws UIMAException {

        String sTest = "Dies ist ein kleiner Test.";

        JCas pCas = JCasFactory.createText(sTest, "de");

        DocumentMetaData dmd = DocumentMetaData.create(pCas);
        dmd.setDocumentTitle("Test");
        dmd.setDocumentId("0");

        AggregateBuilder pipeline = new AggregateBuilder();


        pipeline.add(createEngineDescription(
                XmiWriter.class
                ,
                XmiWriter.PARAM_TARGET_LOCATION, "/tmp/test.xmi"
        ));
        SimplePipeline.runPipeline(pCas, pipeline.createAggregate());


    }

}
