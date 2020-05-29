package org.hucompute.textimager.uima.tool.test;

import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.matetools.MateLemmatizer;
import org.dkpro.core.matetools.MateMorphTagger;
import org.dkpro.core.matetools.MateParser;
import org.hucompute.textimager.disambiguation.verbs.FrameEvaluator;
import org.hucompute.textimager.disambiguation.verbs.VerbsDisambiguation;
import org.hucompute.textimager.uima.util.XmlFormatter;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;

public class SimpleTest {

	public static void main(String[] args) throws UIMAException {

		JCas cas = JCasFactory.createText("Der Gefangene erhängte sich in seiner Zelle. Die Dorfbewohner erhängten den Viehdieb an einem Baum.","de");
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(CoreNlpSegmenter.class));
		builder.add(createEngineDescription(CoreNlpPosTagger.class));
		builder.add(createEngineDescription(MateLemmatizer.class));
		builder.add(createEngineDescription(MateMorphTagger.class));
		builder.add(createEngineDescription(MateParser.class));
//		builder.add(createEngineDescription(
//				FrameEvaluator.class,
//				FrameEvaluator.PARAM_GERMANET_PATH,"/home/staff_homes/ahemati/projects/VerbsAnnotator/trunk/src/main/resources/GN_V140.zip"));
//		builder.add(createEngineDescription(
//				VerbsDisambiguation.class,
//				VerbsDisambiguation.PARAM_GERMANET_PATH,"/home/staff_homes/ahemati/projects/VerbsAnnotator/trunk/src/main/resources/GN_V140.zip",
//				VerbsDisambiguation.PARAM_VERBLEMMAIDS_PATH,"/home/staff_homes/ahemati/projects/VerbsAnnotator/trunk/verbLemmaIds"
//				));
//		builder.add(createEngineDescription(
//				VerbsDisambiguation.class,
//				VerbsDisambiguation.PARAM_GERMANET_PATH,"/resources/nlp/models/disambig/verbs/GN_V140.zip"
//				));
//		builder.add(createEngineDescription(
//				VerbsDisambiguation.class,
//				VerbsDisambiguation.PARAM_GERMANET_PATH,"/resources/nlp/models/disambig/verbs/GN_V140.zip",
//				VerbsDisambiguation.PARAM_ACTIVATE_REDUCER,true,
//				VerbsDisambiguation.PARAM_VARIANT,"reduced"
//				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
	}

}
