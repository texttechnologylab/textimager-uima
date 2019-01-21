package org.hucompute.textimager.fasttext.languageidentification;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.services.type.Language;

import com.github.jfasttext.JFastText;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ModelProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;


public class LanguageIdentificationPercentage extends JCasAnnotator_ImplBase{

	/**
	 * Location from which the model is read.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;
	private CasConfigurableProviderBase<JFastText> modelProvider;

	/**
	 * Variant of a model the model. Used to address a specific model if here are multiple models
	 * for one language.
	 */
	public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;


	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		modelProvider = new ModelProviderBase<JFastText>()
		{
			{
				setContextObject(LanguageIdentificationPercentage.this);

				setDefault(ARTIFACT_ID, "${groupId}.fasttext-languageidentification-model-${language}-${variant}");
				setDefault(LOCATION,
						"classpath:/${package}/lib/languageidentification-${language}-${variant}.properties");
				setDefault(VARIANT, "small");

				setOverride(LOCATION, modelLocation);
				setOverride(LANGUAGE, "any");
				setOverride(VARIANT, variant);
			}

			@Override
			protected JFastText produceResource(URL aUrl)
					throws IOException
			{
				JFastText fasttext = new JFastText();
				File profileFolder = ResourceUtils.getUrlAsFile(aUrl, true);
				fasttext.loadModel(profileFolder.getAbsolutePath());
				return fasttext;
			}
		};
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		modelProvider.configure(aJCas.getCas());
		for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
			JFastText.ProbLabel probLabel = modelProvider.getResource().predictProba(sentence.getCoveredText());
			String foundLanguage = probLabel.label.replace("__label__", "");
			Language lang = new Language(aJCas);
			lang.setBegin(sentence.getBegin());
			lang.setEnd(sentence.getEnd());
			lang.setLanguage(foundLanguage);
			lang.addToIndexes(aJCas);
		}	
	}

}
