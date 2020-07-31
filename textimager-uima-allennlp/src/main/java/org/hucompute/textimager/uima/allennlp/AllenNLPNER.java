package org.hucompute.textimager.uima.allennlp;

import static java.lang.Math.toIntExact;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.dkpro.core.api.parameter.ComponentParameters;
import jep.JepException;

public class AllenNLPNER extends AllenNLPBase {
	/**
	 * Overwrite CAS Language?
	 */
	public static final String PARAM_LANGUAGE = "language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	/**
	 * 
	 */
	public static final String PARAM_MODEL_LOCATION = "modelLocation";
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;

	/**
	 * Overwrite ner mapping location?
	 */
	public static final String PARAM_NER_MAPPING_LOCATION = "nerMappingLocation";
	@ConfigurationParameter(name = PARAM_NER_MAPPING_LOCATION, mandatory = false)
	protected String nerMappingLocation;

	/**
	 * Overwrite model variant?
	 */
	public static final String PARAM_VARIANT = "variant";
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	private MappingProvider nerMappingProvider;
	private CasConfigurableProviderBase<File> modelProvider;

	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		/*
		modelProvider = new CasConfigurableProviderBase<File>() {
			{
				setContextObject(StanzaTagger.this);

				//setDefault(ARTIFACT_ID, "${groupId}.OpenerProject-model-tagger-${language}-${variant}");
				setDefault(LOCATION,
						"classpath:org/hucompute/textimager/uima/stanza/lib/tagger-${variant}.model");
				setDefault(VARIANT, "default");

				setOverride(LOCATION, modelLocation);
				setOverride(LANGUAGE, language);
				setOverride(VARIANT, variant);
			}

			@Override
			protected File produceResource(URL aUrl) throws IOException {
				return ResourceUtils.getUrlAsFile(aUrl, true);
			}
		};*/

		nerMappingProvider = MappingProviderFactory.createNerMappingProvider(aContext,nerMappingLocation, variant, language);
        };


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		final CAS cas = aJCas.getCas();
		//modelProvider.configure(cas);
		//nerMappingProvider.configure(cas);
		try {
			final Object lang = aJCas.getDocumentLanguage();
			final Object text = aJCas.getDocumentText();
			interp.set("lang", lang);
			interp.set("text", text);
			interp.exec("predictor = Predictor.from_path('https://storage.googleapis.com/allennlp-public-models/ner-model-2020.02.10.tar.gz')");
			interp.exec("predicted = predictor.predict(sentence=text)");
			interp.exec("words = predicted.get('words')");
			interp.exec("tags = predicted.get('tags')");
			interp.exec("begin = 0");
			interp.exec("begin_list = []");
			interp.exec("end_list = []");
			for(int i = 0; i < toIntExact((long)interp.getValue("len(words)")); i++) {
				interp.set("i", i);
				interp.exec("begin_list.append(begin)");
				interp.exec("begin += len(words[i])");
				interp.exec("end_list.append(begin)");
				interp.exec("begin += 1");
			}
			interp.exec("token_list = [{'tags':words[i], 'words':tags[i], 'begin':begin_list[i], 'end': end_list[i]} for i in range(len(words))]");
			ArrayList<HashMap<String, Object>> tokenList = (ArrayList<HashMap<String, Object>>) interp.getValue("token_list");
			tokenList.forEach(token -> {
				String type = (String)token.get("tags");
				int begin = toIntExact((long)token.get("begin"));
				int end = toIntExact((long)token.get("end"));
				//Type neTag = nerMappingProvider.getTagType(type);
				//NamedEntity neAnno = (NamedEntity) aJCas.getCas().createAnnotation(neTag, begin, end);
				NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
				neAnno.setValue(type);
				neAnno.addToIndexes();
			});
		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}
