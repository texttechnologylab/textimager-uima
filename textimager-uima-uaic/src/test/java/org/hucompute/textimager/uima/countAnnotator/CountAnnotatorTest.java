package org.hucompute.textimager.uima.countAnnotator;

import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolLemmatizer;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.tudarmstadt.ukp.dkpro.core.stanfordnlp.StanfordPosTagger;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hamcrest.Matchers;
import org.hucompute.textimager.uima.countAnnotator.type.CountAnnotation;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.*;

public class CountAnnotatorTest {
    @Test
    public void process() throws Exception {

        //Segmentierer. Fuegt Token und Sentece Ebene hinzu.
        AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
        //Pos-Tagger. Fuegt POS-Tag Ebene hinzu
        AnalysisEngineDescription pos = createEngineDescription(StanfordPosTagger.class);
        //Lemmatisierer. Fuegt Lemma Ebene hinzu.
        AnalysisEngineDescription lemmatizer = createEngineDescription(LanguageToolLemmatizer.class);


        //Ihr Annotator, der die statistischen Auswertungen dem XMI-Cas Objekt hinzufuegen soll.
        AnalysisEngineDescription countAnnotator = createEngineDescription(CountAnnotator.class);

        //JCas ist quasi die "Holder"-Klasse fuer die Annotationsebenen.
        JCas inputCas = JCasFactory.createJCas();

        //Die Eingabesprache
        inputCas.setDocumentLanguage("de");

        //Der Eingabetext.
        //inputCas.setDocumentText("Das ist ein simpler Test sind.");

        //File als eingabe.
        inputCas.setDocumentText("Sind ist");

        //Die pipeline. Die JCas wird von Annotator zu Annotator gereicht, und jeweils mit Ebenen
        //angereichert. Jeder Annotator kann auf die Annotationsebenen der Annotatoren zugreifen,
        //die zuvor in der Pipeline ausgefuert wurden.
        SimplePipeline.runPipeline(inputCas, segmenter, pos, lemmatizer, countAnnotator);

        Map<String, Integer> expectedResults = new HashMap<>();
        expectedResults.put("Word Form (Sind)", 1);
        expectedResults.put("Word Form (ist)", 1);
        expectedResults.put("Lemma (sein)", 2);


        Map<String, Integer> actualResults = new HashMap<>();
        JCasUtil.select(inputCas, CountAnnotation.class).forEach(new Consumer<CountAnnotation>() {
            @Override
            public void accept(CountAnnotation countAnnotation) {
                actualResults.put(countAnnotation.getValue(), countAnnotation.getCount());
            }
        });

        assertThat(expectedResults, Matchers.is(actualResults));

    }

}