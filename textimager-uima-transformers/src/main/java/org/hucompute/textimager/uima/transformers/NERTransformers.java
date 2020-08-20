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

	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

	}
	

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		//		super.process(aJCas);
		HashMap<String, Object>  json = buildJSON(aJCas);
		ArrayList<ArrayList<Integer>> tokens;
		try {
			interp.set("lang", aJCas.getDocumentLanguage());
			interp.set("words",json.get("words"));
			interp.set("spaces",json.get("spaces"));
			interp.set("text",aJCas.getDocumentText());

			interp.exec("nlp = pipeline('ner')");
			interp.exec("ents = nlp(text)");
			
			interp.exec("tokeni = AutoTokenizer.from_pretrained(\"dbmdz/bert-large-cased-finetuned-conll03-english\", use_fast=True)");
			interp.exec("tokens = tokenizer(text, return_offsets_mappint = True).get('offset_mapping')");
			tokens = (ArrayList<ArrayList<Integer>>) interp.get("tokens");
			ArrayList<HashMap<String, Object>> poss = (ArrayList<HashMap<String, Object>>) interp.getValue("ents");
			poss.forEach(p -> {
				
				int index =((Long)p.get("index")).intValue();
				int begin = tokens.get(index).get(0) ;
				int end = tokens.get(index).get(1);
				String labelStr = p.get("entity").toString();
				NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
				neAnno.setValue(labelStr);
				neAnno.addToIndexes();
			});

		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}