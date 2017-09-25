package org.hucompute.textimager.uima.IXA;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import eus.ixa.ixa.pipe.lemma.StatisticalLemmatizer;
import eus.ixa.ixa.pipe.pos.Morpheme;
import eus.ixa.ixa.pipe.pos.Resources;
import eus.ixa.ixa.pipe.pos.StatisticalTagger;

public class IXAPOS extends JCasAnnotator_ImplBase {
    /**
     * Use this language instead of the document language to resolve the model.
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    /**
     * Override the default variant used to locate the model.
     */
    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;

    /**
     * Load the model from this location instead of locating the model automatically.
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
    protected String modelLocation;

    /**
     * Load the part-of-speech tag to UIMA type mapping from this location instead of locating the
     * mapping automatically.
     */
    public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
    @ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
    protected String posMappingLocation;

    /**
     * Use the {@link String#intern()} method on tags. This is usually a good idea to avoid spaming
     * the heap with thousands of strings representing only a few different tags.
     *
     * Default: {@code true}
     */
    public static final String PARAM_INTERN_TAGS = ComponentParameters.PARAM_INTERN_TAGS;
    @ConfigurationParameter(name = PARAM_INTERN_TAGS, mandatory = false, defaultValue = "true")
    private boolean internTags;

    /**
     * Log the tag set(s) when a model is loaded.
     *
     * Default: {@code false}
     */
    public static final String PARAM_PRINT_TAGSET = ComponentParameters.PARAM_PRINT_TAGSET;
    @ConfigurationParameter(name = PARAM_PRINT_TAGSET, mandatory = true, defaultValue = "false")
    protected boolean printTagSet;

    private CasConfigurableProviderBase<File> modelProvider;
    private MappingProvider posMappingProvider;

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);

        modelProvider = new CasConfigurableProviderBase<File>()
        {
            {
                setContextObject(IXAPOS.this);

                setDefault(ARTIFACT_ID, "${groupId}.IXA-model-tagger-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/IXA/lib/"
                        + "tagger-${variant}.model");
                setDefault(VARIANT, "default");

                setOverride(LOCATION, modelLocation);
                setOverride(LANGUAGE, language);
                setOverride(VARIANT, variant);
            }

            @Override
            protected File produceResource(URL aUrl)
                throws IOException
            {
                return ResourceUtils.getUrlAsFile(aUrl, true);
            }
        };

        
		if(posMappingLocation == null) 
			posMappingLocation="classpath:/org/hucompute/textimager/uima/IXA/lib/pos-default.map";
        posMappingProvider = MappingProviderFactory.createPosMappingProvider(posMappingLocation,
                language, modelProvider);
    }

	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Needed for Mapping
		CAS cas = aJCas.getCas();
		modelProvider.configure(cas);
		posMappingProvider.configure(cas);
		
		// Get JCas text and language
		String language = aJCas.getDocumentLanguage();
		String originalText = aJCas.getDocumentText();

		if(modelLocation == null) 
			modelLocation="classpath:/org/hucompute/textimager/uima/IXA/morph-models-1.5.0/";
		// Set Properties for pipe
		Properties properties = new Properties();
		properties.setProperty("language", language);
		properties.setProperty("model", 
				modelLocation+language+"/"+language+"-pos-perceptron.bin");
		properties.setProperty("lemmatizerModel", 
				modelLocation+language+"/"+language+"-lemma-perceptron.bin");
		
		StatisticalTagger tagger = new StatisticalTagger(properties);
		
		for (Sentence jCasSentence : JCasUtil.select(aJCas, Sentence.class)) {
			List<Token> jCasTokens = selectCovered(aJCas, Token.class, jCasSentence);
			List<String> tokenList = new ArrayList<String>();
			for(Token jCasToken :jCasTokens) {
				tokenList.add(jCasToken.getCoveredText());				
			}
			List<String> posTags = tagger.posAnnotate(tokenList.toArray(new String[] {}));
			//List<Morpheme> morphTags = tagger.getMorphemesFromStrings(posTags, tokenList.toArray(new String[] {}));
			
			StatisticalLemmatizer lemmatizer= new StatisticalLemmatizer(properties);
			List<String>  lemmaTags = lemmatizer.lemmatize(tokenList.toArray(new String[] {}), posTags.toArray(new String[] {}));
			
			int i = 0;
			for(Token jCasToken :jCasTokens) {
				int begin = jCasToken.getBegin();
				int end = jCasToken.getEnd();
				
				String tag = posTags.get(i);
				tag = Resources.getKafTagSet(tag, aJCas.getDocumentLanguage());
	        	Type posTag = posMappingProvider.getTagType(tag);
                POS posAnno = (POS) cas.createAnnotation(posTag, begin, end);
                posAnno.setPosValue(tag);
                posAnno.addToIndexes();
				
				Lemma lemma = new Lemma(aJCas, begin, end);
				lemma.setValue(lemmaTags.get(i));
				lemma.addToIndexes();

				i++;
							
			}
		}
		
		
	}

}
