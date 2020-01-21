package org.hucompute.textimager.uima.io.html;

import static org.apache.uima.fit.factory.CollectionReaderFactory.createReaderDescription;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.xml.sax.SAXException;


public class EnhancedHtmlReaderTest {
    public static void main(String[] args) throws UIMAException, IOException, SAXException {
        CollectionReaderDescription htmlReader = createReaderDescription(EnhancedHtmlReader.class,
        		EnhancedHtmlReader.PARAM_SOURCE_LOCATION, new File("test/in/strafkol.xml").toURI(),
        		EnhancedHtmlReader.PARAM_LANGUAGE, "de",
        		EnhancedHtmlReader.PARAM_SOURCE_ENCODING, "auto"
        );
        
        htmlReader.toXML(new FileWriter(new File("EnhancedHtmlReader.xml")));

//        AnalysisEngineDescription xmiWriter = createEngineDescription(XmiWriter.class,
//                XmiWriter.PARAM_TARGET_LOCATION, "test/out/",
//                XmiWriter.PARAM_OVERWRITE, true
//        );
//        SimplePipeline.runPipeline(htmlReader, xmiWriter);
    }
}