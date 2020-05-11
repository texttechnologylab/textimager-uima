package org.biofid.gazetteer;

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
import org.junit.Test;
import org.texttechnologylab.annotation.type.Taxon;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TestBIOfidGazetteer {
	
	private String sourceLocation = "src/test/resources/taxa.zip";
//    private String sourceLocation = "https://www.texttechnologylab.org/files/BIOfidTaxa.zip";
	
	//    @Test
	public void testRegularGazetteer() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BIOfidGazetteer.class,
					BIOfidGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BIOfidGazetteer.PARAM_USE_LOWERCASE, false));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testCharGazetteer() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BIOfidTreeGazetteer.class,
					BIOfidTreeGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BIOfidTreeGazetteer.PARAM_USE_LOWERCASE, false,
					BIOfidTreeGazetteer.PARAM_USE_STRING_TREE, false,
					BIOfidTreeGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, true
			));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCharGazetteerDocumentLevel() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BIOfidTreeGazetteer.class,
					BIOfidTreeGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BIOfidTreeGazetteer.PARAM_USE_LOWERCASE, false,
					BIOfidTreeGazetteer.PARAM_USE_STRING_TREE, false
			));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStringGazetteer() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BIOfidTreeGazetteer.class,
					BIOfidTreeGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BIOfidTreeGazetteer.PARAM_USE_LOWERCASE, false,
					BIOfidTreeGazetteer.PARAM_USE_STRING_TREE, true,
					BIOfidTreeGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, true
			));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testStringGazetteerDocumentLevel() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BIOfidTreeGazetteer.class,
					BIOfidTreeGazetteer.PARAM_SOURCE_LOCATION, sourceLocation,
					BIOfidTreeGazetteer.PARAM_USE_LOWERCASE, false,
					BIOfidTreeGazetteer.PARAM_USE_STRING_TREE, true
			));
			
			runTest(gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
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
			}
		}
	}
	
}
