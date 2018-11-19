package org.hucompute.textimager.uima.NeuralnetworkNER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.io.IobDecoder;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

/**
 * Neuralnetwork NER implementation class.
 *
 * @author Manuel Stoeckel
 */
public class NeuralNER extends NeuralNERBase {


	public static final String PARAM_NAMED_ENTITY_MAPPING_LOCATION = 
			ComponentParameters.PARAM_NAMED_ENTITY_MAPPING_LOCATION;
	@ConfigurationParameter(name = PARAM_NAMED_ENTITY_MAPPING_LOCATION, mandatory = false)
	private String namedEntityMappingLocation;

	private MappingProvider namedEntityMappingProvider;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		namedEntityMappingProvider = new MappingProvider();
		namedEntityMappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/org/hucompute/textimager/uima/NeuralnetworkNER/lib/ner-default.map");
		namedEntityMappingProvider.setDefault(MappingProvider.BASE_TYPE, NamedEntity.class.getName());
		//        namedEntityMappingProvider.setOverride(MappingProvider.LOCATION, namedEntityMappingLocation);
		namedEntityMappingProvider.setOverride(MappingProvider.LANGUAGE, "de");
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		namedEntityMappingProvider.configure(aJCas.getCas());
		super.process(aJCas);

	}

	@Override
	protected JSONObject buildJSON(JCas aJCas) {
		JSONObject json = new JSONObject();
		jsonAddWordsAndCharIDs(aJCas, json);
		return json;
	}

	@Override
	protected void updateCAS(JCas aJCas, JSONObject jsonResult) {
		Type namedEntityType = JCasUtil.getType(aJCas, NamedEntity.class);
		Feature namedEntityValue = namedEntityType.getFeatureByBaseName("value");
		IobDecoder decoder = new IobDecoder(aJCas.getCas(), namedEntityValue, namedEntityMappingProvider);

		ArrayList<Token>words = new ArrayList<Token>(JCasUtil.select(aJCas,Token.class));
		JSONArray ents = jsonResult.getJSONArray("ents");
		String[] namedEntityTags = new String[words.size()];
		for (int i = 0; i < namedEntityTags.length; i++) {
			namedEntityTags[i] = "O";
		}
		ents.forEach(e -> {
			JSONObject ent = (JSONObject) e;
			int begin = ent.getInt("start_char");
			int end = ent.getInt("end_char");
			String labelStr = ent.getString("label");
			namedEntityTags[words.indexOf(JCasUtil.selectAt(aJCas, Token.class, begin, end).get(0))] = labelStr;
		});
		decoder.decode(words, namedEntityTags);
		for (NamedEntity ne : JCasUtil.select(aJCas, NamedEntity.class)) {
			ne.setValue("I-" + ne.getValue());
		}
	}

	@Override
	protected String getRestRoute() {
		return "/ner";
	}
}