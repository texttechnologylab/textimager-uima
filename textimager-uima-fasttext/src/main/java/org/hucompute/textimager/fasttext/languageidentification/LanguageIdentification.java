package org.hucompute.textimager.fasttext.languageidentification;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.ModelProviderBase;
import org.dkpro.core.api.resources.ResourceUtils;

import com.github.jfasttext.JFastText;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;



public class LanguageIdentification extends JCasAnnotator_ImplBase{
	
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
				setContextObject(LanguageIdentification.this);

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
		JFastText.ProbLabel probLabel = modelProvider.getResource().predictProba(aJCas.getDocumentText());
		try{
			DocumentMetaData.get(aJCas).setLanguage(probLabel.label.replace("__label__", ""));
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
		aJCas.setDocumentLanguage(probLabel.label.replace("__label__", ""));
	}

}
