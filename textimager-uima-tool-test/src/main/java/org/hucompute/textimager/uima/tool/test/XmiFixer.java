package org.hucompute.textimager.uima.tool.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.xml.transform.OutputKeys;

import org.apache.uima.UIMAException;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLSerializer;
import org.xml.sax.SAXException;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/*
    Fix XMIs using "regex" to allow procesing in TextImager
    - Remove Token, Sentence Annotations
 */
public class XmiFixer {
    Path outputFile;
    JCas jCas;

    public XmiFixer(Path xmiFile, Path outputFile) throws IOException, UIMAException, SAXException {
        this.outputFile = outputFile;
        
        jCas = JCasFactory.createJCas();
        InputStream inputStream = Files.newInputStream(xmiFile);
        XmiCasDeserializer.deserialize(inputStream, jCas.getCas());
    }
    
    void fixMeta() {
    	DocumentMetaData meta = DocumentMetaData.get(jCas);
    	meta.setDocumentId(meta.getDocumentId().replaceAll("\\\\", "/"));
    	meta.setDocumentUri(meta.getDocumentUri().replaceAll("\\\\", "/").replaceAll("file:k:", ""));
    	meta.setDocumentBaseUri(meta.getDocumentBaseUri().replaceAll("\\\\", "/").replaceAll("file:k:", ""));
    	meta.setCollectionId(meta.getCollectionId().replaceAll("\\\\", "/").replaceAll("file:k:", ""));
    }

    void removeToken() {
        for (Token token : JCasUtil.select(jCas, Token.class)) {
        	token.removeFromIndexes();
        }
    }

    void removeSentences() {
        for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
        	sentence.removeFromIndexes();
        }
    }

    private void finish() throws IOException, SAXException {
        Files.createDirectories(outputFile.getParent());
        OutputStream outputStream = Files.newOutputStream(outputFile);
        XMLSerializer xmlSerializer = new XMLSerializer(outputStream, true);
        xmlSerializer.setOutputProperty(OutputKeys.VERSION, "1.0");
        xmlSerializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
        XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(null);
        xmiCasSerializer.serialize(jCas.getCas(), xmlSerializer.getContentHandler());
        outputStream.close();
    }

    public static void main(String[] args) throws IOException {
        Path inputDir = Paths.get(args[0]);
        Path outputDir = Paths.get(args[1]);

        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir);
        }
        if (!Files.isDirectory(outputDir)) {
            System.err.println("Output dir is not a directory: " + outputDir.toString());
            System.exit(1);
        }

        try (Stream<Path> stream = Files.walk(inputDir)) {
            stream
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        try {
                            Path outputFile = outputDir.resolve(inputDir.relativize(file));
                            System.out.println(file.toString() + " --> " + outputFile.toString());
                            XmiFixer xmiFixer = new XmiFixer(file, outputFile);
                            xmiFixer.removeToken();
                            xmiFixer.removeSentences();
                            xmiFixer.fixMeta();
                            xmiFixer.finish();
                        } catch (IOException | UIMAException | SAXException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }
}
