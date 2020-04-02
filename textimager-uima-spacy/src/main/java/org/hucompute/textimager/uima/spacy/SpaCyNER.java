package org.hucompute.textimager.uima.spacy;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import jep.JepException;

public class SpaCyNER extends SpaCyBase {
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
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
			interp.exec("for name, proc in nlp.pipeline:\n"
					+ "    print(name,proc)\n"
					+ "    if name == 'ner':\n"
					+ "        doc = proc(doc)");
			interp.exec("ents = [{'start_char': ent.start_char,'end_char': ent.end_char,'label': ent.label_}for ent in doc.ents]");

			ArrayList<HashMap<String, Object>> poss = (ArrayList<HashMap<String, Object>>) interp.getValue("ents");
			poss.forEach(p -> {
				int begin = ((Long)p.get("start_char")).intValue();
				int end = ((Long)p.get("end_char")).intValue();
				String labelStr = p.get("label").toString();
				NamedEntity neAnno = new NamedEntity(aJCas, begin, end);
				neAnno.setValue(labelStr);
				neAnno.addToIndexes();
			});
		} catch (JepException e) {
			e.printStackTrace();
		}
	}
}