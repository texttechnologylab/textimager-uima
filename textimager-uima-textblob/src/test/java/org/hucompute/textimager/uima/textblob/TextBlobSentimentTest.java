//package org.hucompute.textimager.uima.textblob;
//
//import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
//
//import org.apache.uima.UIMAException;
//import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.fit.factory.JCasFactory;
//import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.fit.util.JCasUtil;
//import org.apache.uima.jcas.JCas;
//import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger;
//import org.hucompute.textimager.uima.type.Sentiment;
//import org.hucompute.textimager.uima.util.XmlFormatter;
//import org.junit.Test;
//
//import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
//
//public class TextBlobSentimentTest {
//	@Test
//	public void multiTaggerTest() throws UIMAException {
//		JCas cas = JCasFactory.createText("I hate this car! This sentence is neutral! I really like this house.", "en");
//		
//		/*Sentence s1 = new Sentence(cas, 0, 16);
//		s1.addToIndexes();
//		Sentence s2 = new Sentence(cas, 17, 33);
//		s2.addToIndexes();
//		Sentence s3 = new Sentence(cas, 34, 59);
//		s3.addToIndexes();*/
//
//		AnalysisEngineDescription spacy = createEngineDescription(SpaCyMultiTagger.class);
//		AnalysisEngineDescription textblobSentiment = createEngineDescription(TextBlobSentiment.class);
//		
//
//		SimplePipeline.runPipeline(cas, spacy, textblobSentiment);
//		
//		for (Sentiment sent : JCasUtil.select(cas, Sentiment.class)) {
//			System.out.println("Sentiment:");
//			System.out.println(sent.getSentiment());
//			System.out.println(sent.getSubjectivity());
//			System.out.println(sent.getCoveredText());
//			System.out.println("----------------------");
//		}
//		
//		System.out.println(XmlFormatter.getPrettyString(cas));
//	}
//}
//
