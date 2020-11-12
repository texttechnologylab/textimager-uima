//package org.hucompute.textimager.uima.tool.test;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//
//import org.apache.uima.UIMAException;
//import org.apache.uima.fit.factory.AggregateBuilder;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.jcas.JCas;
//import org.hucompute.textimager.uima.transformers.NERTransformers;
//import org.hucompute.textimager.uima.transformers.SentAnaTransformers;
//import org.hucompute.textimager.uima.transformers.TextSumTransformers;
//import org.hucompute.textimager.uima.util.XmlFormatter;
//
//
//import jep.JepException;
//
//public class SimpleTestTransformers {
//
//	public static void main(String[] args) throws UIMAException, JepException {
//
//
//			JCas cas = JCasFactory.createText("Bill Gates ist der Chef von Microsoft","de");
//			AggregateBuilder builder = new AggregateBuilder();
//
//			//builder.add(createEngineDescription(NERTransformers.class,NERTransformers.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
//			builder.add(createEngineDescription(SentAnaTransformers.class));
//			builder.add(createEngineDescription(SentAnaTransformers.class,SentAnaTransformers.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
//			//builder.add(createEngineDescription(TextSumTransformers.class,TextSumTransformers.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
//			SimplePipeline.runPipeline(cas,builder.createAggregate());
//						
//			System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//
//
//		}
//}
