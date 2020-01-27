package org.hucompute.textimager.uima.marmot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.lexmorph.morph.MorphologicalFeaturesParser;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.dkpro.core.api.resources.ModelProviderBase;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import marmot.morph.MorphTagger;
import marmot.morph.Word;

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
public class MarMoTTagger extends JCasAnnotator_ImplBase {
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
	@ConfigurationParameter(name=PARAM_WRITE_MORPH, mandatory=true, defaultValue="false")
	private boolean writeMorph;
	
	public static final String PARAM_MAX_SENTENCE_LENGTH = "PARAM_MAX_SENTENCE_LENGTH";
	@ConfigurationParameter(name = PARAM_MAX_SENTENCE_LENGTH, mandatory = false, defaultValue="-1")
	protected int maximumSentenceLength;

	private CasConfigurableProviderBase<MorphTagger> modelProvider;
    private MorphologicalFeaturesParser featuresParser;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		super.initialize(aContext);

		modelProvider = new ModelProviderBase<MorphTagger>(this, "marmot", "pos")
		{
			@Override
			protected MorphTagger produceResource(InputStream aStream) throws Exception {
				ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream(aStream));
				Object object = stream.readObject();
				stream.close();
				if (object == null) {
					throw new RuntimeException("Object couldn't be deserialized: ");
				}
				return (MorphTagger) object;
			}
		};
		mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext,posMappingLocation,
				language,modelProvider);
		mappingProvider.setDefaultVariantsLocation(
				"classpath:/org/hucompute/textimager/uima/marmot/lib/pos-default-variants.map");
        featuresParser = new MorphologicalFeaturesParser(this, modelProvider);
	}
	
	int processed = 0;


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		modelProvider.configure(aJCas.getCas());
		mappingProvider.configure(aJCas.getCas());
        featuresParser.configure(aJCas.getCas());

		for (Sentence sentence : select(aJCas, Sentence.class)) {
			List<Word> words = new ArrayList<>();
			List<Token>tokens =  JCasUtil.selectCovered(Token.class, sentence);
			if(maximumSentenceLength > 0 && tokens.size() > maximumSentenceLength){
				for (Token token : tokens) {
					token.removeFromIndexes();
				}
				sentence.removeFromIndexes();
				continue;
			}
			
			for (Token token : tokens) {
				words.add(new Word(token.getCoveredText()));
			}
			List<List<String>> tags = modelProvider.getResource().tag(new marmot.morph.Sentence(words));
			for (int i = 0; i < tags.size(); i++) {
				//Add the POS
				if(writePos){
//					Type posTag = mappingProvider.getTagType(tags.get(i).get(0));
//					POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, tokens.get(i).getBegin(), tokens.get(i).getEnd());
					POS posAnno = new POS(aJCas,tokens.get(i).getBegin(), tokens.get(i).getEnd());
					posAnno.setPosValue(tags.get(i).get(0));
					posAnno.addToIndexes();
					tokens.get(i).setPos(posAnno);
				}
				
				//Add Morph
				if(writeMorph){
                    MorphologicalFeatures analysis = featuresParser
                            .parse(aJCas, tokens.get(i), tags.get(i).get(1));
                    tokens.get(i).setMorph(analysis);
				}
			}
		}
	}		
}
