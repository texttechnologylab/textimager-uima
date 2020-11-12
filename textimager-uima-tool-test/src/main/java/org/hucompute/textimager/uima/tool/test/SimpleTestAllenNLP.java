//package org.hucompute.textimager.uima.tool.test;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//
//import java.nio.file.Paths;
//
//import org.apache.uima.UIMAException;
//import org.apache.uima.fit.factory.AggregateBuilder;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.jcas.JCas;
//import org.hucompute.textimager.uima.allennlp.AllenNLPNER;
//import org.hucompute.textimager.uima.allennlp.AllenNLPSentiment;
//import org.hucompute.textimager.uima.allennlp.AllenNLPOpenIE;
//import org.hucompute.textimager.uima.util.XmlFormatter;
//
//import jep.JepException;
//import junit.framework.Test;
//
//
//public class SimpleTestAllenNLP {
//
//	public static void main(String[] args) throws UIMAException, JepException {
//		JCas cas = JCasFactory.createText("Die Gefangenen erhängten sich in ihrer Zelle. Die Dorfbewohner erhängten den Viehdieb an einem Baum.","de");
//
//		
//		AggregateBuilder builder = new AggregateBuilder();
////		builder.add(createEngineDescription(
////			AllenNLPNER.class,
////			AllenNLPNER.PARAM_PYTHON_HOME,Paths.get(System.getProperty("user.home") ,"AppData\\Local\\Programs\\Python\\Python36").toAbsolutePath().toString(),
////			AllenNLPNER.PARAM_NER_MAPPING_LOCATION,"classpath:org/hucompute/textimager/uima/allennlp/lib/ner-default.map"
////				));
//		
////		builder.add(createEngineDescription(
////				AllenNLPSentiment.class,
////				AllenNLPSentiment.PARAM_PYTHON_HOME,Paths.get(System.getProperty("user.home") ,"AppData\\Local\\Programs\\Python\\Python36").toAbsolutePath().toString()
////				));
//		
//		builder.add(createEngineDescription(
//				AllenNLPOpenIE.class,
//				AllenNLPOpenIE.PARAM_PYTHON_HOME,Paths.get(System.getProperty("user.home") ,"AppData\\Local\\Programs\\Python\\Python36").toAbsolutePath().toString()
//				));
//		
//		
//		SimplePipeline.runPipeline(cas,builder.createAggregate());
//	
//		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
//	}
//
//}