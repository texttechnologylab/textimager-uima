package org.hucompute.textimager.fasttext.labelannotator;

import marmot.morph.MorphTagger;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.ModelProviderBase;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.marmot.ModelProviderTwo;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SimpleTest {
    private ModelProviderBase<String> modelProvider;

    @Test
    public void testDdcDe() throws UIMAException {
        JCas cas = JCasFactory.createText("Das ist einfach nur ein Test.");
        cas.setDocumentLanguage("any");

        AggregateBuilder builder = new AggregateBuilder();

        builder.add(createEngineDescription(LanguageToolSegmenter.class));
        builder.add(createEngineDescription(LanguageToolLemmatizer.class));

        //String cpath = "classpath:/de/tudarmstadt/ukp/dkpro/core/marmot/lib/lemma-de-hucompute.marmot";
        //URL fileUrl = getClass().getResource("classpath:/de/tudarmstadt/ukp/dkpro/core/marmot/lib/lemma-de-hucompute.marmot");

        modelProvider= new ModelProviderTwo(this,"test","languageidentification");
        modelProvider.configure(cas.getCas());
        modelProvider.getResource();

        JCas cas2 = JCasFactory.createText("Das ist einfach nur ein Test.");
        cas2.setDocumentLanguage("en");

        modelProvider.configure(cas2.getCas());
        modelProvider.getResource();

        modelProvider.configure(cas.getCas());
        modelProvider.getResource();

        //get classpath of model file
        builder.add(createEngineDescription(LabelAnnotator.class,
                LabelAnnotator.PARAM_FASTTEXT_LOCATION, "C:/files/fasttext",
                LabelAnnotator.PARAM_APPEND_DDC_VARIANT,"small",
                LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "de, C:/files/wiki.bin,98",
                LabelAnnotator.PARAM_FASTTEXT_K, 100,
                LabelAnnotator.PARAM_CUTOFF, false,
                LabelAnnotator.PARAM_SELECTION, "text",
                LabelAnnotator.PARAM_TAGS, "ddc2",
                LabelAnnotator.PARAM_USE_LEMMA, true,
                LabelAnnotator.PARAM_ADD_POS, false,
                LabelAnnotator.PARAM_POSMAP_LOCATION, "C:\\files\\am_posmap.txt",
                LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true,
                LabelAnnotator.PARAM_REMOVE_PUNCT, true
        ));

        SimplePipeline.runPipeline(cas, builder.createAggregate());

        for (CategoryCoveredTagged category : JCasUtil.select(cas, CategoryCoveredTagged.class)) {
            System.out.println(category.getScore() + " " + category.getValue() + " " + category.getCoveredText());
        }
    }
}
