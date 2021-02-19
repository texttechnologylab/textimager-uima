package org.hucompute.textimager.uima.biofid.gazetteer;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.Location;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
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
import org.texttechnologylab.annotation.type.Attribute_Property;
import org.texttechnologylab.annotation.type.Habitat;
import org.texttechnologylab.annotation.type.Taxon;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TestMultiGazetteer {
	
	@Test
	public void testAttribute_Property() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGazetteer.class,
					BiofidGazetteer.PARAM_SOURCE_LOCATION, "src/test/resources/ATTR.list",
					BiofidGazetteer.PARAM_TAGGING_TYPE_NAME, Attribute_Property.class.getName(),
					BiofidGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/uima/biofid/gazetteer/lib/ner-default.map",
					BiofidGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGazetteer.PARAM_USE_STRING_TREE, false,
					BiofidGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, true
			));
			
			runTest(gazetterEngine, Attribute_Property.class);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testLocation() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGazetteer.class,
					BiofidGazetteer.PARAM_SOURCE_LOCATION, "src/test/resources/LOC.list",
					BiofidGazetteer.PARAM_TAGGING_TYPE_NAME, Location.class.getName(),
					BiofidGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/uima/biofid/gazetteer/lib/ner-default.map",
					BiofidGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGazetteer.PARAM_USE_STRING_TREE, true,
					BiofidGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, false
			));
			
			runTest(gazetterEngine, Location.class);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testTaxon() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGazetteer.class,
					BiofidGazetteer.PARAM_SOURCE_LOCATION, "src/test/resources/TAX.list",
					BiofidGazetteer.PARAM_TAGGING_TYPE_NAME, Taxon.class.getName(),
					BiofidGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/uima/biofid/gazetteer/lib/ner-default.map",
					BiofidGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGazetteer.PARAM_USE_STRING_TREE, true,
					BiofidGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, false
			));
			
			runTest(gazetterEngine, Taxon.class);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testHabitat() {
		try {
			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGazetteer.class,
					BiofidGazetteer.PARAM_SOURCE_LOCATION, "src/test/resources/HAB.list",
					BiofidGazetteer.PARAM_TAGGING_TYPE_NAME, Habitat.class.getName(),
					BiofidGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/uima/biofid/gazetteer/lib/ner-default.map",
					BiofidGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGazetteer.PARAM_USE_STRING_TREE, true,
					BiofidGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, false
			));
			
			runTest(gazetterEngine, Habitat.class);
		} catch (UIMAException e) {
			e.printStackTrace();
		}
	}
	
	private void runTest(AnalysisEngine gazetterEngine, Class<? extends NamedEntity> clazz) throws UIMAException {
		String fname = "src/test/resources/text.xmi";
		try {
			File file = new File(fname);
			{
				JCas jCas = JCasFactory.createJCas();
				CasIOUtils.load(Files.newInputStream(file.toPath()), null, jCas.getCas(), true);
				jCas.removeAllIncludingSubtypes(NamedEntity.type);
				
				StopWatch stopWatch = StopWatch.createStarted();
				SimplePipeline.runPipeline(jCas, gazetterEngine);
				System.out.printf("Finished tagging in %dms.\n", stopWatch.getTime(TimeUnit.MILLISECONDS));
				
				XmiSerializationSharedData sharedData = new XmiSerializationSharedData();
				XmiCasSerializer.serialize(jCas.getCas(), jCas.getTypeSystem(), new FileOutputStream(new File("/tmp/temp.xmi")), true, sharedData);
				
				System.out.printf("Found %d %s.%n", JCasUtil.select(jCas, clazz).size(), clazz.getSimpleName());
				System.out.println(JCasUtil.select(jCas, clazz).stream().map(taxon -> String.format("%s@(%d, %d): %s", taxon.getCoveredText(), taxon.getBegin(), taxon.getEnd(), taxon.getValue())).collect(Collectors.joining("\n")));
			}
		} catch (IOException | SAXException e) {
			e.printStackTrace();
		}
	}
	
}
