package org.hucompute.textimager.uima.biofid.flair;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.Test;
import org.texttechnologylab.annotation.NamedEntity;

import java.util.Arrays;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

public class BiofidFlairTest {
    @Test
    public void basicTest() throws UIMAException {
        JCas jCas = JCasFactory.createText("Das ist ein Test am 08.12.2020, einem Dienstag in Frankfurt.", "de");

        Sentence sentence = new Sentence(jCas, 0, jCas.getDocumentText().length());
        sentence.addToIndexes();

        AnalysisEngineDescription biodidFlairTagger = createEngineDescription(BiofidFlair.class,
                BiofidFlair.PARAM_REST_ENDPOINT, "http://localhost:5567"
        );

        SimplePipeline.runPipeline(jCas, biodidFlairTagger);

        String[] texts = new String[] { "08.12.2020", "Dienstag", "Frankfurt", "Frankfurt", "Frankfurt"};
        String[] casTexts = JCasUtil.select(jCas, NamedEntity.class)
                .stream()
                .map(Annotation::getCoveredText)
                .toArray(String[]::new);
        System.out.println(Arrays.toString(casTexts));
        assertArrayEquals(texts, casTexts);

        String[] entities = new String[] { "Time", "Time", "Society", "Artifact", "Location_Place"};
        String[] casEntities = JCasUtil.select(jCas, NamedEntity.class)
                .stream()
                .map(ne -> ne.getValue().substring(0, ne.getValue().indexOf(";")))
                .toArray(String[]::new);
        System.out.println(Arrays.toString(casEntities));
        assertArrayEquals(entities, casEntities);
    }
}
