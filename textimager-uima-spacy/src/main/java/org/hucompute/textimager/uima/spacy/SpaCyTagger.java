package org.hucompute.textimager.uima.spacy;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.pos.POSUtils;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;

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
		mappingProvider = MappingProviderFactory.createPosMappingProvider(posMappingLocation, variant, language);
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		super.process(aJCas);
	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		JSONObject json = new JSONObject();
		json.put("lang", aJCas.getDocumentLanguage());
		jsonAddWordsAndSpaces(aJCas, json);
		return json;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		mappingProvider.configure(aJCas.getCas());
				
		JSONArray poss = jsonResult.getJSONArray("pos");
		poss.forEach(p -> {
				JSONObject pos = (JSONObject)p;
				if (!pos.getBoolean("is_space")) {
					int begin = pos.getInt("idx");
					int end = begin + pos.getInt("length");
					String tagStr = pos.getString("tag");
	
					Type posTag = mappingProvider.getTagType(tagStr);
					POS posAnno = (POS) aJCas.getCas().createAnnotation(posTag, begin, end);
					posAnno.setPosValue(tagStr);
					POSUtils.assignCoarseValue(posAnno);
					posAnno.addToIndexes();
				}
			});
	}

	@Override
	protected String getRestRoute() {
		return "/tagger";
	}
}