package org.hucompute.textimager.uima.spacy;

import de.unihd.dbs.uima.annotator.heideltime.biofid.HeidelTime;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.XMLSerializer;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.hucompute.textimager.uima.geonames.gazetteer.GeonamesGazetteer;
import org.junit.Test;
import org.texttechnologylab.annotation.GeoNamesEntity;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SpaCyInformationExtractorTestSmallMulti {
	private final String sourceLocation = "/mnt/ssd/data/geonames.txt";
	@Test
	public void multiIETest() throws UIMAException, IOException {
		ArrayList<String> texts = new ArrayList<String>(Arrays.asList(
				"Andorra liegt zwischen Spanien und Frankreich.",

				"Der Atlantik ist ein großer Ozean.",

				"Die Straße 'Unter den Linden' führt zum Brandenburger Tor.",

				"Den Bayerischen Wald, so sagt Berlin, wird man nicht in die Liste von Naturschutzgebieten aufnehmen.",

				"'Leuphana' ist der Name einer Universität.",

				"Das Pferderennen gestern gewann Robert Schumann auf 'Prinz von Hamburg'.",

				"Der Name der kleinen Kirche lautet 'Kleine Kirche'.",

				"Im Bergedorfer Gehölz ist eine Lichtung mit Weihnachtsbäumen bepflanzt worden.",

				"Eine der beliebtesten Touristenattraktionen Deutschlands, wenn nicht weltweit, ist Schloss Neuschwanstein."
		));
		int i = 0;
		for (String text : texts) {
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
			SimplePipeline.runPipeline(cas, segmenter, heidelTime, geoNames, spacyIE);

			i++;
			Path outputXmi = Paths.get("/mnt/ssd/SRL/bio_test/ie/test_" + i + ".xmi");
			try (OutputStream outputStream = Files.newOutputStream(outputXmi)) {
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
