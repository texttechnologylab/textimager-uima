package org.hucompute.textimager.uima.splitter;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.api.resources.CompressionMethod;
import org.dkpro.core.io.xmi.XmiReader;
import org.junit.Test;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SplitterTest {
    @Test
    public void test() throws UIMAException, IOException {
        CollectionReader reader = CollectionReaderFactory.createReader(XmiReader.class,
                XmiReader.PARAM_SOURCE_LOCATION, "/resources/corpora/ducc_tests/hansard_extracts/out_spacy3_supar_steps",
                XmiReader.PARAM_PATTERNS, "**/*.xmi.gz",
                XmiReader.PARAM_LANGUAGE, "en",
                XmiReader.PARAM_LENIENT, true
        );

        AggregateBuilder builder = new AggregateBuilder();

        builder.add(createEngineDescription(Splitter.class,
                Splitter.PARAM_TARGET_LOCATION, "/resources/corpora/ducc_tests/hansard_extracts/out",
                Splitter.PARAM_COMPRESSION, CompressionMethod.GZIP
        ));

        SimplePipeline.runPipeline(reader, builder.createAggregate());
    }
}
