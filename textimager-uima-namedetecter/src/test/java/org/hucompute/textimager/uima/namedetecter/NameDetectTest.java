package org.hucompute.textimager.uima.namedetecter;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;
import org.texttechnologylab.utilities.helper.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

public class NameDetectTest {
    @Test
    public void TimeTest() throws UIMAException, IOException {

        String testFile = NameDetectTest.class.getClassLoader().getResource("1000.txt").getPath();
//        String testFile = NameDetectTest.class.getClassLoader().getResource("City.txt").getPath();
//        String testFile = NameDetectTest.class.getClassLoader().getResource("Small.txt").getPath();

        JCas cas = JCasFactory.createText(FileUtils.getContentFromFile(new File(testFile)), "de");

        AnalysisEngineDescription nameDetector = createEngineDescription(NameDetect.class,
                NameDetect.PARAM_REST_ENDPOINT, "http://rawindra.hucompute.org:8102"
        );
//        AnalysisEngineDescription nameDetector = createEngineDescription(NameDetect.class,
//                NameDetect.PARAM_DOCKER_HOST_PORT, 8000
//        );

        long lStart = System.currentTimeMillis();

        SimplePipeline.runPipeline(cas, nameDetector);

        long lEnd = System.currentTimeMillis();

        System.out.println(XmlFormatter.getPrettyString(cas));

        System.out.println((lEnd - lStart) / 60000);

        System.out.println("End");
    }


    @Test
    public void NameTest() throws UIMAException {

        JCas cas = JCasFactory.createText("Germany location Angela", "en");

        Token t1 = new Token(cas, 0, 7);
        t1.addToIndexes();
        Token t2 = new Token(cas, 8, 16);
        t2.addToIndexes();
        Token t3 = new Token(cas, 17, 23);
        t3.addToIndexes();

//        AnalysisEngineDescription nameDetector = createEngineDescription(NameDetect.class,
//                NameDetect.PARAM_REST_ENDPOINT, "http://rawindra.hucompute.org:8102"
//        );

        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(NamedEntity::getValue).toArray(String[]::new);

        System.out.println(XmlFormatter.getPrettyString(cas));

        System.out.println((lEnd - lStart) % 60000);

        assertArrayEquals(ents, casEnts);
    }

    @Test
    public void NameTest2() throws UIMAException {

        JCas cas = JCasFactory.createText("Germany location Angela Merkel", "en");

        Token t1 = new Token(cas, 0, 7);
        t1.addToIndexes();
        Token t2 = new Token(cas, 8, 16);
        t2.addToIndexes();
        Token t3 = new Token(cas, 17, 30);
        t3.addToIndexes();

        AnalysisEngineDescription nameDetector = createEngineDescription(NameDetect.class,
                NameDetect.PARAM_DOCKER_HOST_PORT, 8000
        );
        SimplePipeline.runPipeline(cas, nameDetector);

        String[] ents = new String[] {"ORG", "LOC", null};
        String[] casEnts = JCasUtil.select(cas, NamedEntity.class).stream().map(NamedEntity::getValue).toArray(String[]::new);

        System.out.println(XmlFormatter.getPrettyString(cas));

        assertArrayEquals(ents, casEnts);
    }
}
