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
import org.dkpro.core.corenlp.CoreNlpPosTagger;
import org.dkpro.core.corenlp.CoreNlpSegmenter;
import org.dkpro.core.io.xmi.XmiWriter;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.matetools.MateLemmatizer;
import org.dkpro.core.matetools.MatePosTagger;
import org.hucompute.textimager.uima.io.tei.TeiReaderTTLab;
import org.xml.sax.SAXException;

public class SimpleTeiTest {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				TeiReaderTTLab.class 
//				,TeiReader.PARAM_SOURCE_LOCATION,"/resources/corpora/Zeit/raw",
//				TeiReader.PARAM_PATTERNS,"**/*.tei",
//				TeiReader.PARAM_LANGUAGE,"de"
				);
		
		reader.toXML(new FileWriter(new File("TeiReaderModified.xml")));
//		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(createEngineDescription(CoreNlpSegmenter.class));
//		builder.add(createEngineDescription(MatePosTagger.class));
//		builder.add(createEngineDescription(LanguageToolLemmatizer.class));
//		builder.add(createEngineDescription(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,"/resources/corpora/Zeit/XMI",XmiWriter.PARAM_OVERWRITE,true));
//		
//		SimplePipeline.runPipeline(reader., builder.createAggregate());
	}

}
