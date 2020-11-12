//package org.hucompute.textimager.uima.tool.test;
//
//import org.apache.uima.fit.factory.AggregateBuilder;
//import org.apache.uima.fit.factory.CollectionReaderFactory;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.jcas.JCas;
//import org.biofid.gazetteer.BIOfidTreeGazetteer;
//import org.dkpro.core.corenlp.CoreNlpNamedEntityRecognizer;
//import org.dkpro.core.corenlp.CoreNlpPosTagger;
//import org.dkpro.core.corenlp.CoreNlpSegmenter;
//import org.dkpro.core.io.text.TextReader;
//import org.dkpro.core.io.xmi.XmiWriter;
//import org.dkpro.core.languagetool.LanguageToolLemmatizer;
//import org.dkpro.core.matetools.MateLemmatizer;
//import org.dkpro.core.matetools.MateMorphTagger;
//import org.dkpro.core.matetools.MateParser;
//import org.dkpro.core.stanfordnlp.StanfordNamedEntityRecognizer;
//import org.hucompute.textimager.disambiguation.verbs.FrameEvaluator;
//import org.hucompute.textimager.disambiguation.verbs.VerbsDisambiguation;
//import org.hucompute.textimager.uima.flair.FlairNERBiofid;
//import org.hucompute.textimager.uima.ner.HUComputeNER;
//import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger;
//import org.hucompute.textimager.uima.tagme.TagMeAPIAnnotator;
//import org.hucompute.textimager.uima.util.XmlFormatter;
//import org.hucompute.textimager.uima.wiki.WikidataHyponyms;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//
//import java.io.IOException;
//
//import org.apache.uima.UIMAException;
//import org.apache.uima.collection.CollectionReader;
//
//public class SimpleBHLTest {
//
//	public static void main(String[] args) throws UIMAException, IOException {
//
//		CollectionReader reader = CollectionReaderFactory.createReader(
//				TextReader.class 
//				,TextReader.PARAM_SOURCE_LOCATION,"29472.txt"
////				,TeiReader.PARAM_PATTERNS,"**/*.xml"
//				,TextReader.PARAM_LANGUAGE,"de"
//				);		
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(createEngineDescription(SpaCyMultiTagger.class));
//		builder.add(createEngineDescription(LanguageToolLemmatizer.class));
//		builder.add(createEngineDescription(TagMeAPIAnnotator.class,
//				TagMeAPIAnnotator.PARAM_GCUBE_TOKEN,"685b6106-bba0-43e2-87b6-ad8ea0c8f9e2-843339462",
//				TagMeAPIAnnotator.PARAM_RHO,0.01f));
//		builder.add(createEngineDescription(WikidataHyponyms.class));
//		builder.add(createEngineDescription(BIOfidTreeGazetteer.class));
//		builder.add(createEngineDescription(FlairNERBiofid.class)); //Fehler in Flair Projekt hat dazu gef�hrt dass das gesamte Tool-Test Projekt nicht mehr 
//																	// ausf�hrbar war. Habe Flair aus der Pom.xml ausgeklammert.
//		
//		builder.add(createEngineDescription(HUComputeNER.class,HUComputeNER.PARAM_CLASS_MAP,"/resources/nlp/models/ner/hucomputeNer/classmap"));
//		builder.add(createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,"bhl",XmiWriter.PARAM_OVERWRITE,true));
//		SimplePipeline.runPipeline(reader, builder.createAggregate());
//
//		
//	}
//
//}
