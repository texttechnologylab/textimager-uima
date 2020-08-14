package org.hucompute.textimager.uima.stanza;

import static java.lang.Math.toIntExact;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;
import org.dkpro.core.api.resources.ResourceUtils;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import org.dkpro.core.api.parameter.ComponentParameters;
import jep.JepException;

public class StanzaTagger extends StanzaBase {
	/**
	 * Overwrite CAS Language?
	 */
	public static final String PARAM_LANGUAGE = "language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;


	/**
     * Load the part-of-speech tag to UIMA type mapping from this location instead of locating the
     * mapping automatically.
     */
    public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
    @ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
    protected String posMappingLocation;

	/**
	 * Overwrite ner mapping location?
	 */
	public static final String PARAM_NER_MAPPING_LOCATION = "nerMappingLocation";
	@ConfigurationParameter(name = PARAM_NER_MAPPING_LOCATION, mandatory = false)
	protected String nerMappingLocation;

	/**
	 * Overwrite model variant?
	 */
	public static final String PARAM_VARIANT = "variant";
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	private MappingProvider posMappingProvider;
	private MappingProvider nerMappingProvider;

	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		posMappingProvider = MappingProviderFactory.createPosMappingProvider(aContext,posMappingLocation, variant, language);
		nerMappingProvider = MappingProviderFactory.createNerMappingProvider(aContext,nerMappingLocation, language, variant);
        };

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		final CAS cas = aJCas.getCas();
		//modelProvider.configure(cas);
		posMappingProvider.configure(cas);
		nerMappingProvider.configure(cas);
		try {
			final Object lang = aJCas.getDocumentLanguage();
			final Object text = aJCas.getDocumentText();
			interp.set("lang", lang);
			interp.set("text", text);
			interp.exec("nlp = stanza.Pipeline(**{'processors': 'tokenize,pos,lemma,mwt,depparse,ner','lang': lang,})");
			interp.exec("doc = nlp(text)");
            interp.exec("dic = doc.to_dict()");
			interp.exec("token_list = [{"+
				"'upos': token.get('upos'),"+
				"'feats': token.get('feats'),"+
				"'lemma': token.get('lemma'),"+
				"'id': token.get('id'),"+
				"'deprel' : token.get('deprel'),"+
				"'begin': token.get('misc').replace('start_char=','').split('|')[0],"+
				"'end': token.get('misc').replace('end_char=','').split('|')[1],"+
				"'head': token.get('head'),"+
				"'type': token.get('type'),"+
				"}"+
				"for sentence in dic for token in sentence]");
			ArrayList<HashMap<String, Object>> tokenList = (ArrayList<HashMap<String, Object>>) interp.getValue("token_list");
			tokenList.forEach(token -> {
				int begin = Integer.valueOf((String)token.get("begin"));
				int end = Integer.valueOf((String)token.get("end"));

				String tagStr = token.get("upos").toString();
				String feats = (String)token.get("feats");
				Type posTag = posMappingProvider.getTagType(tagStr.intern());
				POS posAnno = (POS) cas.createAnnotation(posTag, begin, end);
				posAnno.setPosValue(tagStr);
				posAnno.setCoarseValue(feats);
				posAnno.addToIndexes();

				Token casToken = new Token(aJCas, begin, end);
				casToken.addToIndexes();

				Lemma casLemma = new Lemma(aJCas, begin, end);
				casLemma.setValue((String)token.get("lemma"));
				casLemma.addToIndexes();

				int headID = toIntExact((Long)token.get("head"));
				int beginHead = Integer.valueOf((String)(tokenList.get(headID).get("begin")));
				int endHead = Integer.valueOf((String)(tokenList.get(headID).get("end")));
				Token dependent = casToken;
				Token governor = new Token(aJCas, beginHead, endHead);
				Dependency depAnno;
				String depStr = token.get("deprel").toString().toUpperCase();
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
				
				String type = (String)token.get("type");
				Type neTag = nerMappingProvider.getTagType(type);
				System.out.println(neTag);
				NamedEntity neAnno = (NamedEntity) aJCas.getCas().createAnnotation(neTag, begin, end);
//				NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
				neAnno.setValue(type);
				neAnno.addToIndexes();
			});
		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}
