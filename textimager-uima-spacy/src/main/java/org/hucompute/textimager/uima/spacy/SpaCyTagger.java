package org.hucompute.textimager.uima.spacy;

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

@TypeCapability(
		inputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token"
		},
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"
		})
public class SpaCyTagger extends SpaCyBase {
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

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		// TODO defaults for de (stts) and en (ptb) are ok, add own language mapping later
		mappingProvider = MappingProviderFactory.createPosMappingProvider(aContext,posMappingLocation, variant, language);
		try {
			interp.exec("from spacy.tokens import Doc");
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		//		super.process(aJCas);
		mappingProvider.configure(aJCas.getCas());
		HashMap<String, Object>  json = buildJSON(aJCas);
		try {
			interp.set("lang", aJCas.getDocumentLanguage());
			interp.set("words",json.get("words"));
			interp.set("spaces",json.get("spaces"));
			if(aJCas.getDocumentLanguage().equals("de"))
				interp.exec("nlp = spacy.load('de_core_news_sm')");
			else
				interp.exec("nlp = spacy.load('en_core_web_sm')");
			interp.exec("doc = Doc(nlp.vocab, words=words, spaces=spaces)");
			interp.exec("nlp.tagger(doc)");
			interp.exec("pos = [{'tag': token.tag_,'idx': token.idx,'length': len(token),'is_space': token.is_space}for token in doc]");
			ArrayList<HashMap<String, Object>> poss = (ArrayList<HashMap<String, Object>>) interp.getValue("pos");
			poss.forEach(p -> {
				if (!(Boolean)p.get("is_space")) {
					int begin = ((Long)p.get("idx")).intValue();
					int end = begin + ((Long)p.get("length")).intValue();
					String tagStr = p.get("tag").toString();

					Type posTag = mappingProvider.getTagType(tagStr);
					POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
					posAnno.setPosValue(tagStr);
					POSUtils.assignCoarseValue(posAnno);
					posAnno.addToIndexes();
				}
			});
		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}