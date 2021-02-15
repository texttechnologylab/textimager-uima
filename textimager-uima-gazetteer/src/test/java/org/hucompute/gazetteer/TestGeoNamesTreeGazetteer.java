package org.hucompute.gazetteer;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.impl.XmiSerializationSharedData;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;
import org.hucompute.textimager.uima.tree.neo4j.StringTreeGazetteerModel;
import org.junit.Test;
import org.texttechnologylab.annotation.GeoNamesEntity;
import org.texttechnologylab.annotation.type.Taxon;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TestGeoNamesTreeGazetteer {

    private String sourceLocation = "/media/gabrami/85ff0921-743b-48ce-8962-07a08a9db03e/Arbeit/geonames/test.txt";

    @Test
    public void testRegularGazetteer() {
        try {
            final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
                    GeoNamesTreeGazetteer.class,
                    GeoNamesTreeGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
                    GeoNamesTreeGazetteer.PARAM_USE_LOWERCASE, false));

            runTest(gazetterEngine);
        } catch (UIMAException e) {
            e.printStackTrace();
        }

    }

    private void runTest(AnalysisEngine gazetterEngine) throws UIMAException {

        JCas jCas = JCasFactory.createText("Hamburg liegt im Norden von Deutschland und München im Süden.");
        StopWatch stopWatch = StopWatch.createStarted();
        SimplePipeline.runPipeline(jCas, gazetterEngine);
        System.out.printf("Finished tagging in %dms.\n", stopWatch.getTime(TimeUnit.MILLISECONDS));

        System.out.printf("Found %d GeoNames.\n", JCasUtil.select(jCas, GeoNamesEntity.class).size());
        System.out.println(JCasUtil.select(jCas, GeoNamesEntity.class).stream().map(element -> String.format("%s@(%d, %d): %s", element.getCoveredText(), element.getBegin(), element.getEnd(), element.getId())).collect(Collectors.joining("\n")));

    }

}
