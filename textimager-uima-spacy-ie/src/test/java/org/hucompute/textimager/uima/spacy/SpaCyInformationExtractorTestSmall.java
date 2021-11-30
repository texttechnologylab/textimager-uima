package org.hucompute.textimager.uima.spacy;

import de.unihd.dbs.uima.annotator.heideltime.biofid.HeidelTime;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.TypeSystemUtil;
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

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SpaCyInformationExtractorTestSmall {
//	private final String sourceLocation = "/mnt/ssd/data/geonames_sample.txt";
	private final String sourceLocation = "/mnt/ssd/data/geonames/geonames.txt";
	@Test
	public void multiTaggerTest() throws UIMAException, IOException {
//		JCas cas = JCasFactory.createText("Man sollte meinen, dafs ein solches Gebiet eine artenreiche Flora " +
//				"dieser zierlichen Wassergewächse beherbergen müsse, umsomehr, " +
//			"da auch aus den benachbarten Gebieten verhältnismafsig viele Species " +
//						"bekannt geworden sind .", "de");
//		JCas cas = JCasFactory.createText("Dass Wir dieses bei rationeller Behandlung können, das bezeugen" +
//				" Palmen und viele andere Pflanzen der Tropen, die man früher ängstlich in geschlossenen und " +
//				"sehr warmen Häusern kultivierte und trotz alter Schonung und Sorgfalt doch nur selten eine" +
//				" schöne Pflanze heranzog. Streben des Gärtners muss sein, Alles, was fremde Länder an Pflanzen und" +
//				" Blumen besitzen, auch für die Kultur in unserem rauheren und ungünstigem Vaterlande möglich " +
//						"zu machen.", "de");
//		Path inputXmlDir = Paths.get("/mnt/ssd/SRL/bio");
//        JCas cas = JCasFactory.createText("Der Botaniker pflückt einen Apfel nicht.", "de");
        String text = "Man sollte meinen, dass ein solches Gebiet heute in Andorra eine artenreiche Flora dieser "+
				"zierlichen Wassergewächse nicht beherbergt, " +
				"umsomehr, da auch heute aus den benachbarten Gebieten verhältnismafsig viele Species bekannt geworden sind .";
//        String text = "Im Jahr 2020 schuldete er mir 2020 Euro.";
		text = "Andorra ist ein kleines Land.";
		text = "Heute ist Mittwoch.";
//		text = "Am Montag ist Carl für drei Wochen nach Spanien abgereist und kehrt erst am 23.02.2022 zurück.";
//		text = "Am Montag ist Carl für drei Wochen nach Spanien abgereist und ist bald wieder da.";
//		text = "Am Montag ist Carl für drei Wochen nach Spanien abgereist und kehrt erst am Freitag zurück.";
//		text = "Die Straße 'Unter den Linden' führt zum Brandenburger Tor.";
        text = "Was du heute kannst besorgen, das verschiebe nicht auf die Birne.";
		text = "Heute geht Australien unter.";
//        text = "Den Bayerischen Wald, so sagt Berlin, wird man nicht in die Liste von Naturschutzgebieten aufnehmen.";
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
//				GeonamesGazetteer.PARAM_ANNOTATION_COMMENTS, new String[]{ "ttlab_model", "ttlab_geonames_v_1.0.1" }
		);
//		SimplePipeline.runPipeline(cas, heidelTime, segmenter, geoNames);
//		SimplePipeline.runPipeline(cas, segmenter, heidelTime, geoNames, spacyIE);
//		SimplePipeline.runPipeline(cas, segmenter, heidelTime, spacyIE);
//		SimplePipeline.runPipeline(cas, segmenter, heidelTime);
		SimplePipeline.runPipeline(cas, segmenter, heidelTime, geoNames, spacyIE);

				//		for (Token t : JCasUtil.select(cas, Token.class)) {
				//			System.out.println("!~" + t.getCoveredText() + "!~");
				//			System.out.println(t);
				//		}

				//		for (SrLink l : JCasUtil.select(cas, SrLink.class)) {
				//			System.out.println("!~" + l.getGround().getCoveredText() + "!~");
				//			System.out.println("!~" + l.getFigure().getCoveredText() + "!~");
				//			System.out.println("!~" + l.getRel_type() + "!~");
				//			System.out.println(l);
				//		System.out.println(XmlFormatter.getPrettyString(cas));
				//		assertArrayEquals(tokens, casTokens);
//		Path inputXmlDir = Paths.get("/mnt/ssd/SRL/bio");
		Path outputXmi = Paths.get("/mnt/ssd/SRL/bio_test/test_in.xmi");
		try (OutputStream outputStream = Files.newOutputStream(outputXmi)) {
			XMLSerializer xmlSerializer = new XMLSerializer(outputStream, true);
			xmlSerializer.setOutputProperty(OutputKeys.VERSION, "1.0");
			xmlSerializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
			XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(null);
			xmiCasSerializer.serialize(cas.getCas(), xmlSerializer.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}

//		Path outputXml = Paths.get("/mnt/ssd/SRL/bio_test/test_in.xml");
//				Path outputXml = Paths.get("test.xml");
//		try (OutputStream outputStreamTS = Files.newOutputStream(outputXml)) {
//			TypeSystemUtil.typeSystem2TypeSystemDescription(cas.getTypeSystem()).toXML(outputStreamTS);
//		} catch (SAXException e) {
//			e.printStackTrace();
//				}
		}
}