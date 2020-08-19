package org.hucompute.textimager.uima.spacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
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
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import jep.JepException;

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
	
	/**
	 * Max Text Length
	 */
	public static final String PARAM_MAX_TEXT_LENGTH = "maxTextLength";
	@ConfigurationParameter(name = PARAM_MAX_TEXT_LENGTH, defaultValue = "-1")
	protected long maxTextLength;

	private MappingProvider mappingProvider;

	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		// TODO defaults for de (stts) and en (ptb) are ok, add own language mapping later
		mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext, posMappingLocation, variant,
				language);
	}

	private void processToken(JCas aJCas, int beginOffset) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> output = (ArrayList<HashMap<String, Object>>) interpreter.getValue("tokens");
		for (HashMap<String, Object> token : output) {
			if (!(Boolean) token.get("is_space")) {
				int begin = ((Long) token.get("idx")).intValue() + beginOffset;
				int end = begin + ((Long) token.get("length")).intValue();
				Token casToken = new Token(aJCas, begin, end);
				casToken.addToIndexes();
			}
		}
	}

	private void processPOS(JCas aJCas, int beginOffset) throws AnalysisEngineProcessException, JepException {
		mappingProvider.configure(aJCas.getCas());
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> poss = (ArrayList<HashMap<String, Object>>) interpreter.getValue("pos");
		poss.forEach(p -> {
			if (!(Boolean) p.get("is_space")) {
				int begin = ((Long) p.get("idx")).intValue() + beginOffset;
				int end = begin + ((Long) p.get("length")).intValue();
				String tagStr = p.get("tag").toString();

				Type posTag = mappingProvider.getTagType(tagStr);
				POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
				posAnno.setPosValue(tagStr);
				POSUtils.assignCoarseValue(posAnno);
				posAnno.addToIndexes();
			}
		});
		
		// add pos refs to tokens
		Map<POS, Collection<Token>> posTokenMap = JCasUtil.indexCovering(aJCas, POS.class, Token.class);
		for (Map.Entry<POS, Collection<Token>> entry : posTokenMap.entrySet()) {
			POS posAnno = entry.getKey();
			for (Token tokenAnno : entry.getValue()) {
				tokenAnno.setPos(posAnno);
			}
		}
	}

	private void processDep(JCas aJCas, int beginOffset) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> deps = (ArrayList<HashMap<String, Object>>) interpreter.getValue("deps");
		deps.forEach(dep -> {
			if (!(Boolean) dep.get("is_space")) {
				String depStr = dep.get("dep").toString().toUpperCase();

				int begin = ((Long) dep.get("idx")).intValue() + beginOffset;
				int end = begin + ((Long) dep.get("length")).intValue();

				@SuppressWarnings("unchecked")
				HashMap<String, Object> headToken = (HashMap<String, Object>) dep.get("head");
				int beginHead = ((Long) headToken.get("idx")).intValue() + beginOffset;
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

	private void processNER(JCas aJCas, int beginOffset) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> entss = (ArrayList<HashMap<String, Object>>) interpreter.getValue("ents");
		entss.forEach(p -> {
			int begin = ((Long) p.get("start_char")).intValue() + beginOffset;
			int end = ((Long) p.get("end_char")).intValue() + beginOffset;
			String labelStr = p.get("label").toString();
			NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
			neAnno.setValue(labelStr);
			neAnno.addToIndexes();
		});
	}
	
	private void processSentences(JCas aJCas, int beginOffset) throws JepException {
		@SuppressWarnings("unchecked")
		ArrayList<HashMap<String, Object>> sents = (ArrayList<HashMap<String, Object>>) interpreter.getValue("sents");
		sents.forEach(p -> {
			int begin = ((Long) p.get("begin")).intValue() + beginOffset;
			int end = ((Long) p.get("end")).intValue() + beginOffset;
			Sentence sentAnno = new Sentence(aJCas, begin, end);
			sentAnno.addToIndexes();
		});
	}

	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		/// DEBUG START
		// remove all token, sentences, pos, ner, dep
		//for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
		//	sentence.removeFromIndexes();
		//}
		//for (NamedEntity ne : JCasUtil.select(aJCas, NamedEntity.class)) {
		//	ne.removeFromIndexes();
		//}
		//for (POS pos : JCasUtil.select(aJCas, POS.class)) {
		//	pos.removeFromIndexes();
		//}
		//for (Dependency dep : JCasUtil.select(aJCas, Dependency.class)) {
		//	dep.removeFromIndexes();
		//}
		//for (ROOT dep : JCasUtil.select(aJCas, ROOT.class)) {
		//	dep.removeFromIndexes();
		//}
		//for (Token token : JCasUtil.select(aJCas, Token.class)) {
		//	token.removeFromIndexes();
		//}
		/// DEBUG END
		
		long textLength = aJCas.getDocumentText().length();
		System.out.println("text length: " + textLength);
		// abort on empty
		if (textLength < 1) {
			System.out.println("skipping spacy due to text length < 1");
			return;
		}
				
		try {
			interpreter.set("lang", (Object)aJCas.getDocumentLanguage());
			if (aJCas.getDocumentLanguage().equals("de"))
				interpreter.exec("nlp = spacy.load('de_core_news_sm')");
			else
				interpreter.exec("nlp = spacy.load('en_core_web_sm')");
			
			int spacyMaxLength = interpreter.getValue("nlp.max_length", Integer.class);
			//int spacyMaxLength = 20;
			System.out.println("Spacy max length is " + spacyMaxLength);

			// set nlp length to text lenght to allow complete text if needed
			if (textLength > spacyMaxLength) {
				interpreter.exec("nlp.max_length = " + String.valueOf(textLength+100));
				interpreter.exec("print('max length is', nlp.max_length)");
			}
			
			List<String> texts = new ArrayList<>();
			if (textLength > spacyMaxLength) {
				int textLimit = spacyMaxLength / 2;
				System.out.println("Text limit is: " + textLimit);
				// split text on "." near "nlp.max_length (= " characters
				StringBuilder sb = new StringBuilder();
				String[] textParts = aJCas.getDocumentText().split("\\.", 0);
				for (String textPart : textParts) {
					if (sb.length() >= textLimit) {
						texts.add(sb.toString());
						sb.setLength(0);
					}
					boolean isFirst = sb.length() == 0;
					if (!isFirst) {
						sb.append(" ");
					}
					sb.append(textPart).append(".");
				}
				// handle rest
				if (sb.length() > 0) {
					texts.add(sb.toString());
				}
			}
			else {
				texts.add(aJCas.getDocumentText());
			}

			int beginOffset = 0;
			int counter = 0;
			for (String text : texts) {
				counter++;
				System.out.println("processing text part " + counter + "/" + texts.size());
				//System.out.println(text);
				
				// count spaces on left side for offset, remove spaces for space
				int len = text.length();
				text = StringUtils.stripStart(text, null);
				int strippedSpaces = len - text.length();
				beginOffset += strippedSpaces;
				System.out.println("stripped " + strippedSpaces + " left spaces");
				
				// text to python interpreter
				interpreter.set("text", (Object)text);
				interpreter.exec("doc = nlp(text)");
	
				// prepare annotations for retrieval
				interpreter.exec("tokens = [{'idx': token.idx,'length': len(token),'is_space': token.is_space} for token in doc]");
				interpreter.exec("pos = [{'tag': token.tag_,'idx': token.idx,'length': len(token),'is_space': token.is_space}for token in doc]");
				interpreter.exec("deps = [{'dep': token.dep_,'idx': token.idx,'length': len(token),'is_space': token.is_space,'head': {'idx': token.head.idx,'length': len(token.head),'is_space': token.head.is_space}}	for token in doc]");
				interpreter.exec("ents = [{'start_char': ent.start_char,'end_char': ent.end_char,'label': ent.label_}for ent in doc.ents]");
				interpreter.exec("sents = [{'begin': sent.start_char, 'end': sent.end_char} for sent in doc.sents]");
				
				// Tokenizer
				processToken(aJCas, beginOffset);
	
				// Tagger
				processPOS(aJCas, beginOffset);
	
				// PARSER
				processDep(aJCas, beginOffset);
	
				// NER
				processNER(aJCas, beginOffset);
				
				// Sentences
				processSentences(aJCas, beginOffset);
				
				beginOffset += text.length();
			}
			
		} catch (JepException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
