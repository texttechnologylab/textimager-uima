package org.hucompute.textimager.uima.supar;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.io.xmi.XmiReader;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class Test {
    public static void main(String[] args) throws UIMAException, IOException {

        CollectionReader reader = CollectionReaderFactory.createReader(
            TextReader.class
            ,XmiReader.PARAM_SOURCE_LOCATION,"/home/daniel/data/hiwi/hansard_parser/extracts"
            ,XmiReader.PARAM_PATTERNS,"**/*.csv.extracted"
            ,XmiReader.PARAM_ADD_DOCUMENT_METADATA,false
            ,XmiReader.PARAM_LANGUAGE,"en"
        );

        AnalysisEngineDescription segmenter = createEngineDescription(SpaCyMultiTagger3.class,
                SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, 8000
        );

        AnalysisEngineDescription depParser = createEngineDescription(SuparDep.class,
                SuparDep.PARAM_DOCKER_HOST_PORT, 8001,
                SuparDep.PARAM_MODEL_NAME, "biaffine-dep-en",
                SuparDep.PARAM_MODELS_CACHE_DIR, "/home/daniel/.textimager/cache"
        );
        SimplePipeline.runPipeline(reader, segmenter, depParser);
    }
}
