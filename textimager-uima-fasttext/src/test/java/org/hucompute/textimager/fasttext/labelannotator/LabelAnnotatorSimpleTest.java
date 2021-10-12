package org.hucompute.textimager.fasttext.labelannotator;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;


public class LabelAnnotatorSimpleTest {

	@Test
	public void testDdcDe() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist einfach nur ein Test.");
		cas.setDocumentLanguage("de");

		AggregateBuilder builder = new AggregateBuilder();

		builder.add(createEngineDescription(LanguageToolSegmenter.class));
		builder.add(createEngineDescription(LanguageToolLemmatizer.class));

		builder.add(createEngineDescription(LabelAnnotator.class,
				LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext",
				LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "de,/resources/nlp/models/categorization/ddc/ddc_2018/wikipedia_de.v8.lemma.nopunct.pos.no_functionwords_gnd_ddc.v4.with_categories-lr0.2-lrUR150-minC5-dim100-ep10000-vec_vec_token_lemmapos.vec.epoch5000.bin,98",
				LabelAnnotator.PARAM_FASTTEXT_K, 100,
				LabelAnnotator.PARAM_CUTOFF, false,
				LabelAnnotator.PARAM_SELECTION, "text",
				LabelAnnotator.PARAM_TAGS, "ddc2",
				LabelAnnotator.PARAM_USE_LEMMA, true,
				LabelAnnotator.PARAM_ADD_POS, false,
				LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt",
				LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true,
				LabelAnnotator.PARAM_REMOVE_PUNCT, true
		));

		SimplePipeline.runPipeline(cas, builder.createAggregate());

		for (CategoryCoveredTagged category : JCasUtil.select(cas, CategoryCoveredTagged.class)) {
			System.out.println(category.getScore() + " " + category.getValue() + " " + category.getCoveredText());
		}
	}

//	@Test
//	public void simpleExampleDE() throws UIMAException{
//		JCas cas = JCasFactory.createText("Am 14.12.2020 konnte ich in Frankfurt eine vielzahl an Rosenblüten beobachten.");
//		cas.setDocumentLanguage("de");
//
//		AggregateBuilder builder = new AggregateBuilder();
//
//		// Needs Token to work
//		builder.add(createEngineDescription(LanguageToolSegmenter.class));
//
//		builder.add(createEngineDescription(LabelAnnotatorSimple.class,
//				LabelAnnotatorSimple.PARAM_FASTTEXT_LOCATION, "/home/daniel/data/hiwi/git/gitlab/fastText-biofid/fasttext",
//				LabelAnnotatorSimple.PARAM_LANGUAGE_MODELS_LABELS, "de,/home/daniel/data/hiwi/1611571599_biofid.bin,26",
//				LabelAnnotatorSimple.PARAM_FASTTEXT_K, 26,
//				LabelAnnotatorSimple.PARAM_SCORE_THRESHOLD, 0.5f,
//				LabelAnnotatorSimple.PARAM_CUTOFF, false,
//				LabelAnnotatorSimple.PARAM_SELECTION, "text",
//				LabelAnnotatorSimple.PARAM_TAGS, "BiofidFastText"
//		));
//
//		SimplePipeline.runPipeline(cas, builder.createAggregate());
//
//		for (CategoryCoveredTagged category : JCasUtil.select(cas, CategoryCoveredTagged.class)) {
//			System.out.println(category.getScore() + " " + category.getValue());
//		}
//	}

}
