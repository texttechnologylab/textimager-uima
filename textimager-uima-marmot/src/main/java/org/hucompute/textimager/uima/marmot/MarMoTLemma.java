package org.hucompute.textimager.uima.marmot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import marmot.morph.MorphTagger;
import marmot.morph.Word;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ModelProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;



public class MarMoTLemma extends JCasAnnotator_ImplBase {

	/**
	 * Location from which the model is read.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;
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

	private CasConfigurableProviderBase<MorphTagger> modelProvider;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		// TODO Auto-generated method stub
		super.initialize(aContext);

		modelProvider = new ModelProviderBase<MorphTagger>(this, "marmot", "lemma")
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
		//		if(modelLocation == null){
		//			try {
		//				tagger_la = loadFromStream(new FileInputStream(new File("/home/team/models/lemmatizer/marmot/la/cap_all_lemmas.marmot")));
		//			} catch (FileNotFoundException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//			try {
		//				tagger_de = loadFromStream(new FileInputStream(new File("/home/team/models/lemmatizer/marmot/de/all-tiger_onlyLemmas-utf8.marmot")));
		//			} catch (FileNotFoundException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		}
		//		else{
		//			try {
		//				tagger = loadFromStream(new FileInputStream(new File(modelLocation)));
		//			} catch (FileNotFoundException e) {
		//				// TODO Auto-generated catch block
		//				e.printStackTrace();
		//			}
		//		}
	}


	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		modelProvider.configure(aJCas.getCas());

		for (Sentence sentence : select(aJCas, Sentence.class)) {
			List<Word> words = new ArrayList<>();
			List<Token>tokens =  JCasUtil.selectCovered(Token.class, sentence);
			for (Token token : tokens) {
				words.add(new Word(token.getCoveredText()));
			}
			List<List<String>> tags = modelProvider.getResource().tagWithLemma(new marmot.morph.Sentence(words));
			for (int i = 0; i < tags.size(); i++) {
				Lemma lemma = new Lemma(aJCas, tokens.get(i).getBegin(), tokens.get(i).getEnd());
				lemma.setValue(getLemma(tokens.get(i).getCoveredText(), tags.get(i).get(1)));
				lemma.addToIndexes();
				tokens.get(i).setLemma(lemma);
			}
		}
	}

	private String getLemma(String token,String input){
		String[]split = input.split("\\|");
		String lemma = token;
		if(split.length>1){
			lemma = split[1].split("=")[1].replace("_", "") + lemma.substring(Integer.parseInt(split[0].split("=")[1]));
			lemma = lemma.substring(0,lemma.length()-Integer.parseInt(split[2].split("=")[1]))+split[3].split("=")[1].replace("_", "") ;
		}
		return lemma;
	}
}
