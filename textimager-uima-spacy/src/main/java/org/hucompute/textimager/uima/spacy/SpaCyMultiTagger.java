package org.hucompute.textimager.uima.spacy;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.lexmorph.pos.POSUtils;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import jep.JepException;

import java.util.ArrayList;
import java.util.HashMap;

public class SpaCyMultiTagger extends SpaCyBase {
	/**
	 * Overwrite CAS Language?
	 */
	public static final String PARAM_LANGUAGE = "language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	/**
	 * Overwrite POS mapping location?
	 */
	public static final String PARAM_POS_MAPPING_LOCATION = "posMappingLocation";
	@ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
	protected String posMappingLocation;

	/**
	 * Overwrite model variant?
	 */
	public static final String PARAM_VARIANT = "variant";
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	private MappingProvider mappingProvider;

	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		// TODO defaults for de (stts) and en (ptb) are ok, add own language mapping later
		mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext, posMappingLocation, variant,
				language);
	}

	private void processToken(JCas aJCas) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> output = (ArrayList<HashMap<String, Object>>) interpreter.getValue("tokens");
		for (HashMap<String, Object> token : output) {
			if (!(Boolean) token.get("is_space")) {
				int begin = ((Long) token.get("idx")).intValue();
				int end = begin + ((Long) token.get("length")).intValue();
				Token casToken = new Token(aJCas, begin, end);
				casToken.addToIndexes();
			}
		}
	}

	private void processPOS(JCas aJCas) throws AnalysisEngineProcessException, JepException {
		mappingProvider.configure(aJCas.getCas());
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> poss = (ArrayList<HashMap<String, Object>>) interpreter.getValue("pos");
		poss.forEach(p -> {
			if (!(Boolean) p.get("is_space")) {
				int begin = ((Long) p.get("idx")).intValue();
				int end = begin + ((Long) p.get("length")).intValue();
				String tagStr = p.get("tag").toString();

				Type posTag = mappingProvider.getTagType(tagStr);
				POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
				posAnno.setPosValue(tagStr);
				POSUtils.assignCoarseValue(posAnno);
				posAnno.addToIndexes();
			}
		});
	}

	private void processDep(JCas aJCas) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> deps = (ArrayList<HashMap<String, Object>>) interpreter.getValue("deps");
		deps.forEach(dep -> {
			if (!(Boolean) dep.get("is_space")) {
				String depStr = dep.get("dep").toString().toUpperCase();

				int begin = ((Long) dep.get("idx")).intValue();
				int end = begin + ((Long) dep.get("length")).intValue();

				@SuppressWarnings("unchecked")
				HashMap<String, Object> headToken = (HashMap<String, Object>) dep.get("head");
				int beginHead = ((Long) headToken.get("idx")).intValue();
				int endHead = beginHead + ((Long) headToken.get("length")).intValue();

				Token dependent = JCasUtil.selectSingleAt(aJCas, Token.class, begin, end);
				Token governor = JCasUtil.selectSingleAt(aJCas, Token.class, beginHead, endHead);

				Dependency depAnno;
				if (depStr.equals("ROOT")) {
					depAnno = new ROOT(aJCas, begin, end);
					depAnno.setDependencyType("--");
				} else {
					depAnno = new Dependency(aJCas, begin, end);
					depAnno.setDependencyType(depStr);
				}
				depAnno.setDependent(dependent);
				depAnno.setGovernor(governor);
				depAnno.setFlavor(DependencyFlavor.BASIC);
				depAnno.addToIndexes();
			}
		});
	}

	private void processNER(JCas aJCas) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> entss = (ArrayList<HashMap<String, Object>>) interpreter.getValue("ents");
		entss.forEach(p -> {
			int begin = ((Long) p.get("start_char")).intValue();
			int end = ((Long) p.get("end_char")).intValue();
			String labelStr = p.get("label").toString();
			NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
			neAnno.setValue(labelStr);
			neAnno.addToIndexes();
		});
	}

	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		try {
			interpreter.set("lang", (Object)aJCas.getDocumentLanguage());
			interpreter.set("text", (Object)aJCas.getDocumentText());
			if (aJCas.getDocumentLanguage().equals("de"))
				interpreter.exec("nlp = spacy.load('de_core_news_sm')");
			else
				interpreter.exec("nlp = spacy.load('en_core_web_sm')");

			interpreter.exec("doc = nlp(text)");

			interpreter.exec("tokens = [{'idx': token.idx,'length': len(token),'is_space': token.is_space} for token in doc]");
			interpreter.exec("pos = [{'tag': token.tag_,'idx': token.idx,'length': len(token),'is_space': token.is_space}for token in doc]");
			interpreter.exec("deps = [{'dep': token.dep_,'idx': token.idx,'length': len(token),'is_space': token.is_space,'head': {'idx': token.head.idx,'length': len(token.head),'is_space': token.head.is_space}}	for token in doc]");
			interpreter.exec("ents = [{'start_char': ent.start_char,'end_char': ent.end_char,'label': ent.label_}for ent in doc.ents]");
			
			// Tokenizer
			processToken(aJCas);

			// Tagger
			processPOS(aJCas);

			// PARSER
			processDep(aJCas);

			// NER
			processNER(aJCas);
			
		} catch (JepException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
