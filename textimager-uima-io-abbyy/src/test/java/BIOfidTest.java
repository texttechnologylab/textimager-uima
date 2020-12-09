import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.io.xmi.XmiWriter;
import org.junit.Test;
import org.texttechnologylab.utilities.helper.FileUtils;
import textimager.uima.io.abby.DocumentReader;
import textimager.uima.io.abby.annotation.Document;
import textimager.uima.io.abby.utility.XMIWriter;

import java.io.IOException;
import java.nio.file.Paths;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * Created on 18.11.19.
 */
public class BIOfidTest {

	@Test
	public void testReader() throws IOException {
		String sPath = "/mnt/bioFID/Export/Biodiversity";

		FileUtils.getFiles(sPath, ".xml").forEach(f->{
			try {
				System.out.println(sPath);
				CollectionReader reader = CollectionReaderFactory.createReader(DocumentReader.class,
						DocumentReader.PARAM_SOURCE_LOCATION, f.getPath());
				AnalysisEngineDescription writer = createEngineDescription(XMIWriter.class,
						XMIWriter.PARAM_PRETTY_PRINT, true,
						XMIWriter.PARAM_SINGULAR_TARGET, true,
						XMIWriter.PARAM_TARGET_LOCATION, "/tmp/test/"+f.getName());

				SimplePipeline.runPipeline(reader, writer);

			} catch (UIMAException | IOException e) {
				e.printStackTrace();
			}
		});


	}
}
