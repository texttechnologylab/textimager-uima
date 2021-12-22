package org.hucompute.textimager.uima.local;

import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.api.resources.CompressionMethod;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.io.xmi.XmiReader;
import org.dkpro.core.io.xmi.XmiWriter;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SimpleLocalPipeline {
    public static void main(String[] args) throws UIMAException, IOException {
        if (args.length != 5) {
            System.out.println("Usage:");
            System.out.println("  fileType language inputDir outputDir dockerPort");
            System.exit(1);
        }

        String fileType = args[0];                      // xmi, txt, ...
        String language = args[1];                      // de, en, ...
        Path inputDir = Paths.get(args[2]);             // path
        Path outputDir = Paths.get(args[3]);            // path
        int dockerPort = Integer.parseInt(args[4]);     // 8462

        CollectionReader reader = null;
        if (fileType.equalsIgnoreCase("xmi")) {
            reader = CollectionReaderFactory.createReader(
                    XmiReader.class
                    , XmiReader.PARAM_SOURCE_LOCATION, inputDir.toString()
                    , XmiReader.PARAM_PATTERNS, "**/*.xmi*"
                    , XmiReader.PARAM_LENIENT, false
                    , XmiReader.PARAM_ADD_DOCUMENT_METADATA, false
                    , XmiReader.PARAM_OVERRIDE_DOCUMENT_METADATA, false
                    , XmiReader.PARAM_MERGE_TYPE_SYSTEM, false
                    , XmiReader.PARAM_USE_DEFAULT_EXCLUDES, true
                    , XmiReader.PARAM_INCLUDE_HIDDEN, false
                    , XmiReader.PARAM_LOG_FREQ, 1
            );
        } else if (fileType.equalsIgnoreCase("txt")) {
            reader = CollectionReaderFactory.createReader(
                    TextReader.class
                    , TextReader.PARAM_TARGET_LOCATION, inputDir.toString()
                    , TextReader.PARAM_SOURCE_ENCODING, "UTF-8"
                    , XmiReader.PARAM_USE_DEFAULT_EXCLUDES, true
                    , XmiReader.PARAM_INCLUDE_HIDDEN, false
                    , XmiReader.PARAM_LOG_FREQ, 1
                    , XmiReader.PARAM_LANGUAGE, language
            );
        }

        AnalysisEngineDescription writer = createEngineDescription(
                XmiWriter.class
                , XmiWriter.PARAM_TARGET_LOCATION, outputDir.toString()
                , XmiWriter.PARAM_VERSION, "1.1"
                , XmiWriter.PARAM_COMPRESSION, CompressionMethod.GZIP
                , XmiWriter.PARAM_PRETTY_PRINT, true
        );

        AnalysisEngineDescription spacyMulti = createEngineDescription(SpaCyMultiTagger3.class,
                SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, dockerPort,
                SpaCyMultiTagger3.PARAM_DOCKER_REGISTRY, "141.2.89.20:5000"
        );

        assert reader != null;
        SimplePipeline.runPipeline(reader, spacyMulti, writer);
    }
}