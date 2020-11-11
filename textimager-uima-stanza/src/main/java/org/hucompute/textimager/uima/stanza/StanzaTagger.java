package org.hucompute.textimager.uima.stanza;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import jep.JepException;

import static java.lang.Math.toIntExact;

public class StanzaTagger extends StanzaBase{
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
		mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext,posMappingLocation, variant, language);

		try {
			interpreter.exec("nlps = {}");
			interpreter.exec("nlps['de'] = stanza.Pipeline(**{'processors': 'tokenize,pos,lemma,mwt,depparse','lang': 'de','mwt_batch_size':1})");
			interpreter.exec("nlps['en'] = stanza.Pipeline(**{'processors': 'tokenize,pos,lemma,depparse','lang': 'en'})");


		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		final CAS cas = aJCas.getCas();
		mappingProvider.configure(cas);
		try {
			String lang = aJCas.getDocumentLanguage();
			String text = aJCas.getDocumentText();
			if(text.trim().length() == 0)
				return;

			interpreter.set("lang", lang);
			interpreter.set("text", text);
			interpreter.exec("nlp = nlps[lang] if lang in nlps else nlps['en']");
			interpreter.exec("doc = nlp(text)");
			interpreter.exec("dic = doc.to_dict()");

			//			interpreter.exec("token_list = [{"+
			//				"'upos': token.get('xpos'),"+
			//				"'lemma': token.get('lemma'),"+
			//				"'id': token.get('id'),"+
			//				"'deprel' : token.get('deprel'),"+
			//				"'begin': token.get('misc').replace('start_char=','').split('|')[0],"+
			//				"'end': token.get('misc').replace('end_char=','').split('|')[1],"+
			//				"'head': token.get('head'),"+
			//				"}"+
			//				"for sentence in dic for token in sentence]");

			interpreter.exec(
					"token_list = []\n"+ 
							"for sentence in dic:\n"+
							"    cleanedSentece = []\n"+
							"    mwt = None\n"+
							"    lemma = ''\n"+
							"    for token in sentence:\n"+
							"        if(type(token['id']) is list or type(token['id']) is tuple):\n"+
							"            mwt = token\n"+
							"        else:\n"+
							"            if(mwt != None and token['id'] in mwt['id']):\n"+
							"                if(len(lemma) > 0):\n"+
							"                    lemma+= ' '\n"+
							"                lemma += token['lemma']\n"+
							"                if(token['id'] == max(mwt['id'])):\n"+
							"                    token['misc'] = mwt['misc']\n"+
							"                    token['lemma'] = lemma\n"+
							"                    token['begin'] = token.get('misc').replace('start_char=','').split('|')[0]\n"+
							"                    token['end'] = token.get('misc').replace('end_char=','').split('|')[1]\n"+
							"                    cleanedSentece.append(token)\n"+
							"                    mwt = None\n"+
							"                    lemma = ''\n"+
							"            else:\n"+
							"                token['begin'] = token.get('misc').replace('start_char=','').split('|')[0]\n"+
							"                token['end'] = token.get('misc').replace('end_char=','').split('|')[1]\n"+
							"                cleanedSentece.append(token)\n"+
							"    token_list.append(cleanedSentece)");

			ArrayList<ArrayList<HashMap<String, Object>>> sentenceList = (ArrayList<ArrayList<HashMap<String, Object>>>) interpreter.getValue("token_list");

			for (ArrayList<HashMap<String, Object>> tokenList : sentenceList) {
				Sentence sentence = new Sentence(aJCas, Integer.parseInt(tokenList.get(0).get("begin").toString()), Integer.parseInt(tokenList.get(tokenList.size()-1).get("end").toString()));
				sentence.addToIndexes();

				Map<Integer, Map<Integer, Token>> tokensMap = new HashMap<>();
				Map<Integer, Token> tokensIdMap = new HashMap<>();
				for (HashMap<String, Object> token : tokenList) {

					int begin = Integer.valueOf((String)token.get("begin"));
					int end = Integer.valueOf((String)token.get("end"));

					Token casToken = new Token(aJCas, begin, end);
					casToken.addToIndexes();
					tokensIdMap.put(Integer.parseInt(token.get("id").toString()), casToken);

					if (!tokensMap.containsKey(begin)) {
						tokensMap.put(begin, new HashMap<>());
					}
					if (!tokensMap.get(begin).containsKey(end)) {
						tokensMap.get(begin).put(end, casToken);
					}
				}

				// then the rest
				tokenList.forEach(token -> {
					int begin = Integer.valueOf((String)token.get("begin"));
					int end = Integer.valueOf((String)token.get("end"));

					// get token
					Token casToken = tokensMap.get(begin).get(end);

					// POS
					String tagStr = token.get("upos").toString();
					Type posTag = mappingProvider.getTagType(tagStr.intern());
					POS posAnno = (POS) cas.createAnnotation(posTag, begin, end);
					posAnno.setPosValue(tagStr);
					casToken.setPos(posAnno);
					posAnno.addToIndexes();

					// Lemma
					Lemma casLemma = new Lemma(aJCas, begin, end);
					casLemma.setValue((String)token.get("lemma"));
					casToken.setLemma(casLemma);
					casLemma.addToIndexes();

					// Dependency
					int headID = toIntExact((Long)token.get("head"));

					int beginHead = casToken.getBegin();
					int endHead = casToken.getEnd();

					if(headID > 0)
					{
						while(!tokensIdMap.containsKey(headID))
							headID++;
						beginHead = tokensIdMap.get(headID).getBegin();
						endHead = tokensIdMap.get(headID).getEnd();
					}
					Token governor = tokensMap.get(beginHead).get(endHead);
					Dependency depAnno;
					String depStr = token.get("deprel").toString().toUpperCase();
					if (depStr.equals("ROOT")) {
						depAnno = new ROOT(aJCas, begin, end);
						depAnno.setDependencyType("--");
					} else {
						depAnno = new Dependency(aJCas, begin, end);
						depAnno.setDependencyType(depStr);
					}
					depAnno.setDependent(casToken);
					depAnno.setGovernor(governor);
					depAnno.setFlavor(DependencyFlavor.BASIC);
					depAnno.addToIndexes();
				});
			}
		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}