package org.hucompute.textimager.uima.flair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.resource.ResourceInitializationException;
import org.texttechnologylab.annotation.NamedEntity;
import org.texttechnologylab.annotation.type.*;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import jep.JepException;

import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.MappingProvider;

public class FlairNERBiofid extends FlairBase {
	public static final String PARAM_CACHE_MODELS = "cacheModels";
	@ConfigurationParameter(name = PARAM_CACHE_MODELS, mandatory = true, defaultValue = "false")
	protected boolean cacheModels;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		mappingProvider = new MappingProvider();
		mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/org/textimager/uima/flair/ner-biofid.map");
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, NamedEntity.class.getName());
		mappingProvider.setDefault(MappingProvider.LANGUAGE, "de");

		if (StringUtils.isNotBlank(pMappingProviderLocation))
			mappingProvider.setOverride(MappingProvider.LOCATION, pMappingProviderLocation);
		if (StringUtils.isNotBlank(language))
			mappingProvider.setOverride(MappingProvider.LANGUAGE, language);

		try {
			if (cacheModels) {
				System.out.println("using CachedMultiModel");
				interpreter.exec(String.format("model = CachedMultiModel('%s')", modelLocation));
			}
			else {
				System.out.println("using MultiModel");
				interpreter.exec(String.format("model = MultiModel('%s')", modelLocation));
			}
		} catch (JepException e) {
			throw new ResourceInitializationException(e);
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {
		mappingProvider.configure(jCas.getCas());

		Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
		List<Integer> offsets = sentences.stream().map(Sentence::getBegin).collect(Collectors.toList());
		List<String> sentenceStrings = sentences.stream().map(Sentence::getCoveredText).collect(Collectors.toList());

		try {
			ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) interpreter.invoke("model.tag",
					sentenceStrings, offsets);

			for (int i = 0; i < result.size(); i++) {
				ArrayList<String> entry = result.get(i);

				String tagValue = entry.get(0);
				int begin = Integer.parseInt(entry.get(1));
				int end = Integer.parseInt(entry.get(2));

				Type tagType = mappingProvider.getTagType(tagValue);
				AnnotationFS annotation = jCas.getCas().createAnnotation(tagType, begin, end);
				annotation.setStringValue(tagType.getFeatureByBaseName("value"), tagValue);
				jCas.addFsToIndexes(annotation);
			}
		} catch (JepException | ClassCastException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}

}
