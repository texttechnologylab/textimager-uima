package org.hucompute.textimager.uima.zemberek;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ModelProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.analysis.SentenceAnalysis;
import zemberek.morphology.analysis.WordAnalysis;
import zemberek.morphology.analysis.tr.TurkishMorphology;
import zemberek.morphology.analysis.tr.TurkishSentenceAnalyzer;

/**
 * 
 * @author Wahed Hemati
 *
 */

@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
		"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" },
		outputs = {"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"})
public class ZemberekNewPartOfSpeech extends JCasAnnotator_ImplBase {
	private MappingProvider mappingProvider;

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
	 * Load the part-of-speech tag to UIMA type mapping from this location instead of locating
	 * the mapping automatically.
	 */
	public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
	@ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
	protected String posMappingLocation;

	/**
	 * Location from which the model is read.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;


	/**
	 * Write part-of-speech information.
	 *
	 * Default: {@code true}
	 */
	public static final String PARAM_WRITE_POS = ComponentParameters.PARAM_WRITE_POS;
	@ConfigurationParameter(name=PARAM_WRITE_POS, mandatory=true, defaultValue="true")
	private boolean writePos;

	/**
	 * Write lemma information.
	 *
	 * Default: {@code true}
	 */
	public static final String PARAM_WRITE_MORPH = ComponentParameters.PARAM_WRITE_MORPH;
	@ConfigurationParameter(name=PARAM_WRITE_MORPH, mandatory=true, defaultValue="true")
	private boolean writeMorph;


	private CasConfigurableProviderBase<ZemberekNewPartOfSpeech> modelProvider;


	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		modelProvider = new ModelProviderBase<ZemberekNewPartOfSpeech>(this, "tagger", "pos")
		{
			@Override
			protected ZemberekNewPartOfSpeech produceResource(InputStream aStream) throws Exception {
				ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream(aStream));
				Object object = stream.readObject();
				stream.close();
				if (object == null) {
					throw new RuntimeException("Object couldn't be deserialized: ");
				}
				return (ZemberekNewPartOfSpeech) object;
			}
		};
		mappingProvider = MappingProviderFactory.createPosMappingProvider("src/main/resources/org/hucompute/textimager/uima/zemberek/lib/pos-tr-pretrained.map", language, modelProvider);
	}


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		modelProvider.configure(aJCas.getCas());
		mappingProvider.configure(aJCas.getCas());

        // Objects to create POS-Tags
 		TurkishMorphology morphology;
        Z3MarkovModelDisambiguator disambiguator;
        TurkishSentenceAnalyzer sentenceAnalyzer;
                
        try {
        	// Initialize
			morphology = TurkishMorphology.createWithDefaults();
			disambiguator = new Z3MarkovModelDisambiguator();
			sentenceAnalyzer = new TurkishSentenceAnalyzer(
	                morphology,
	                disambiguator
	        );
			
			// Analyze the sentence or text.
			for (Sentence sentence : select(aJCas, Sentence.class)) {
				// ZEMBEREK
				SentenceAnalysis analysis = sentenceAnalyzer.analyze(sentence.getCoveredText());
				sentenceAnalyzer.disambiguate(analysis);
				
				// Current Token
				int i = 0;
				
				// Create an ArrayList of all Token, because POS-library doesn't output begin/end of POS. Calculate it manually.
				ArrayList<Token> T = new ArrayList<Token>();
				for (Token token : select(aJCas, Token.class)) {
					T.add(token);
				}
				
				// Analyze Sentence
		        for (SentenceAnalysis.Entry entry : analysis) {	            
		        	// Analyze current Token
		        	WordAnalysis wa = entry.parses.get(0);
		        	
		        	// Create POS-Tag, only if we have a Token.
		        	if(T.size() > i) {		        		
		        		// Filter for secondaryPos
		        		if(wa.dictionaryItem.secondaryPos.toString().equals("None")) {
		        			Type posTag = mappingProvider.getTagType(wa.dictionaryItem.primaryPos + "");
							POS posElement = (POS) aJCas.getCas().createAnnotation(posTag, T.get(i).getBegin(), T.get(i).getEnd());
							posElement.addToIndexes();
							T.get(i).setPos(posElement);
		        		} else {
		        			Type posTag = mappingProvider.getTagType(wa.dictionaryItem.secondaryPos + "");
							POS posElement = (POS) aJCas.getCas().createAnnotation(posTag, T.get(i).getBegin(), T.get(i).getEnd());
							posElement.addToIndexes();
							T.get(i).setPos(posElement);
		        		}
		        	}            
		    		
		    		// Next Token
		        	i = i + 1;
		        }
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}		
}
