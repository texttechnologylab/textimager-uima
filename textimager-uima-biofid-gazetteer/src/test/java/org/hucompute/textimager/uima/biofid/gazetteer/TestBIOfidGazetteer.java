package org.hucompute.textimager.uima.biofid.gazetteer;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.cas.impl.XmiSerializationSharedData;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.annotation.type.Taxon;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

public class TestBIOfidGazetteer {
	
	private final String sourceLocation = "src/test/resources/taxa.zip";
//    private String sourceLocation = "https://www.texttechnologylab.org/files/BIOfidTaxa.zip";
	
	@Test
	public void testStringGazetteer() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGazetteer.class,
					BiofidGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BiofidGazetteer.PARAM_TAGGING_TYPE_NAME, Taxon.class.getName(),
					BiofidGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/biofid/lib/ner-default.map",
					BiofidGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGazetteer.PARAM_USE_STRING_TREE, true,
					BiofidGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, true,
					BiofidGazetteer.PARAM_USE_LEMMATA, false
			));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testStringGazetteerLemma() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGazetteer.class,
					BiofidGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BiofidGazetteer.PARAM_TAGGING_TYPE_NAME, Taxon.class.getName(),
					BiofidGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/biofid/lib/ner-default.map",
					BiofidGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGazetteer.PARAM_USE_STRING_TREE, true,
					BiofidGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, true,
					BiofidGazetteer.PARAM_USE_LEMMATA, true
			));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testStringGazetteerDocumentLevel() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGazetteer.class,
					BiofidGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BiofidGazetteer.PARAM_TAGGING_TYPE_NAME, Taxon.class.getName(),
					BiofidGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/biofid/lib/ner-default.map",
					BiofidGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGazetteer.PARAM_USE_STRING_TREE, true,
					BiofidGazetteer.PARAM_USE_LEMMATA, false
			));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
			fail();
		}
	}

//	@Test
//	public void testStringGazetteerV2() {
//		try {
//			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
//					BIOfidTreeGazetteerV2.class,
//					BIOfidTreeGazetteerV2.PARAM_SOURCE_LOCATION, sourceLocation,
//					BIOfidTreeGazetteerV2.PARAM_TAGGING_TYPE_NAME, Taxon.class.getName(),
//					BIOfidTreeGazetteerV2.PARAM_USE_LOWERCASE, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_STRING_TREE, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_SENTECE_LEVEL_TAGGING, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_LEMMATA, false
//			));
//
//			runTest(gazetterEngine);
//		} catch (UIMAException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	public void testStringGazetteerLemmaV2() {
//		try {
//			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
//					BIOfidTreeGazetteerV2.class,
//					BIOfidTreeGazetteerV2.PARAM_SOURCE_LOCATION, sourceLocation,
//					BIOfidTreeGazetteerV2.PARAM_TAGGING_TYPE_NAME, Taxon.class.getName(),
//					BIOfidTreeGazetteerV2.PARAM_USE_LOWERCASE, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_STRING_TREE, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_SENTECE_LEVEL_TAGGING, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_LEMMATA, true
//			));
//
//			runTest(gazetterEngine);
//		} catch (UIMAException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
//
//	@Test
//	public void testStringGazetteerDocumentLevelV2() {
//		try {
//			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
//					BIOfidTreeGazetteerV2.class,
//					BIOfidTreeGazetteerV2.PARAM_SOURCE_LOCATION, sourceLocation,
//					BIOfidTreeGazetteerV2.PARAM_TAGGING_TYPE_NAME, Taxon.class.getName(),
//					BIOfidTreeGazetteerV2.PARAM_USE_LOWERCASE, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_STRING_TREE, true,
//					BIOfidTreeGazetteerV2.PARAM_USE_LEMMATA, false
//			));
//
//			runTest(gazetterEngine);
//		} catch (UIMAException e) {
//			e.printStackTrace();
//			fail();
//		}
//	}
	
	private void runTest(AnalysisEngine gazetterEngine) throws UIMAException {
		for (String fname : Arrays.asList("src/test/resources/9031034.xmi", "src/test/resources/4058393.xmi")) {
			try {
				File file = new File(fname);
				{
					JCas jCas = JCasFactory.createJCas();
					CasIOUtils.load(java.nio.file.Files.newInputStream(file.toPath()), null, jCas.getCas(), true);
					
					jCas.removeAllIncludingSubtypes(Taxon.type);
					
					StopWatch stopWatch = StopWatch.createStarted();
					SimplePipeline.runPipeline(jCas, gazetterEngine);
					XmiSerializationSharedData sharedData = new XmiSerializationSharedData();
					XmiCasSerializer.serialize(jCas.getCas(), jCas.getTypeSystem(), new FileOutputStream(new File("/tmp/temp.xmi")), true, sharedData);
					System.out.printf("Finished tagging in %dms.\n", stopWatch.getTime(TimeUnit.MILLISECONDS));
					
					System.out.printf("Found %d taxa.\n", JCasUtil.select(jCas, Taxon.class).size());
					System.out.println(JCasUtil.select(jCas, Taxon.class).stream().map(taxon -> String.format("%s@(%d, %d): %s", taxon.getCoveredText(), taxon.getBegin(), taxon.getEnd(), taxon.getValue())).collect(Collectors.joining("\n")));
				}
			} catch (IOException | SAXException e) {
				e.printStackTrace();
				fail();
			}
		}
//		try {
//			JCas jCas = JCasFactory.createText("Ihnen folgen Gypcrus pannonieus, Festuca amethystina, F. elatior, F. ovina, Poa bulbosa, P. pratensis, Ayroslis spica venti u. s. w.");
//			jCas.addFsToIndexes(new Sentence(jCas, 0, jCas.getDocumentText().length() - 1));
//
//			StopWatch stopWatch = StopWatch.createStarted();
//			SimplePipeline.runPipeline(jCas, gazetterEngine);
//			System.out.printf("Finished tagging in %dms.\n", stopWatch.getTime(TimeUnit.MILLISECONDS));
//
//			System.out.printf("Found %d taxa.\n", JCasUtil.select(jCas, Taxon.class).size());
//			System.out.println(JCasUtil.select(jCas, Taxon.class).stream().map(taxon -> String.format("%s@(%d, %d): %s", taxon.getCoveredText(), taxon.getBegin(), taxon.getEnd(), taxon.getValue())).collect(Collectors.joining("\n")));
//
//
//		} catch (UIMAException e) {
//			e.printStackTrace();
//		}
	}
	
}
