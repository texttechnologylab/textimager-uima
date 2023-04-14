import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.texttechnologylab.utilities.helper.FileUtils;
import org.texttechnologylab.utilities.helper.StringUtils;
import textimager.uima.io.abby.DocumentReader;
import textimager.uima.io.abby.utility.XMIWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * Created on 18.11.19.
 */
public class BIOfidTest {

	@Test
	public void testReader() throws IOException {
//		String sBase = "/home/staff_homes/abrami/Downloads/newRun/";
		String sBase = "/data/biofid/abbyy";
//		String sOutPath= "/home/staff_homes/abrami/Downloads/newRunOut/";
		String sOutPath= "/data/tmp";

		Set<String> outSet = FileUtils.getFiles(sBase, ".xml").stream().map(f->f.getName()).collect(Collectors.toSet());

		FileUtils.getFiles(sBase, ".xml").stream()
//				.filter(f->!outSet.contains(f.getName()))
			.forEach(f->{
			try {
				System.out.println("Processing: "+f.getName());

//				File tFile = TempFileHandler.getTempFile(f.getName().replace(".gz", ""),".xml");

				File targetFile = new File (sOutPath+f.getName());
				if(!targetFile.exists()) {

//					decompressGZ(Paths.get(f.getAbsolutePath()), Paths.get(tFile.getAbsolutePath()));

					CollectionReader reader = CollectionReaderFactory.createReader(DocumentReader.class,
							DocumentReader.PARAM_SOURCE_LOCATION, f.getPath());

					AnalysisEngineDescription languageTool = createEngineDescription(LanguageToolSegmenter.class);

					AnalysisEngineDescription writer = createEngineDescription(XMIWriter.class,
							XMIWriter.PARAM_PRETTY_PRINT, true,
							XMIWriter.PARAM_SINGULAR_TARGET, true,
							XMIWriter.PARAM_TARGET_LOCATION, targetFile.getPath());

					SimplePipeline.runPipeline(reader, writer);

					String sContent = StringUtils.getContent(targetFile);

					int iPlace = sContent.indexOf("</xmi:XMI>");
					System.out.println(iPlace);
					sContent = sContent.substring(0, iPlace+10);

					StringUtils.writeContent(sContent, targetFile);

					JCas test = JCasFactory.createJCas();
					CasIOUtil.readXmi(test, targetFile);

					SimplePipeline.runPipeline(test, languageTool);
                    DocumentMetaData dmd = DocumentMetaData.get(test);
                    dmd.setDocumentId(f.getName());

					CasIOUtil.writeXmi(test, targetFile);

//					compressGZ(Paths.get(targetFile.getAbsolutePath()), Paths.get(targetFile.getAbsolutePath()));

//					tFile.delete();
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
