//package org.hucompute.textimager.uima.tool.test;
//
//import org.apache.uima.fit.factory.AggregateBuilder;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.jcas.JCas;
//import org.dkpro.core.corenlp.CoreNlpPosTagger;
//import org.dkpro.core.corenlp.CoreNlpSegmenter;
//import org.dkpro.core.languagetool.LanguageToolLemmatizer;
//import org.dkpro.core.matetools.MateLemmatizer;
//import org.dkpro.core.matetools.MateMorphTagger;
//import org.dkpro.core.matetools.MateParser;
//import org.dkpro.core.udpipe.*;
//import org.hucompute.textimager.disambiguation.verbs.FrameEvaluator;
//import org.hucompute.textimager.disambiguation.verbs.VerbsDisambiguation;
//import org.hucompute.textimager.uima.util.XmlFormatter;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//
//import org.apache.uima.UIMAException;
//
//public class SimpleTestUdPipe {
//
//	public static void main(String[] args) throws UIMAException {
//
//		
//		JCas cas = JCasFactory.createText("Petrus annum decimum agens ad regnum vocatus est, favente imprimis familia Narykin, abnuente familia Miloslavski et factione streltsy, scilicet militum pyrobolistarum.","lt");
//		AggregateBuilder builder = new AggregateBuilder();
//
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "proiel-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "proiel-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "proiel-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		
//		cas = JCasFactory.createText("Petrus annum decimum agens ad regnum vocatus est, favente imprimis familia Narykin, abnuente familia Miloslavski et factione streltsy, scilicet militum pyrobolistarum.","lt");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "ittb-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "ittb-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "ittb-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		
//		cas = JCasFactory.createText("Petrus annum decimum agens ad regnum vocatus est, favente imprimis familia Narykin, abnuente familia Miloslavski et factione streltsy, scilicet militum pyrobolistarum.","lt");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "perseus-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "perseus-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "perseus-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//
//		
//
//		cas = JCasFactory.createText("Der Apfel f�llt nicht weit vom Stamm.","de");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "gsd-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "gsd-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "gsd-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		
//		cas = JCasFactory.createText("Der Apfel f�llt nicht weit vom Stamm.","de");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "hdt-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "hdt-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "hdt-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//
//		
//		cas = JCasFactory.createText("The Overture From All New SpaceLifetimes Souls AlReeady Awakening On Those than The Eternal AwareNess cannot Wait Anymore.","en");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "ewt-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "ewt-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "ewt-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		
//		cas = JCasFactory.createText("The Overture From All New SpaceLifetimes Souls AlReeady Awakening On Those than The Eternal AwareNess cannot Wait Anymore.","en");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "gum-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "gum-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "gum-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		
//		cas = JCasFactory.createText("The Overture From All New SpaceLifetimes Souls AlReeady Awakening On Those than The Eternal AwareNess cannot Wait Anymore.","en");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "lines-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "lines-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "lines-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		
//		cas = JCasFactory.createText("The Overture From All New SpaceLifetimes Souls AlReeady Awakening On Those than The Eternal AwareNess cannot Wait Anymore.","en");
//		builder = new AggregateBuilder();
//		builder.add(createEngineDescription(UDPipeSegmenter.class, UDPipeSegmenter.PARAM_VARIANT, "partut-ud"));
//		builder.add(createEngineDescription(UDPipePosTagger.class, UDPipePosTagger.PARAM_VARIANT, "partut-ud"));
//		builder.add(createEngineDescription(UDPipeParser.class, UDPipeParser.PARAM_VARIANT, "partut-ud"));
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//		
//	}
//
//}
