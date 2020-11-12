package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.api.resources.CompressionMethod;
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.io.tei.TeiReader;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.io.xmi.XmiReader;
import org.dkpro.core.io.xmi.XmiWriter;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.matetools.MateLemmatizer;
import org.dkpro.core.matetools.MatePosTagger;
import org.dkpro.core.tokit.ParagraphSplitter;
import org.hucompute.textimager.uima.io.tei.TeiReaderTTLab;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger;
import org.xml.sax.SAXException;

public class SimplePipeLoadXMLDescriptor {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		CollectionReader reader = CollectionReaderFactory.createReader(
				XmiReader.class 
				,XmiReader.PARAM_SOURCE_LOCATION,"/media/ahemati/cea5347d-36d3-4856-a9be-bcd0bddbfd92/dta_kernkorpus_out"
				,XmiReader.PARAM_PATTERNS,"**/*.xmi.gz"
				,XmiReader.PARAM_LANGUAGE,"de"
				,XmiReader.PARAM_LENIENT,true
				);
//		
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(createEngineDescription(ParagraphSplitter.class));
//		builder.add(createEngineDescription(SpaCyMultiTagger.class));
//		builder.add(createEngineDescription(XmiWriter.class, 
//				XmiWriter.PARAM_TARGET_LOCATION,"BHL",
//				XmiWriter.PARAM_OVERWRITE,true,
//				XmiWriter.PARAM_COMPRESSION,CompressionMethod.GZIP));
//		
		SimplePipeline.runPipeline(reader, AnalysisEngineFactory.createAnalysisEngineFromPath("desc.xml"));
//		createEngineDescription(XmiWriter.class, 
//				XmiWriter.PARAM_TARGET_LOCATION,"BHL",
//				XmiWriter.PARAM_OVERWRITE,true,
//				XmiWriter.PARAM_COMPRESSION,CompressionMethod.GZIP).toXML(new FileWriter(new File("XmiWriter.xml")));
	}

}
