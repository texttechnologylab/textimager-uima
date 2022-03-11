package org.hucompute.textimager.fasttext.labelannotator;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.hucompute.textimager.uima.util.XmlFormatter;
import org.junit.Test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;


public class LabelAnnotatorDockerSimpleTest {

	@Test
	public void testDdc2De() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist einfach nur ein Test.");
		cas.setDocumentLanguage("de");

		AggregateBuilder builder = new AggregateBuilder();

		builder.add(createEngineDescription(LanguageToolSegmenter.class));
		builder.add(createEngineDescription(LanguageToolLemmatizer.class));
		builder.add(createEngineDescription(CoreNlpPosTagger.class));

		builder.add(createEngineDescription(LabelAnnotatorDocker.class,
				LabelAnnotatorDocker.PARAM_FASTTEXT_K, 100,
				LabelAnnotatorDocker.PARAM_CUTOFF, false,
				LabelAnnotatorDocker.PARAM_SELECTION, "text",
				LabelAnnotatorDocker.PARAM_TAGS, "ddc2",
				LabelAnnotatorDocker.PARAM_USE_LEMMA, true,
				LabelAnnotatorDocker.PARAM_ADD_POS, true,
				//LabelAnnotatorDocker.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt",
				LabelAnnotatorDocker.PARAM_POSMAP_LOCATION, "/home/daniel/data/hiwi/git/gitlab/textimager-uima-ddc-fasttext-service/resources/nlp/models/categorization/am_posmap.txt",
				LabelAnnotatorDocker.PARAM_REMOVE_FUNCTIONWORDS, true,
				LabelAnnotatorDocker.PARAM_REMOVE_PUNCT, true,
				LabelAnnotatorDocker.PARAM_REST_ENDPOINT, "http://localhost:8080"
		));

		SimplePipeline.runPipeline(cas, builder.createAggregate());

		System.out.println(XmlFormatter.getPrettyString(cas));

		for (CategoryCoveredTagged category : JCasUtil.select(cas, CategoryCoveredTagged.class)) {
			System.out.println(category.getScore() + " " + category.getValue() + " " + category.getCoveredText());
		}
	}

	@Test
	public void testDdcMulDe() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist einfach nur ein Test.");
		cas.setDocumentLanguage("de");

		AggregateBuilder builder = new AggregateBuilder();

		builder.add(createEngineDescription(LanguageToolSegmenter.class));
		builder.add(createEngineDescription(LanguageToolLemmatizer.class));
		builder.add(createEngineDescription(CoreNlpPosTagger.class));

		builder.add(createEngineDescription(LabelAnnotatorDDCMulDocker.class,
				LabelAnnotatorDDCMulDocker.PARAM_FASTTEXT_K, 1000,
				LabelAnnotatorDDCMulDocker.PARAM_CUTOFF, false,
				LabelAnnotatorDDCMulDocker.PARAM_SELECTION, "text",
				LabelAnnotatorDDCMulDocker.PARAM_TAGS, "ddc3",
				LabelAnnotatorDDCMulDocker.PARAM_USE_LEMMA, true,
				LabelAnnotatorDDCMulDocker.PARAM_ADD_POS, true,
				//LabelAnnotatorDDCMulDocker.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt",
				LabelAnnotatorDDCMulDocker.PARAM_POSMAP_LOCATION, "/home/daniel/data/hiwi/git/gitlab/textimager-uima-ddc-fasttext-service/resources/nlp/models/categorization/am_posmap.txt",
				LabelAnnotatorDDCMulDocker.PARAM_REMOVE_FUNCTIONWORDS, true,
				LabelAnnotatorDDCMulDocker.PARAM_REMOVE_PUNCT, true,
				LabelAnnotatorDDCMulDocker.PARAM_REMOVE_OLD_SCORES, true,
				LabelAnnotatorDDCMulDocker.PARAM_REST_ENDPOINT, "http://localhost:8080"
		));

		SimplePipeline.runPipeline(cas, builder.createAggregate());

		System.out.println(XmlFormatter.getPrettyString(cas));

		for (CategoryCoveredTagged category : JCasUtil.select(cas, CategoryCoveredTagged.class)) {
			System.out.println(category.getScore() + " " + category.getValue() + " " + category.getCoveredText());
		}
	}
}
