package test;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.hucompute.textimager.uima.io.tei.TeiReaderTTLab;

import java.io.IOException;

public class TeiReaderTest {

	public static void main(String[] args) throws UIMAException, IOException {
		CollectionReaderDescription reader = CollectionReaderFactory.createReaderDescription(
				TeiReaderTTLab.class,
				TeiReaderTTLab.PARAM_SOURCE_LOCATION,"/resources/corpora/Zeit/raw/0",
				TeiReaderTTLab.PARAM_PATTERNS,"**/*.tei");

//		SimplePipeline.runPipeline(reader, AnalysisEngineFactory.createEngine(XmiWriter.class, XmiWriter.PARAM_TARGET_LOCATION,"/resources/corpora/Zeit/XMI/0"));

	}

}
