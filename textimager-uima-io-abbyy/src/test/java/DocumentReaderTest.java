import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.junit.Test;
import textimager.uima.io.abby.DocumentReader;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * Created on 18.11.19.
 */
public class DocumentReaderTest {
	
	@Test
	public void testReader() {
		String path = Paths.get("src/test/resources/Biodiversity/9031458/9657398/9843779/9843841").toAbsolutePath().toString();
		
		try {
			System.out.println(path);
			CollectionReader reader = CollectionReaderFactory.createReader(DocumentReader.class,
					DocumentReader.PARAM_SOURCE_LOCATION, path);
			AnalysisEngineDescription engine1 = AnalysisEngineFactory.createEngineDescription(DummyCasConsumer.class);
			SimplePipeline.runPipeline(reader, engine1);
		} catch (UIMAException | IOException e) {
			e.printStackTrace();
		}
	}
}
