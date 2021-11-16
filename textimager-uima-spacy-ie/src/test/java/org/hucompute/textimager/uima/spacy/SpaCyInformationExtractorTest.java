package org.hucompute.textimager.uima.spacy;

import de.unihd.dbs.uima.annotator.heideltime2.HeidelTime;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;
import org.apache.uima.util.XMLSerializer;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
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
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SpaCyInformationExtractorTest {
	private final String sourceLocation = "/mnt/ssd/data/geonames.txt";
	@Test
	public void multiTaggerTest() throws UIMAException, IOException {
//		JCas cas = JCasFactory.createText("Dass Wir dieses bei rationeller Behandlung können, das bezeugen" +
//				" Palmen und viele andere Pflanzen der Tropen, die man früher ängstlich in geschlossenen und " +
//				"sehr warmen Häusern kultivierte und trotz alter Schonung und Sorgfalt doch nur selten eine" +
//				" schöne Pflanze heranzog. Streben des Gärtners muss sein, Alles, was fremde Länder an Pflanzen und" +
//				" Blumen besitzen, auch für die Kultur in unserem rauheren und ungünstigem Vaterlande möglich " +
//						"zu machen.", "de");
//		Path inputXmlDir = Paths.get("/mnt/ssd/SRL/bio");
//	File folder = new File("/mnt/ssd/SRL/data/biofid/");
	File folder = new File("/mnt/ssd/SRL/data/biofid_new/in");
		for (File file : folder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".xmi.gz")) {
				InputStream in = new GZIPInputStream(new FileInputStream(file));
				String content = FileUtils.readFileToString(file);
				System.out.println("**********************************");
				System.out.println(file.getName());
//				JCas cas = JCasFactory.createText(content, "de");

				JCas cas = JCasFactory.createJCas();
				CasIOUtils.load(in, cas.getCas());
//				CasIOUtil.readXmi (cas.getCas(),  file);
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
				AnalysisEngineDescription spacyIE = createEngineDescription(SpaCyInformationExtractor.class,
						SpaCyInformationExtractor.PARAM_DOCKER_HOST_PORT, 8000
				);
				System.out.println("**********************************");
//				SimplePipeline.runPipeline(cas, spacyIE, segmenter, heidelTime);
//				SimplePipeline.runPipeline(cas, heidelTime);
//				SimplePipeline.runPipeline(cas, spacyIE, heidelTime, segmenter, geoNames);
//				SimplePipeline.runPipeline(cas, geoNames);
				SimplePipeline.runPipeline(cas, spacyIE);
//				SimplePipeline.runPipeline(cas, segmenter, heidelTime, geoNames, spacyIE);

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
				Path outputXmi = Paths.get("/mnt/ssd/SRL/data/biofid_new/out/" + file.getName());
//						; + "_out.xmi");
				try (OutputStream outputStream = (new GZIPOutputStream(Files.newOutputStream(outputXmi)))) {
					XMLSerializer xmlSerializer = new XMLSerializer(outputStream, true);
					xmlSerializer.setOutputProperty(OutputKeys.VERSION, "1.0");
					xmlSerializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
					XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(null);
					xmiCasSerializer.serialize(cas.getCas(), xmlSerializer.getContentHandler());
				} catch (SAXException e) {
					e.printStackTrace();
				}

//				Path outputXml = Paths.get("/mnt/ssd/SRL/biofid_new/clean/out/" + file.getName());
//				Path outputXml = Paths.get("test.xml");
//				try (OutputStream outputStreamTS = Files.newOutputStream(outputXml)) {
//					TypeSystemUtil.typeSystem2TypeSystemDescription(cas.getTypeSystem()).toXML(outputStreamTS);
//				} catch (SAXException e) {
//					e.printStackTrace();
//				}
			}
		}
	}
}