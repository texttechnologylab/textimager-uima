package de.unihd.dbs.uima.annotator.heideltime.biofid;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.unihd.dbs.uima.types.heideltime.Timex3;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

public class HeideltimeBioFIDTest {

        @Test
        public void simpleExample() throws UIMAException, URISyntaxException, IOException {

            File tFile = null;

            URL resource = getClass().getClassLoader().getResource("test.txt");
            if (resource == null) {
                throw new IllegalArgumentException("file not found!");
            } else {

                // failed if files have whitespaces or special characters
                //return new File(resource.getFile());

                tFile = new File(resource.toURI());
            }

            String content = FileUtils.readFileToString(tFile);

            JCas cas = JCasFactory.createText(content, "de");

            new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

            AggregateBuilder builder = new AggregateBuilder();
            builder.add(createEngineDescription(
                    HeidelTimeBioFID.class
            ));

            SimplePipeline.runPipeline(cas, builder.createAggregate());



            List<Timex3> tRel = JCasUtil.select(cas, Timex3.class).stream().collect(Collectors.toList());

            assertEquals(tRel.get(0).getBegin(), 13);
            assertEquals(tRel.get(0).getEnd(), 25);
            assertEquals(tRel.get(0).getTimexType(), "DATE");


        }

    }

