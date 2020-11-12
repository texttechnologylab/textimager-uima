package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.admin.CASFactory;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.CasUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.io.tei.TeiReader;
import org.dkpro.core.io.xmi.XmiReader;
import org.dkpro.core.io.xmi.XmiWriter;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.matetools.MateLemmatizer;
import org.dkpro.core.matetools.MatePosTagger;
import org.dkpro.core.tokit.BreakIteratorSegmenter;
import org.dkpro.core.tokit.ParagraphSplitter;
import org.hucompute.textimager.uima.io.tei.TeiReaderTTLab;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger;
import org.xml.sax.SAXException;


public class SimpleTxtTest {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		CollectionReader reader = CollectionReaderFactory.createReader(
				XmiReader.class 
				,XmiReader.PARAM_SOURCE_LOCATION,"/resources/corpora/Wikipedia/dewiki-20190201/xmi_clean/0/*16300.xmi"
//				,XmiReader.PARAM_PATTERNS,"**/*.xmi"
				,XmiReader.PARAM_ADD_DOCUMENT_METADATA,false
//				,XmiReader.PARAM_LANGUAGE,"de"
//				,"sortBySize",true
				);
		
		
//		reader.toXML(new FileWriter(new File("XmiReader.xml")));
		
		
		
		
//		CollectionReader reader = CollectionReaderFactory.createReader(
//				TeiReader.class 
//				,TeiReader.PARAM_SOURCE_LOCATION,"/media/ahemati/cea5347d-36d3-4856-a9be-bcd0bddbfd92/dta_kernkorpus_2020-07-20/wuerttemberg_zigeuner_1685.TEI-P5.xml"
////				,TeiReader.PARAM_PATTERNS,"**/*.xml"
//				,TeiReader.PARAM_LANGUAGE,"de"
//				,"sortBySize",true
//				);
//		
//		
//		reader.toXML(new FileWriter(new File("TeiReader.xml")));
		
		
//		AggregateBuilder builder = new AggregateBuilder();
////		builder.add(createEngineDescription(BreakIteratorSegmenter.class));
//		builder.add(createEngineDescription(SpaCyMultiTagger.class));
////		builder.add(createEngineDescription(LanguageToolLemmatizer.class));
//		builder.add(createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,"dta",XmiWriter.PARAM_OVERWRITE,true));
////		
//		SimplePipeline.runPipeline(reader, builder.createAggregate());
		
		CAS cas = JCasFactory.createJCas().getCas();
		while(reader.hasNext()){
			reader.getNext(cas);
		}
	}

}
