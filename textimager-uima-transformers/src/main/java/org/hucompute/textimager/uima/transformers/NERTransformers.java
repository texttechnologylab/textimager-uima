package org.hucompute.textimager.uima.transformers;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.MappingProvider;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import jep.JepException;

public class NERTransformers extends BaseTransformers {
	
	
	/**
	 * Overwrite CAS Language?
	 */
	public static final String PARAM_LANGUAGE = "language";
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	/**
	 * Overwrite POS mapping location?
	 */
	public static final String PARAM_NAMED_ENTITY_MAPPING_LOCATION = "nerMappingLocation";
	@ConfigurationParameter(name = PARAM_NAMED_ENTITY_MAPPING_LOCATION, mandatory = false)
	protected String nerMappingLocation;

	/**
	 * Overwrite model variant?
	 */
	public static final String PARAM_VARIANT = "variant";
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	private MappingProvider mappingProvider;
	
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		
		
		super.initialize(aContext);
		mappingProvider = new MappingProvider();
		mappingProvider.setDefaultVariantsLocation("org/hucompute/textimager/uima/transformers/ner-default-variants.map");
		mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/org/hucompute/textimager/uima/transformers/ner-${language}-${variant}.map");
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, NamedEntity.class.getName());
		mappingProvider.setOverride(MappingProvider.LOCATION, nerMappingLocation);
		mappingProvider.setOverride(MappingProvider.LANGUAGE, language);
		mappingProvider.setOverride(MappingProvider.VARIANT, variant);
	}
	

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		//		super.process(aJCas);
		mappingProvider.configure(aJCas.getCas());
		HashMap<String, Object>  json = buildJSON(aJCas);
		ArrayList<ArrayList<Long>> tokens;
		try {
			interp.set("lang", aJCas.getDocumentLanguage());
			interp.set("words",json.get("words"));
			interp.set("spaces",json.get("spaces"));
			interp.set("text",aJCas.getDocumentText());
			
			if(aJCas.getDocumentLanguage().equals("de")) {
				interp.exec("nlp = pipeline('ner',model='fhswf/bert_de_ner')");
				interp.exec("tokenizer = AutoTokenizer.from_pretrained(\"fhswf/bert_de_ner\", use_fast=True)");
			}
			else {
				interp.exec("nlp = pipeline('ner')");
				interp.exec("tokenizer = AutoTokenizer.from_pretrained(\"dbmdz/bert-large-cased-finetuned-conll03-english\", use_fast=True)");
			}
			
			
			interp.exec("ents = nlp(text)");
			interp.exec("tokens = tokenizer(text, return_offsets_mapping = True).get('offset_mapping')");
			tokens = (ArrayList<ArrayList<Long>>) interp.getValue("tokens");			
			ArrayList<HashMap<String, Object>> poss = (ArrayList<HashMap<String, Object>>) interp.getValue("ents");
			poss.forEach(p -> {
				
				int index = ((Long)p.get("index")).intValue();
				ArrayList<Long> token = new ArrayList<Long>(tokens.get(index));
				int begin = token.get(0).intValue() ;
				int end = token.get(1).intValue();
				String labelStr = p.get("entity").toString();
				Type neTag = mappingProvider.getTagType(labelStr);
				NamedEntity neAnno = (NamedEntity) aJCas.getCas().createAnnotation(neTag, begin, end);
				//NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
				neAnno.setValue(labelStr);
				neAnno.addToIndexes();
				
				
			});
					
			

		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}