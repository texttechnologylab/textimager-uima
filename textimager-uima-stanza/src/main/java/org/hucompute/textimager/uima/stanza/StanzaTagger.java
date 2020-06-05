package org.hucompute.textimager.uima.stanza;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.lexmorph.pos.POSUtils;
import org.dkpro.core.api.resources.MappingProvider;
import org.dkpro.core.api.resources.MappingProviderFactory;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import jep.JepException;

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
	}
	
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		mappingProvider.configure(aJCas.getCas());
		try {
			interp.set("lang", aJCas.getDocumentLanguage());
			interp.set("text",aJCas.getDocumentText());
			interp.exec("nlp = stanza.Pipeline(**{'processors': 'tokenize,pos,mwt','lang': lang,})");
			interp.exec("doc = nlp(text)");
            interp.exec("dic = doc.to_dict()");
			interp.exec("pos = [{'upos': token.get('upos'), 'xpos': token.get('xpos'), 'misc': token.get('misc').replace('start_char=','').replace('end_char=','').split('|')}for sentence in dic for token in sentence]");
			ArrayList<HashMap<String, Object>> poss = (ArrayList<HashMap<String, Object>>) interp.getValue("pos");
			poss.forEach(p -> {
				int begin = Integer.valueOf((String)(((ArrayList)p.get("misc")).get(0)));
				int end = Integer.valueOf((String)(((ArrayList)p.get("misc")).get(1)));
				String tagStr = p.get("upos").toString();

				Type posTag = mappingProvider.getTagType(tagStr);
				POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
				posAnno.setPosValue(tagStr);
				POSUtils.assignCoarseValue(posAnno);
				posAnno.addToIndexes();
			});
			
			
			
		} catch (JepException e) {
			e.printStackTrace();
		}
	}

}
