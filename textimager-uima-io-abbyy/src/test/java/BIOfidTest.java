import de.tudarmstadt.ukp.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.io.xmi.XmiWriter;
import org.junit.Test;
import org.texttechnologylab.utilities.helper.FileUtils;
import org.texttechnologylab.utilities.helper.TempFileHandler;
import textimager.uima.io.abby.DocumentReader;
import textimager.uima.io.abby.annotation.Document;
import textimager.uima.io.abby.utility.XMIWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * Created on 18.11.19.
 */
public class BIOfidTest {

	@Test
	public void testReader() throws IOException {
		String sPath = "/media/gabrami/85ff0921-743b-48ce-8962-07a08a9db03e/bhl/abbyy";

		FileUtils.getFiles(sPath, ".gz").forEach(f->{
			try {
				System.out.println("Processing: "+f.getName());

//				File tFile = TempFileHandler.getTempFile(f.getName().replace(".gz", ""),".xml");

				File tFile = TempFileHandler.getTempFileName(f.getName().replace(".gz", "")+".xml");
				String sTemp = "/media/gabrami/85ff0921-743b-48ce-8962-07a08a9db03e/bhl/tmp/";

				File targetFile = new File ("/media/gabrami/85ff0921-743b-48ce-8962-07a08a9db03e/bhl/out/"+tFile.getName()+".gz");
				if(!targetFile.exists()) {

					decompressGZ(Paths.get(f.getAbsolutePath()), Paths.get(tFile.getAbsolutePath()));

					CollectionReader reader = CollectionReaderFactory.createReader(DocumentReader.class,
							DocumentReader.PARAM_SOURCE_LOCATION, tFile.getPath());
					AnalysisEngineDescription writer = createEngineDescription(XMIWriter.class,
							XMIWriter.PARAM_PRETTY_PRINT, true,
							XMIWriter.PARAM_SINGULAR_TARGET, true,
							XMIWriter.PARAM_TARGET_LOCATION, sTemp + tFile.getName());

					SimplePipeline.runPipeline(reader, writer);

					compressGZ(Paths.get(sTemp+ tFile.getName()), Paths.get(targetFile.getAbsolutePath()));

					File oFile = new File(sTemp + tFile.getName());
					oFile.delete();
					tFile.delete();
				}
			} catch (UIMAException | IOException e) {
				e.printStackTrace();
			}
		});


	}

	public static void decompressGZ(Path input, Path output) throws IOException {

		try (GZIPInputStream gis = new GZIPInputStream(
				new FileInputStream(input.toFile()))) {
			Files.copy(gis, output);

		}

	}

	public static void compressGZ(Path input, Path output) throws IOException {

		try (GZIPOutputStream gos = new GZIPOutputStream(
				new FileOutputStream(output.toFile()))) {

			Files.copy(input, gos);

		}

	}

}
