package org.hucompute.textimager.uima.zemberek;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.util.JCasUtil.select;

import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

/**
* ZemberekPartOfSpeech
*
* @date 03.08.2017
*
* @author Alexander Sang
* @version 1.2
*
* This class provide POS for turkish language. 
* UIMA-Token, UIMA-Sentence are needed as input to create POS.
* UIMA-Standard is used to represent the final POS.
*/
@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"},
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"})
public class ZemberekPartOfSpeech  extends JCasAnnotator_ImplBase {
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
                setContextObject(ZemberekPartOfSpeech.this);

                setDefault(ARTIFACT_ID, "${groupId}.Zemberek-model-tagger-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/zemberek/lib/"
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

        posMappingProvider = MappingProviderFactory.createPosMappingProvider(posMappingLocation, language, modelProvider);
    }
	
    /**
	 * Analyze the sentences and create POS for every token. After successfully creation, map and add POS to JCas.
	 * @param aJCas
	 */
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {		
			// Variables for mapping
			CAS cas = aJCas.getCas();
			modelProvider.configure(cas);
			posMappingProvider.configure(cas);
	        try {
	        	// Initialize Zemberek
	        	TurkishMorphology morphology = TurkishMorphology.createWithDefaults();
	        	Z3MarkovModelDisambiguator disambiguator = new Z3MarkovModelDisambiguator();
	        	TurkishSentenceAnalyzer sentenceAnalyzer = new TurkishSentenceAnalyzer(morphology, disambiguator);

				
				// Create an ArrayList of all token, because POS-library doesn't output begin/end of POS. Calculate it manually.
				ArrayList<Token> T = new ArrayList<Token>();
				for (Token token : select(aJCas, Token.class)) {
					T.add(token);
				}
				
				// Current
				int i = 0;
				
				// Analyze the sentences.
				for (Sentence sentence : select(aJCas, Sentence.class)) {
					SentenceAnalysis analysis = sentenceAnalyzer.analyze(sentence.getCoveredText());
					sentenceAnalyzer.disambiguate(analysis);
					
					// Analyze sentence
			        for (SentenceAnalysis.Entry entry : analysis) {	            
			        	// Analyze current token
			        	WordAnalysis wa = entry.parses.get(0);
			        	
			        	// If we have a token, create a corresponding POS-Tag.
			        	if(T.size() > i) {		        		
			        		// Filter for primaryPos and secondaryPos
			        		if(wa.dictionaryItem.secondaryPos.toString().equals("None")) {
			        			String tag = wa.dictionaryItem.primaryPos + "";
			        			Type posTag = posMappingProvider.getTagType(tag);
								POS posElement = (POS) cas.createAnnotation(posTag, T.get(i).getBegin(), T.get(i).getEnd());
								posElement.setPosValue(tag);
								posElement.addToIndexes();
			        		} else {
			        			String tag = wa.dictionaryItem.secondaryPos + "";
			        			Type posTag = posMappingProvider.getTagType(tag);
								POS posElement = (POS) aJCas.getCas().createAnnotation(posTag, T.get(i).getBegin(), T.get(i).getEnd());
								posElement.setPosValue(tag);
								posElement.addToIndexes();
			        		}
			        	}            
			        	
			    		// Next
			        	i = i + 1;
			        }
				}		
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
}
