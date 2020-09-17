package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.api.io.ResourceCollectionReaderBase;
import org.dkpro.core.api.resources.CompressionMethod;
import org.dkpro.core.io.xmi.XmiReader;
import org.dkpro.core.io.xmi.XmiWriter;
import org.hucompute.textimager.uima.io.tei.TeiReaderTTLab;
import org.hucompute.textimager.uima.stanza.StanzaTagger;
import org.xml.sax.SAXException;

public class SimpleTeiTest {

	public static void main(String[] args) throws UIMAException, IOException, SAXException {
		CollectionReader reader = CollectionReaderFactory.createReader(
				TeiReaderTTLab.class 
				,TeiReaderTTLab.PARAM_SOURCE_LOCATION,"/resources/corpora/DTA/dta_kernkorpus_2020-07-20/tei"
				,TeiReaderTTLab.PARAM_PATTERNS,"**/*.xml"
				,TeiReaderTTLab.PARAM_LANGUAGE,"de",
				ResourceCollectionReaderBase.PARAM_SORT_BY_SIZE,true,
				ResourceCollectionReaderBase.PARAM_LOG_FREQ,1
				);
		
//		INFO: 33 of 1472 (3%  ETA 01:19:56.797  RUN 00:01:50.3    AVG 3333  LAST 4501): file:/resources/corpora/DTA/dta_kernkorpus_2020-07-20/tei/mehring_kunst_1896.TEI-P5.xml
//		Processing 1 TEI elements in file:/resources/corpora/DTA/dta_kernkorpus_2020-07-20/tei/stuer_bericht_1601.TEI-P5.xml.
//		reader.toXML(new FileWriter(new File("TeiReader.xml")));
		AggregateBuilder builder = new AggregateBuilder();
//		builder.add(createEngineDescription(BreakIteratorSegmenter.class));
//		builder.add(createEngineDescription(SpaCyMultiTagger.class));
//		builder.add(createEngineDescription(LanguageToolLemmatizer.class));
		builder.add(createEngineDescription(StanzaTagger.class));
		builder.add(createEngineDescription(
				XmiWriter.class, 
				XmiWriter.PARAM_TARGET_LOCATION,"/resources/corpora/DTA/dta_kernkorpus_2020-07-20/ttlab_xmi_only_stanza",
				XmiWriter.PARAM_OVERWRITE,true,
				XmiWriter.PARAM_COMPRESSION,CompressionMethod.GZIP));
//		
		SimplePipeline.runPipeline(reader, builder.createAggregate());
	}

}
