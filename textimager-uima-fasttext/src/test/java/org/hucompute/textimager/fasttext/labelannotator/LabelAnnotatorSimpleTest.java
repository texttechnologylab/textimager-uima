package org.hucompute.textimager.fasttext.labelannotator;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;


public class LabelAnnotatorSimpleTest {

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
