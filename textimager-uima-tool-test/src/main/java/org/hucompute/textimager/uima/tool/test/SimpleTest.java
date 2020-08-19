package org.hucompute.textimager.uima.tool.test;

import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.corenlp.CoreNlpNamedEntityRecognizer;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.matetools.MateLemmatizer;
import org.dkpro.core.matetools.MateMorphTagger;
import org.dkpro.core.matetools.MateParser;
import org.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
import org.hucompute.textimager.disambiguation.verbs.FrameEvaluator;
import org.hucompute.textimager.disambiguation.verbs.VerbsDisambiguation;
import org.hucompute.textimager.uima.util.XmlFormatter;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;

public class SimpleTest {

	public static void main(String[] args) throws UIMAException {

		JCas cas = JCasFactory.createText("This is a test by Barrack Obama.","en");
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(CoreNlpSegmenter.class));
		builder.add(createEngineDescription(CoreNlpPosTagger.class));
		builder.add(createEngineDescription(StanfordNamedEntityRecognizer.class));
//		builder.add(createEngineDescription(MateLemmatizer.class));
//		builder.add(createEngineDescription(MateParser.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
	}

}
