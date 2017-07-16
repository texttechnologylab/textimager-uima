package main;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.services.util.XmlFormatter;
import org.hucompute.textimager.uima.resha_turkish_stemmer.ReshaStemmer;

/**
* Pipeline
*
* @date 29.05.2017
*
* @author Alexander Sang
* @version 1.0
*
* Central class for the analyzation of text.
*
*/
public class Pipeline {
	
	public static void main(String[] args) throws UIMAException, CASRuntimeException, IOException {		
		// String text = "Türkiye ya da resmî adıyla Türkiye Cumhuriyeti, topraklarının büyük bölümü Anadolu'ya, küçük bir bölümü ise Balkanlar'ın uzantısı olan Trakya'ya yayılmış bir ülke. Vikipedi'nin güvenilebilirliği ve doğruluğu üzerine tartışmalar mevcuttur ve site yoğun olarak vandalizme maruz kalmaktadır.";	
		
		// Create a new Engine Description for the Tokenizer.
		AnalysisEngineDescription stemAnnotator = createEngineDescription(ReshaStemmer.class);
		
		// Create a new JCas - "Holder"-Class for Annotation. 
		JCas inputCas = JCasFactory.createJCas();
		
		// Input
		inputCas.setDocumentText(FileUtils.readFileToString(new File("src/main/resources/input.txt")));
				
		inputCas.setDocumentLanguage("tr");
		// Pipeline
		SimplePipeline.runPipeline(inputCas, stemAnnotator);
		
		// Output as XML
		String output = XmlFormatter.getPrettyString(inputCas.getCas());
		System.out.println(output);
		
		// Ausgabe in eine Datei schreiben.
		File file = new File("Output.xml");
		try {
			FileUtils.writeStringToFile(file, output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
