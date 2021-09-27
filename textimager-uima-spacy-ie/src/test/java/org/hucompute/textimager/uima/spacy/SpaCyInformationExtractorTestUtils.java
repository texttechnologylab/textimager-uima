package org.hucompute.textimager.uima.spacy;

import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.unihd.dbs.uima.annotator.heideltime2.HeidelTime;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.TypeSystemUtil;
import org.apache.uima.util.XMLSerializer;
import org.hucompute.textimager.uima.geonames.gazetteer.GeonamesGazetteer;
import org.junit.Test;
import org.texttechnologylab.annotation.GeoNamesEntity;
import org.texttechnologylab.annotation.administration.FinishAnnotation;
import org.texttechnologylab.annotation.type.Fingerprint;
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

public class SpaCyInformationExtractorTestUtils {
	@Test
	public void multiTaggerTest() throws UIMAException, IOException {
        String text = "Man sollte meinen im Jahr 2020 in Andorra, dass ein solches Gebiet eine artenreiche Flora dieser zierlichen Wassergewächse nicht beherbergt, " +
				"umsomehr, da auch heute aus den benachbarten Gebieten verhältnismafsig viele Species bekannt geworden sind .";
//		JCas cas = JCasFactory.createText(text, "de");
		String inputXmi = "/mnt/ssd/SRL/bio_test/test_in.xmi";
		JCas cas = JCasFactory.createJCas();
		CasIOUtil.readXmi(cas.getCas(), new File((inputXmi)));
		AnalysisEngineDescription spacyIE = createEngineDescription(SpaCyInformationExtractor.class,
					SpaCyInformationExtractor.PARAM_DOCKER_HOST_PORT, 8000
		);
		// Iteration über alle Views
//		cas.getViewIterator().forEachRemaining(view->{

			// Extraktion des Finish-Status
//			JCasUtil.select(view, FinishAnnotation.class).forEach(a->{
//				System.out.println(a.getTool());
//			});

			// Alle User-Annotierten Annotationen
//			JCasUtil.select(view, Fingerprint.class).stream().forEach(fp->{
				// Ausgabe der Referenz (was wurde annotiert)
//				System.out.println(fp.getReference().getType());
//			});

//		});


		SimplePipeline.runPipeline(cas, spacyIE);

//		AggregateBuilder builder = new AggregateBuilder();
		// Hier kommt dein Werkzeug hinein.
//		builder.add(createEngineDescription(LanguageToolSegmenter.class));


		// Die Pipeline läuft....
//		SimplePipeline.runPipeline(pCas, builder.createAggregate());


		// Überprüfung ob deine Annotationen stimmen.
//		JCasUtil.select(cas, Annotation.class).stream().forEach(a->{
//			System.out.println(a);
//		});
//		SimplePipeline.runPipeline(cas, heidelTime);
//		SimplePipeline.runPipeline();

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
		Path outputXmi = Paths.get("/mnt/ssd/SRL/bio_test/test_out.xmi");
		try (OutputStream outputStream = Files.newOutputStream(outputXmi)) {
			XMLSerializer xmlSerializer = new XMLSerializer(outputStream, true);
			xmlSerializer.setOutputProperty(OutputKeys.VERSION, "1.0");
			xmlSerializer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.toString());
			XmiCasSerializer xmiCasSerializer = new XmiCasSerializer(null);
			xmiCasSerializer.serialize(cas.getCas(), xmlSerializer.getContentHandler());
		} catch (SAXException e) {
			e.printStackTrace();
		}

		Path outputXml = Paths.get("/mnt/ssd/SRL/bio_test/test_out.xml");
//				Path outputXml = Paths.get("test.xml");
		try (OutputStream outputStreamTS = Files.newOutputStream(outputXml)) {
			TypeSystemUtil.typeSystem2TypeSystemDescription(cas.getTypeSystem()).toXML(outputStreamTS);
		} catch (SAXException e) {
			e.printStackTrace();
				}
		}
}