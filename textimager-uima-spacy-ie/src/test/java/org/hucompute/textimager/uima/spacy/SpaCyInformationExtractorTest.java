package org.hucompute.textimager.uima.spacy;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.TypeSystemUtil;
import org.apache.uima.util.XMLSerializer;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.transform.OutputKeys;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertArrayEquals;

public class SpaCyInformationExtractorTest {
	@Test
	public void multiTaggerTest() throws UIMAException, IOException {
//		JCas cas = JCasFactory.createText("Dass Wir dieses bei rationeller Behandlung können, das bezeugen" +
//				" Palmen und viele andere Pflanzen der Tropen, die man früher ängstlich in geschlossenen und " +
//				"sehr warmen Häusern kultivierte und trotz alter Schonung und Sorgfalt doch nur selten eine" +
//				" schöne Pflanze heranzog. Streben des Gärtners muss sein, Alles, was fremde Länder an Pflanzen und" +
//				" Blumen besitzen, auch für die Kultur in unserem rauheren und ungünstigem Vaterlande möglich " +
//						"zu machen.", "de");
//		Path inputXmlDir = Paths.get("/mnt/ssd/SRL/bio");
		File folder = new File("/mnt/ssd/SRL/bio");
		for (File file : folder.listFiles()) {
			if (file.isFile() && file.getName().endsWith(".xmi")) {
				String content = FileUtils.readFileToString(file);
				System.out.println(file.getName());
				JCas cas = JCasFactory.createText(content, "de");

				AnalysisEngineDescription spacyIE = createEngineDescription(SpaCyInformationExtractor.class,
						SpaCyInformationExtractor.PARAM_DOCKER_HOST_PORT, 8000
				);
				SimplePipeline.runPipeline(cas, spacyIE);

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
				Path outputXmi = Paths.get("/mnt/ssd/SRL/bio/" + file.getName() + "_out.xmi");
				try (OutputStream outputStream = Files.newOutputStream(outputXmi)) {
					XMLSerializer xmlSerializer = new XMLSerializer(outputStream, true);
					xmlSerializer.setOutputProperty(OutputKeys.VERSION, "1.0");
					xmlSerializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
					XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(null);
					xmiCasSerializer.serialize(cas.getCas(), xmlSerializer.getContentHandler());
				} catch (SAXException e) {
					e.printStackTrace();
				}

				Path outputXml = Paths.get("/mnt/ssd/SRL/bio/" + file.getName() + "_out.xml");
//				Path outputXml = Paths.get("test.xml");
				try (OutputStream outputStreamTS = Files.newOutputStream(outputXml)) {
					TypeSystemUtil.typeSystem2TypeSystemDescription(cas.getTypeSystem()).toXML(outputStreamTS);
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}
		}
	}
}