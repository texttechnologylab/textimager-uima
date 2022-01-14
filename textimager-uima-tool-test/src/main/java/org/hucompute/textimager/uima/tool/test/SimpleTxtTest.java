package org.hucompute.textimager.uima.tool.test;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.dkpro.core.io.xmi.XmiReader;
import org.xml.sax.SAXException;

import java.io.IOException;


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
