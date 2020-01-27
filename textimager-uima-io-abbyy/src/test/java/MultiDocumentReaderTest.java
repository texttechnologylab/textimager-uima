import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.biofid.deep_eos.DeepEosTagger;
import org.junit.Test;
import textimager.uima.io.abby.MultiDocumentReader;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created on 18.11.19.
 */
public class MultiDocumentReaderTest {
	
	@Test
	public void testReader() {
		String path = Paths.get("src/test/resources/Botanische_Zeitschriften/").toAbsolutePath().toString();
		
		try {
			System.out.println(path);
			CollectionReader reader = CollectionReaderFactory.createReader(MultiDocumentReader.class,
					MultiDocumentReader.PARAM_SOURCE_LOCATION, path);
			AnalysisEngineDescription engine1 = AnalysisEngineFactory.createEngineDescription(DeepEosTagger.class,
					DeepEosTagger.PARAM_MODEL_NAME, "biofid",
					DeepEosTagger.PARAM_PYTHON_SRC, "../deep-eos-uima/target/python",
					DeepEosTagger.PARAM_VERBOSE, true);
			AnalysisEngineDescription engine2 =  AnalysisEngineFactory.createEngineDescription(
					JCas2JSONWriter.class,
					JCas2JSONWriter.PARAM_TARGET_LOCATION, "/tmp/MultiDocumentReaderTest.json");
			SimplePipeline.runPipeline(reader, engine1, engine2);
		} catch (UIMAException | IOException e) {
			e.printStackTrace();
		}
	}
}
