package org.hucompute.textimager.uima.biofid.general.gazetteer;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.dkpro.core.languagetool.LanguageToolLemmatizer;
import org.dkpro.core.languagetool.LanguageToolSegmenter;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.annotation.AnnotationComment;
import org.texttechnologylab.annotation.NamedEntity;
import org.texttechnologylab.annotation.type.Communication;
import org.texttechnologylab.annotation.type.Motive;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TestBiofidGeneralGazetteer {

	@Test
	public void testBiofidGeneralGazetteer() {
		try {
			final AnalysisEngine segmewnter = AnalysisEngineFactory.createEngine(LanguageToolSegmenter.class);
			final AnalysisEngine lemmatizer = AnalysisEngineFactory.createEngine(LanguageToolLemmatizer.class);

			final AnalysisEngine gazetterEngine = AnalysisEngineFactory.createEngine(AnalysisEngineFactory.createEngineDescription(
					BiofidGeneralGazetteer.class,
					BiofidGeneralGazetteer.PARAM_SOURCE_LOCATION, new String[] {
							"/home/daniel/data/hiwi/biofid/Motive.gazetteer.txt",
							"/home/daniel/data/hiwi/biofid/Kommunikation.gazetteer.txt"
					},
					BiofidGeneralGazetteer.PARAM_CLASS_MAPPING, new String[] {
							Motive.class.getName(),
							Communication.class.getName()
					},
					BiofidGeneralGazetteer.PARAM_MAPPING_PROVIDER_LOCATION, "classpath:/org/hucompute/textimager/uima/biofid/general/gazetteer/lib/ner-default.map",
					BiofidGeneralGazetteer.PARAM_USE_LOWERCASE, true,
					BiofidGeneralGazetteer.PARAM_USE_STRING_TREE, true,
					BiofidGeneralGazetteer.PARAM_USE_SENTECE_LEVEL_TAGGING, false,
					BiofidGeneralGazetteer.PARAM_USE_LEMMATA, true,
					BiofidGeneralGazetteer.PARAM_MIN_LENGTH, 4
					//BiofidGeneralGazetteer.PARAM_TOKEN_BOUNDARY_REGEX, "(\\p{PUNCT})|(\\s+)",
					//BiofidGeneralGazetteer.PARAM_RETOKENIZE, true,
					//BiofidGeneralGazetteer.PARAM_SPLIT_HYPEN, false
			));

			runTest(segmewnter, lemmatizer, gazetterEngine);
		} catch (UIMAException e) {
			e.printStackTrace();
			fail();
		}
	}

	private void runTest(AnalysisEngine segmenter, AnalysisEngine lemmatizer, AnalysisEngine gazetterEngine) throws UIMAException {
		JCas jCas = JCasFactory.createText("Dies ist ein Testsatz mit dem Grund, oder Vorhaben und Intention (Plan), den Gazetteer-Ansatz hervorheben, ihn zu erklären und wir hoffen auf eine Begründung, ohne die wir einen Vorwand bräuchten.");
		jCas.setDocumentLanguage("de");

		StopWatch stopWatch = StopWatch.createStarted();
		SimplePipeline.runPipeline(jCas, segmenter, lemmatizer, gazetterEngine);
		System.out.printf("Finished tagging in %dms.\n", stopWatch.getTime(TimeUnit.MILLISECONDS));

		System.out.printf("Found %d Named Entities.\n", JCasUtil.select(jCas, NamedEntity.class).size());
		System.out.println(JCasUtil.select(jCas, NamedEntity.class).stream().map(element -> String.format("%s (%d, %d): %s", element.getCoveredText(), element.getBegin(), element.getEnd(), element.getType().getName())).collect(Collectors.joining("\n")));

		System.out.printf("Found %d AnnotationComments.\n", JCasUtil.select(jCas, AnnotationComment.class).size());
		System.out.println(JCasUtil.select(jCas, AnnotationComment.class).stream().map(element -> String.format("%s: %s = %s", element.getReference(), element.getKey(), element.getValue())).collect(Collectors.joining("\n")));

		String[] expectedNames = new String[] {
				// Text, class
				"Grund", Motive.class.getName(),
				"Vorhaben", Motive.class.getName(),
				"Intention", Motive.class.getName(),
				"Intention", Communication.class.getName(),
				"Plan", Motive.class.getName(),				// Wurde nicht erkannt weil nur 4 Zeichen und min=5...
				"hervorheben", Communication.class.getName(),
				"erklären", Communication.class.getName(),
				"hoffen auf", Communication.class.getName(),
				"Begründung", Motive.class.getName(),
				"Vorwand", Motive.class.getName()
		};

		String[] resultNames = JCasUtil
				.select(jCas, NamedEntity.class)
				.stream()
				.flatMap(p -> Arrays.stream(new String[]{p.getCoveredText(), p.getType().getName()}))
				.toArray(String[]::new);

		Arrays.stream(resultNames).forEach(System.out::println);

		assertArrayEquals(expectedNames, resultNames);

		String[] expectedComments = new String[] {
				// Text, key, value
				"Grund", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"Vorhaben", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"Intention", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"Intention", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"Plan", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"hervorheben", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"erklären", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"hoffen auf", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"Begründung", "ttlab_model", "ttlab_biofid_general_v_1.0.1",
				"Vorwand", "ttlab_model", "ttlab_biofid_general_v_1.0.1"
		};

		String[] resultComments = JCasUtil
				.select(jCas, AnnotationComment.class)
				.stream()
				.flatMap(p -> Arrays.stream(new String[]{((NamedEntity)p.getReference()).getCoveredText(), p.getKey(), p.getValue()}))
				.toArray(String[]::new);

		assertArrayEquals(expectedComments, resultComments);
	}

}
