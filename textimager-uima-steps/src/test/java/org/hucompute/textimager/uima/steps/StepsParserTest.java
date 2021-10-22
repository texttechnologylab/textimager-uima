package org.hucompute.textimager.uima.steps;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class StepsParserTest {
    @Test
    public void basicTest() throws UIMAException {
        JCas jCas = JCasFactory.createText("This is a simple test sentence to test this tool. Using a second sentence.",
                "en");

        AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);

        AnalysisEngineDescription stepsParser = createEngineDescription(StepsParser.class,
                StepsParser.PARAM_MODEL_NAME, "basic_mbert",
                StepsParser.PARAM_MODELS_CACHE_DIR, "/home/daniel/data/hiwi/git/myyyvothrr/steps-parser-data",
                StepsParser.PARAM_DOCKER_HOST_PORT, 8011
        );

        StepsParser.set_batch_size(jCas,22);
        SimplePipeline.runPipeline(jCas, segmenter, stepsParser);

        JCasUtil.select(jCas, Dependency.class).stream().forEach(System.out::println);

        System.out.println(XmlFormatter.getPrettyString(jCas));

//        assertArrayEquals(texts, casTexts);
    }
}
