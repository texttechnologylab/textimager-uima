package org.hucompute.textimager.uima.talismane;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.fit.util.JCasUtil.*;

import com.joliciel.talismane.AnnotatedText;
import com.joliciel.talismane.TalismaneException;
import com.joliciel.talismane.TalismaneSession;
import com.joliciel.talismane.parser.ParseConfiguration;
import com.joliciel.talismane.parser.ParseTree;
import com.joliciel.talismane.parser.Parser;
import com.joliciel.talismane.parser.Parsers;
import com.joliciel.talismane.posTagger.PosTag;
import com.joliciel.talismane.posTagger.PosTagSequence;
import com.joliciel.talismane.posTagger.PosTaggedToken;
import com.joliciel.talismane.posTagger.PosTagger;
import com.joliciel.talismane.posTagger.PosTaggers;
import com.joliciel.talismane.posTagger.UnknownPosTagException;
import com.joliciel.talismane.rawText.RawText;
import com.joliciel.talismane.rawText.RawTextAnnotator;
import com.joliciel.talismane.rawText.Sentence;
import com.joliciel.talismane.sentenceAnnotators.SentenceAnnotator;
import com.joliciel.talismane.sentenceAnnotators.SentenceAnnotatorLoadException;
import com.joliciel.talismane.sentenceDetector.SentenceDetector;
import com.joliciel.talismane.tokeniser.Token;
import com.joliciel.talismane.tokeniser.TokenSequence;
import com.joliciel.talismane.tokeniser.Tokeniser;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;

@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence"},
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.Lemma"})
public class TalismanePOS extends SegmenterBase {
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
                setContextObject(TalismanePOS.this);

                setDefault(ARTIFACT_ID, "${groupId}.talismane-model-tagger-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/talismane/lib/"
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


        posMappingProvider = MappingProviderFactory.createPosMappingProvider(posMappingLocation,
                language, modelProvider);
    }

	
	

	/**
	 * funtion to add a Lemma to JCas
	 * 
	 * @param aJCas
	 * @param token
	 * @return true if lemma added to JCas
	 */
	private boolean addLemmatoJCas(JCas aJCas,PosTaggedToken token){
		
		if(token.getLemmaForCoNLL() != "_") {			
			Lemma lemma = new Lemma(aJCas, token.getToken().getStartIndex(),token.getToken().getEndIndex());
			lemma.setValue(token.getLemmaForCoNLL());
			lemma.addToIndexes();
			return true;
		}
		
		return false;
	}
	
	/**
	 * 
	 * funtion to add a POS to JCas
	 * 
	 * @param aJCas
	 * @param token
	 */
	private void addPOStoJCas(JCas aJCas,PosTaggedToken token){
		
		if(token.getDecision().getOutcome() != null) {			
			int begin = token.getToken().getStartIndex();
			int end = token.getToken().getEndIndex();
			CAS cas = aJCas.getCas();
			
			String tag = token.getDecision().getOutcome();
        	Type posTag = posMappingProvider.getTagType(tag);
            POS posAnno = (POS) cas.createAnnotation(posTag, begin, end);
            posAnno.setPosValue(tag);
            posAnno.addToIndexes();

		}
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		
		CAS cas = aJCas.getCas();
		modelProvider.configure(cas);
		posMappingProvider.configure(cas);
		

		// arbitrary session id
	    String sessionId = "";

	    // load the Talismane configuration
	    Config conf = ConfigFactory.load("org/hucompute/textimager/uima/talismane/talismane-fr-4.1.0.conf");
	    TalismaneSession session = null;
		try {
			session = new TalismaneSession(conf, sessionId);
			} 
			catch (ClassNotFoundException e) {e.printStackTrace();} 
			catch (SentenceAnnotatorLoadException e) {e.printStackTrace();} 
			catch (IOException e) {e.printStackTrace();} 
			catch (TalismaneException e) {e.printStackTrace();}

		//Get JCas Sentences
		
		for (de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence jCasSentence : 
			org.apache.uima.fit.util.JCasUtil.select(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence.class)) {
			
			List<de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token> jCasTokens = selectCovered(aJCas, 
					de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, jCasSentence);
			
			//create Sentence and TokenSequence
			Sentence originalText = new Sentence(jCasSentence.getCoveredText(), session);
			TokenSequence tokenSequence = new TokenSequence(originalText, session);
			//create Sentence and TokenSequence with Lower Case
			Sentence originalTextLower = new Sentence(jCasSentence.getCoveredText().toLowerCase(), session);
			TokenSequence tokenSequenceLower = new TokenSequence(originalTextLower, session);
			
			
			for(de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token jCasToken :jCasTokens) {
				tokenSequence.addToken(jCasToken.getBegin(), jCasToken.getEnd());	
			}
			
		    // init Pos Tagger
		    PosTagger posTagger = null;
			try {
				posTagger = PosTaggers.getPosTagger(session);
				} 
				catch (ClassNotFoundException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();} 
				catch (TalismaneException e) {e.printStackTrace();}
		
			
			// -- Lematise text 
			
		    PosTagSequence posTagSequence = null;
			try {
				posTagSequence = posTagger.tagSentence(tokenSequence);
				} 
				catch (UnknownPosTagException e) {e.printStackTrace();} 
				catch (TalismaneException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();}

			// Add to JCAS      
			for (PosTaggedToken token : posTagSequence) {
				
				addPOStoJCas(aJCas, token);
				
				boolean add = addLemmatoJCas(aJCas, token);
		    	if(!add){
		    		tokenSequenceLower.addToken(token.getToken().getStartIndex(), token.getToken().getEndIndex());
		    		}	  		
	  		}
			
			// Lemmatize to Lower
			if(tokenSequence.size() > 0) {
				posTagSequence.clear();
				try {
					posTagSequence = posTagger.tagSentence(tokenSequenceLower);
					} 
				catch (UnknownPosTagException e) {e.printStackTrace();} 
				catch (TalismaneException e) {e.printStackTrace();} 
				catch (IOException e) {e.printStackTrace();}
				
				// Add to JCAS
				for (PosTaggedToken token : posTagSequence) { 
					
					boolean add = addLemmatoJCas(aJCas, token);
					if(!add) {			
						int start = token.getToken().getStartIndex();
						int end = token.getToken().getEndIndex();
						Lemma lemma = new Lemma(aJCas, start,end);
						lemma.setValue(tokenSequence.getSentence().getRawInput(start, end));
						lemma.addToIndexes();
					}
					
		  		}
	      }
			
			
		}

		



	    
	}
}
		
	


