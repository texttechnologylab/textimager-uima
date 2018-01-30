package org.hucompute.textimager.uima.uaic;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.uaic.core.DictionaryProvider;
import org.hucompute.textimager.uima.uaic.core.ResourceStreamProvider;
import ro.uaic.info.nlptools.corpus.INlpSentence;
import ro.uaic.info.nlptools.corpus.InmemoryCorpus;
import ro.uaic.info.nlptools.ggs.engine.core.GGSException;
import ro.uaic.info.nlptools.postagger.UaicHybridPOStagger;
import ro.uaic.info.nlptools.tools.UaicTokenizer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * This class provides the part of speech recognition for the romanian language.
 *
 * We don't need any pre-process of the raw text prior to use this tool
 *
 * @author Dinu
 */
@TypeCapability(outputs = {
        "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"
})
public class PosTagger extends JCasAnnotator_ImplBase {

    /**
     * Specify and set up the configuration parameters
     */

    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    protected String language;

    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    protected String variant;

    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
    protected String modelLocation;


    public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
    @ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
    protected String posMappingLocation;


    public static final String PARAM_INTERN_TAGS = ComponentParameters.PARAM_INTERN_TAGS;
    @ConfigurationParameter(name = PARAM_INTERN_TAGS, mandatory = false, defaultValue = "true")
    private boolean internTags;


    public static final String PARAM_PRINT_TAGSET = ComponentParameters.PARAM_PRINT_TAGSET;
    @ConfigurationParameter(name = PARAM_PRINT_TAGSET, mandatory = true, defaultValue = "false")
    protected boolean printTagSet;

    private CasConfigurableProviderBase<File> modelProvider;
    private MappingProvider posMappingProvider;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        modelProvider = new CasConfigurableProviderBase<File>() {
            {
                setContextObject(PosTagger.this);

                setDefault(ARTIFACT_ID, "${groupId}.UIAC-raw-tagger-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/uaic/tagger-${variant}.model");
                setDefault(VARIANT, "default");

                setOverride(LOCATION, modelLocation);
                setOverride(LANGUAGE, language);
                setOverride(VARIANT, variant);
            }

            @Override
            protected File produceResource(URL aUrl) throws IOException {
                return ResourceUtils.getUrlAsFile(aUrl, true);
            }
        };

        posMappingProvider = MappingProviderFactory.createPosMappingProvider(posMappingLocation, language, modelProvider);
    }


    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        CAS cas = jCas.getCas();
        modelProvider.configure(cas);
        posMappingProvider.configure(cas);

        try {
            UaicHybridPOStagger tagger = new UaicHybridPOStagger(ResourceStreamProvider.getModelFile(), DictionaryProvider.getMorphologicalDictionary(), ResourceStreamProvider.getTagsetFile(), ResourceStreamProvider.getReductionFile());

            UaicTokenizer tokenizer = new UaicTokenizer(DictionaryProvider.getMorphologicalDictionary());
            InmemoryCorpus result = tokenizer.splitAndTokenize(jCas.getDocumentText());

            // Keep track of the current position relative to the complete text by adding the length of every
            // sentence to the total length. This corresponds the cursor movement over the text
            int textRelativePos = 0;
            for (int sentenceIdx = 0; sentenceIdx < result.getSentenceCount(); sentenceIdx++) {
                INlpSentence sentence = result.getSentence(sentenceIdx);

                for (int i = 0; i < sentence.getTokenCount(); i++) {
                    // We'll process every token with the PoS tool.
                    // The processing of the whole text simultaneously could cause massive performance issues
                    ro.uaic.info.nlptools.corpus.Token uaicToken = sentence.getToken(i);

                    String postag = tagger.tag(new String[]{uaicToken.getWordForm()})[0];
                    if (postag.length() > 2) {
                        // The PoS identifiers are the first 2 characters in case the whole PoS string is bigger
                        // Overwrite it
                        postag = postag.substring(0, 2);
                    }

                    Type posTag = posMappingProvider.getTagType(postag);
                    POS posElement = (POS) cas.createAnnotation(posTag, textRelativePos + uaicToken.getCharStartIndexInSentence(), textRelativePos +uaicToken.getCharEndIndexInSentence());
                    posElement.setPosValue(postag);
                    posElement.addToIndexes();
                }

                // "Move the cursor"
                textRelativePos += sentence.getToken(sentence.getTokenCount() - 1).getCharEndIndexInSentence() + 1;
            }

        } catch (IOException | GGSException | IllegalAccessException | InstantiationException e) {
            // Wrap the exception
            throw new AnalysisEngineProcessException(e);
        }
    }
}
