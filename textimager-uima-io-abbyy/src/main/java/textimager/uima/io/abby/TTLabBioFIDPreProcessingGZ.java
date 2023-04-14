package textimager.uima.io.abby;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
//import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
//import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
//import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
//import org.apache.uima.fit.util.CasIOUtil;
//import org.apache.uima.fit.util.JCasUtil;
//import org.apache.uima.jcas.JCas;
//import org.apache.uima.util.XMLSerializer;
import org.texttechnologylab.utilities.helper.FileUtils;
//import org.texttechnologylab.utilities.helper.StringUtils;
//import org.xml.sax.SAXException;
import textimager.uima.io.abby.utility.XMIWriter;

//import javax.xml.transform.OutputKeys;
import java.io.*;
//import java.nio.charset.Charset;
//import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
//import java.util.Set;
//import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class TTLabBioFIDPreProcessingGZ {

    public static void main(String[] args) throws IOException {

        String pathIn = args[0];
        String pathOut = args[1];
        String pathTemp = args[2];

//        Set<String> outSet = FileUtils.getFiles(pathOut, ".gz").stream().map(f->f.getName()).collect(Collectors.toSet());

        FileUtils.getFiles(pathIn, ".gz").stream()
            .forEach(f -> {
//                File nFile = new File(pathTemp + "n" + f.getName().replace(".xml", "") + ".xmi");
                File tmpFile = new File(pathTemp + f.getName().replace(".gz", ""));
                try {
                    System.out.println("Processing: " + f.getName());
                    if (true) {
                        String filename = decompressGZ(Paths.get(f.getAbsolutePath()), Paths.get(tmpFile.getAbsolutePath()));
                        CollectionReader reader = CollectionReaderFactory.createReader(DocumentReaderMultiPage.class,
                                DocumentReader.PARAM_SOURCE_LOCATION, tmpFile.getPath());
//                        AnalysisEngineDescription languageTool = createEngineDescription(LanguageToolSegmenter.class);
                        AnalysisEngineDescription writer = createEngineDescription(XMIWriter.class,
                                XMIWriter.PARAM_PRETTY_PRINT, true,
                                XMIWriter.PARAM_VERSION, "1.1",
                                XMIWriter.PARAM_SINGULAR_TARGET, false,
                                XMIWriter.PARAM_PRETTY_PRINT, true,
                                XMIWriter.PARAM_COMPRESSION, "GZIP",
                                XMIWriter.PARAM_OVERWRITE, true,
                                XMIWriter.PARAM_TARGET_LOCATION, pathOut);

                        SimplePipeline.runPipeline(reader, writer);
                        tmpFile.delete();
                    }
                } catch (UIMAException | IOException e) {
                    e.printStackTrace();
                } finally {
//                    nFile.delete();
                    tmpFile.delete();
                }
            });
    }


    public static String decompressGZ(Path input, Path output) throws IOException {

        String filename = "";
        try (
              GzipCompressorInputStream gis =
                      new GzipCompressorInputStream(
                              new FileInputStream(input.toFile())
                      )
        ) {
            filename = gis.getMetaData().getFilename();
            System.out.println(filename);
            Files.copy(gis, output);
        }

        return filename;
    }

    public static void compressGZ(Path input, Path output) throws IOException {

        try (GZIPOutputStream gos = new GZIPOutputStream(
                new FileOutputStream(output.toFile()))) {

            Files.copy(input, gos);

        }

    }

}
