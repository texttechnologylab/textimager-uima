package org.hucompute.textimager.uima.talismane;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import com.joliciel.talismane.TalismaneException;
import com.joliciel.talismane.TalismaneSession;
import com.joliciel.talismane.machineLearning.Decision;
import com.joliciel.talismane.parser.ParseConfiguration;
import com.joliciel.talismane.parser.ParseTree;
import com.joliciel.talismane.parser.ParseTreeNode;
import com.joliciel.talismane.parser.Parser;
import com.joliciel.talismane.parser.Parsers;
import com.joliciel.talismane.posTagger.PosTagSequence;
import com.joliciel.talismane.posTagger.PosTaggedToken;
import com.joliciel.talismane.posTagger.UnknownPosTagException;
import com.joliciel.talismane.rawText.Sentence;
import com.joliciel.talismane.tokeniser.Token;
import com.joliciel.talismane.tokeniser.TokenSequence;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;

@TypeCapability(
inputs = {
		"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
		"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
		"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"},
outputs = {
		"de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency"})
public class TalismaneDependencyParser extends JCasAnnotator_ImplBase{
	/**
     * Path to Config File
     */
    public static final String PARAM_CONFIG_LOCATION = "PARAM_CONFIG_LOCATION";
    @ConfigurationParameter(name = PARAM_CONFIG_LOCATION, mandatory = false)
    protected String configLocation;
    
    /**
     * Log the tag set(s) when a model is loaded.
     *
     * Default: {@code false}
     */
    public static final String PARAM_PRINT_TAGSET = ComponentParameters.PARAM_PRINT_TAGSET;
    @ConfigurationParameter(name = PARAM_PRINT_TAGSET, mandatory = true, defaultValue="false")
    private boolean printTagSet;

    /**
     * Use this language instead of the document language to resolve the model and tag set mapping.
     */
    public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
    private String language;

    /**
     * Variant of a model the model. Used to address a specific model if here are multiple models
     * for one language.
     */
    public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
    @ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
    private String variant;

    /**
     * Location from which the model is read.
     */
    public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
    private String modelLocation;

    /**
     * Location of the mapping file for part-of-speech tags to UIMA types.
     */
    public static final String PARAM_DEPENDENCY_MAPPING_LOCATION = ComponentParameters.PARAM_DEPENDENCY_MAPPING_LOCATION;
    @ConfigurationParameter(name = PARAM_DEPENDENCY_MAPPING_LOCATION, mandatory = false)
    private String dependencyMappingLocation;
    
    /**
     * Use the {@link String#intern()} method on tags. This is usually a good idea to avoid
     * spaming the heap with thousands of strings representing only a few different tags.
     */
    public static final String PARAM_INTERN_TAGS = ComponentParameters.PARAM_INTERN_TAGS;
    @ConfigurationParameter(name = PARAM_INTERN_TAGS, mandatory = false, defaultValue = "true")
    private boolean internTags;

    /**
     * Process anyway, even if the model relies on features that are not supported by this
     * component.
     * 
     * Default: {@code false}
     */
    public static final String PARAM_IGNORE_MISSING_FEATURES = "ignoreMissingFeatures";
    @ConfigurationParameter(name = PARAM_IGNORE_MISSING_FEATURES, mandatory = true, defaultValue = "false")
    protected boolean ignoreMissingFeatures;
    
    private CasConfigurableProviderBase<File> modelProvider;
    private MappingProvider dependencyMappingProvider;
    

    @Override
    public void initialize(UimaContext aContext)
        throws ResourceInitializationException
    {
        super.initialize(aContext);

        modelProvider = new CasConfigurableProviderBase<File>()
        {
            {
                setContextObject(TalismaneDependencyParser.this);

                setDefault(ARTIFACT_ID, "${groupId}.talismane-model-dependency-${language}-${variant}");
                setDefault(LOCATION, "classpath:org/hucompute/textimager/uima/talismane/lib/"
                        + "dependency-${variant}.model");
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
         
        dependencyMappingProvider = MappingProviderFactory.createDependencyMappingProvider(dependencyMappingLocation,
                language, modelProvider);
    }

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		CAS cas = aJCas.getCas();
		modelProvider.configure(cas);
		dependencyMappingProvider.configure(cas);

		// load the Talismane configuration
	    Config conf = ConfigFactory.load(configLocation);
	    
	    //create session ID
  		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
  		String sessionId = timestamp.toString();
	    TalismaneSession session = null;
		try {
			session = new TalismaneSession(conf, sessionId);
		} catch (ClassNotFoundException | IOException | TalismaneException e1) {
			e1.printStackTrace();
		}

		
		for (de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence jCasSentence : 
			org.apache.uima.fit.util.JCasUtil.select(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence.class)) {
			
			List<de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS> jCasTokens = selectCovered(aJCas, 
					de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS.class, jCasSentence);
			
			int SentenceBegin = jCasSentence.getBegin();
			
			// Create Sequences for Talismane
			Sentence originalText = new Sentence(jCasSentence.getCoveredText(), session);
			TokenSequence tokenSequence = new TokenSequence(originalText, session);
			tokenSequence.cleanSlate();
			PosTagSequence posTagSequence = new PosTagSequence(tokenSequence);
			
			//Add token to PosTagSequence
			for(de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS jCasToken :jCasTokens) {
				Token token = tokenSequence.addToken(jCasToken.getBegin() - SentenceBegin, jCasToken.getEnd() - SentenceBegin);
				
				try {
					posTagSequence.addPosTaggedToken(new PosTaggedToken(token, new Decision(jCasToken.getPosValue()), session));
				} catch (UnknownPosTagException e) {
					e.printStackTrace();
				}
			}

			// Create Parser
			 Parser parser = null;
			try {
				parser = Parsers.getParser(session);
			} catch (ClassNotFoundException | IOException | TalismaneException e) {
				e.printStackTrace();
			}
			// Parse the Sentence
		     ParseConfiguration parseConfiguration = null;
			try {
				parseConfiguration = parser.parseSentence(posTagSequence);
			} catch (TalismaneException | IOException e) {
				e.printStackTrace();
			}
			
			//Get ParseTree and Root
		     ParseTree parseTree = new ParseTree(parseConfiguration, true);
		     ParseTreeNode root = parseTree.getRoot();
		     
		     //Write Annottaion to JCas
		     for(ParseTreeNode node : root.getChildren()) {
		    	 TreetoJCas(node, aJCas, SentenceBegin);
		     }	    
		
		}
			
	}
	/**
	 * This Procedure adds the Nodes to JCas
	 * @param node
	 * @param aJCas
	 * @param SentenceBegin
	 */
	private void TreetoJCas(ParseTreeNode node, JCas aJCas, int SentenceBegin) {
		// Get Begin and End from Parent- and Child-Node
		int pStart = node.getParent().getPosTaggedToken().getToken().getStartIndex() + SentenceBegin;
		int pEnd = node.getParent().getPosTaggedToken().getToken().getEndIndex() + SentenceBegin;
		int cStart = node.getPosTaggedToken().getToken().getStartIndex() + SentenceBegin;
		int cEnd = node.getPosTaggedToken().getToken().getEndIndex() + SentenceBegin;
		
		// Get Parent and Child Token from JCas
		de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token parent = null;
		if((pStart != pEnd)) {		
			parent = JCasUtil.selectSingleAt(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, pStart, pEnd);
		}
		else{
			parent = JCasUtil.selectSingleAt(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, cStart, cEnd);
		}
		
		de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token child = 
				JCasUtil.selectSingleAt(aJCas, de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token.class, cStart, cEnd);
		
		//Map the Dependency Tag and add the Annontaion to JCas
		CAS cas = aJCas.getCas();
		String tag = node.getLabel();
    	Type depTag = dependencyMappingProvider.getTagType(tag);
		System.out.println(tag +" - "+ depTag);
		Dependency dep = (Dependency) cas.createAnnotation(depTag, child.getBegin(), child.getEnd());
		dep.setDependencyType(node.getLabel());
		dep.setGovernor(parent);
		dep.setDependent(child);
		dep.addToIndexes();
		
		//Repeat for all child nodes
		for(ParseTreeNode cnode : node.getChildren()) {
			TreetoJCas(cnode, aJCas, SentenceBegin);
		}
	}

}
