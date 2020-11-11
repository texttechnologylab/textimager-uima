//package org.hucompute.textimager.uima.tool.test;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//
//import java.io.IOException;
//
//import org.apache.uima.UIMAException;
//import org.apache.uima.collection.CollectionReader;
//import org.apache.uima.fit.factory.AggregateBuilder;
//import org.apache.uima.fit.factory.CollectionReaderFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.dkpro.core.io.xmi.XmiReader;
//import org.hucompute.textimager.uima.io.embeddings.writer.BaseEmbeddingsWriter;
//import org.hucompute.textimager.uima.io.embeddings.writer.MikolovWriter;
//import org.xml.sax.SAXException;
//
//public class SimpleEmbeddingsExporter{
//
//	public static void main(String[] args) throws UIMAException, IOException, SAXException {
//		CollectionReader reader = CollectionReaderFactory.createReader(
//				XmiReader.class 
//				,XmiReader.PARAM_SOURCE_LOCATION,"/resources/public/mehler/Lehrbuch_Korpus_2020_06_16/XMI",
//				XmiReader.PARAM_PATTERNS,"**/*.xmi",
//				XmiReader.PARAM_LANGUAGE,"de"
//				);
//		
//		AggregateBuilder builder = new AggregateBuilder();
////		builder.add(createEngineDescription(CoreNlpSegmenter.class));
////		builder.add(createEngineDescription(MatePosTagger.class));
////		builder.add(createEngineDescription(LanguageToolLemmatizer.class));
//		builder.add(createEngineDescription(MikolovWriter.class, 
//				MikolovWriter.PARAM_TARGET_LOCATION,"Lehrbuch_mikolov",
//				MikolovWriter.PARAM_MODUS,BaseEmbeddingsWriter.Modus.LEMMA_POS,
//				MikolovWriter.PARAM_NORMALIZE_POS,true));
////		
//		SimplePipeline.runPipeline(reader, builder.createAggregate());
//	}
//
//}
