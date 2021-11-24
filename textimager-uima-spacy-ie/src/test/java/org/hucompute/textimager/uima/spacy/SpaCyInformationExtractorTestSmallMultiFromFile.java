package org.hucompute.textimager.uima.spacy;

import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.unihd.dbs.uima.annotator.heideltime2.HeidelTime;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.TypeSystemUtil;
import org.apache.uima.util.XMLSerializer;
import org.hucompute.textimager.uima.geonames.gazetteer.GeonamesGazetteer;
import org.junit.Test;
import org.texttechnologylab.annotation.GeoNamesEntity;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SpaCyInformationExtractorTestSmallMultiFromFile {
	private final String sourceLocation = "/mnt/ssd/data/geonames.txt";
	@Test
	public void multiIETest() throws UIMAException, IOException {
//		String path = "/mnt/ssd/SRL/example_s.txt";
//		List<String> texts = Files.readAllLines(Paths.get(path), StandardCharsets.UTF_8);
		ArrayList<String> texts = new ArrayList<String>();
        String t = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(
//				new FileInputStream("/mnt/ssd/SRL/example_s.txt"), StandardCharsets.ISO_8859_1))){
				new FileInputStream("/mnt/ssd/SRL/bio_test/conll/conll.txt"), StandardCharsets.UTF_8))){
//		try (BufferedReader br = new BufferedReader(new FileReader("/mnt/ssd/SRL/example_s.txt"))) {
			String line = br.readLine();
			texts.add(line);
			while (line != null) {
				t = t + " " + line;
				line = br.readLine();
				texts.add(line);
			}
		}
//        System.out.print(t.substring(0, 66));
//		ArrayList<String> texts = new ArrayList<String>();
//		texts.add(t);
//		if (true) {return;}
		int i = 0;
		for (String text : texts) {
//		    if (i > 6) {break;}
		    System.out.println(text);
//			if (true) {continue;}
			JCas cas = JCasFactory.createText(text, "de");
			AnalysisEngineDescription spacyIE = createEngineDescription(SpaCyInformationExtractor.class,
						SpaCyInformationExtractor.PARAM_DOCKER_HOST_PORT, 8000
			);
			AnalysisEngineDescription heidelTime = createEngineDescription(HeidelTime.class);
			AnalysisEngineDescription segmenter = createEngineDescription(LanguageToolSegmenter.class);
			AnalysisEngineDescription geoNames = createEngineDescription(
					GeonamesGazetteer.class,
					GeonamesGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					GeonamesGazetteer.PARAM_TAGGING_TYPE_NAME, GeoNamesEntity.class.getName(),
					GeonamesGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/uima/geonames/gazetteer/lib/ner-default.map",
					GeonamesGazetteer.PARAM_USE_LOWERCASE, false,
					GeonamesGazetteer.PARAM_USE_STRING_TREE, true,
					GeonamesGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, true,
					GeonamesGazetteer.PARAM_USE_LEMMATA, true,
					GeonamesGazetteer.PARAM_NO_SKIPGRAMS, true,
					GeonamesGazetteer.PARAM_ADD_ABBREVIATED_TAXA, false,
					GeonamesGazetteer.PARAM_GET_ALL_SKIPS, false,
					GeonamesGazetteer.PARAM_MIN_LENGTH, 1,
					GeonamesGazetteer.PARAM_SPLIT_HYPEN, false
			);
//			SimplePipeline.runPipeline(cas, spacyIE, heidelTime, segmenter, geoNames);
			SimplePipeline.runPipeline(cas, spacyIE);
//			SimplePipeline.runPipeline(cas, segmenter, geoNames, heidelTime, spacyIE);

			i++;
			Path outputXmi = Paths.get("/mnt/ssd/SRL/bio_test/example_s/conll_test_new_geonames/" + i + ".xmi");
			try (OutputStream outputStream = Files.newOutputStream(outputXmi)) {
			    System.out.println("Saving " + i + ".xmi");
				XMLSerializer xmlSerializer = new XMLSerializer(outputStream, true);
				xmlSerializer.setOutputProperty(OutputKeys.VERSION, "1.0");
				xmlSerializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
				XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(null);
				xmiCasSerializer.serialize(cas.getCas(), xmlSerializer.getContentHandler());
			} catch (SAXException e) {
				e.printStackTrace();
			}

//			Path outputXml = Paths.get("/mnt/ssd/SRL/bio_test/ie/test_" + i + ".xml");
//			try (OutputStream outputStreamTS = Files.newOutputStream(outputXml)) {
//				TypeSystemUtil.typeSystem2TypeSystemDescription(cas.getTypeSystem()).toXML(outputStreamTS);
//			} catch (SAXException e) {
//				e.printStackTrace();
//					}
			}
	}
}