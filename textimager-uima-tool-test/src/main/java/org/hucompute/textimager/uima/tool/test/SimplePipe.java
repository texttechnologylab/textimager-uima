package org.hucompute.textimager.uima.tool.test;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.io.xmi.XmiReader;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SimplePipe {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		CollectionReader reader = CollectionReaderFactory.createReader(
				XmiReader.class
				,XmiReader.PARAM_SOURCE_LOCATION,"/media/ahemati/cea5347d-36d3-4856-a9be-bcd0bddbfd92/dta_kernkorpus_out"
				,XmiReader.PARAM_PATTERNS,"**/*.xmi.gz"
				,XmiReader.PARAM_LANGUAGE,"de"
				,XmiReader.PARAM_LENIENT,true
				);
//
		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(createEngineDescription(ParagraphSplitter.class));
		builder.add(createEngineDescription(SpaCyMultiTagger.class));
//		builder.add(createEngineDescription(XmiWriter.class,
//				XmiWriter.PARAM_TARGET_LOCATION,"BHL",
//				XmiWriter.PARAM_OVERWRITE,true,
//				XmiWriter.PARAM_COMPRESSION,CompressionMethod.GZIP));
//
		SimplePipeline.runPipeline(reader, builder.createAggregate());
//		createEngineDescription(XmiWriter.class,
//				XmiWriter.PARAM_TARGET_LOCATION,"BHL",
//				XmiWriter.PARAM_OVERWRITE,true,
//				XmiWriter.PARAM_COMPRESSION,CompressionMethod.GZIP).toXML(new FileWriter(new File("XmiWriter.xml")));
	}

}
