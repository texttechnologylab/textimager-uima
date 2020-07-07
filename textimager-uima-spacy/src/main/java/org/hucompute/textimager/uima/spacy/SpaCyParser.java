package org.hucompute.textimager.uima.spacy;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import jep.JepException;

public class SpaCyParser extends SpaCyBase {
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		/*try {
			interp.exec("from spacy.tokens import Doc");
		} catch (JepException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		/*HashMap<String, Object>  json = buildJSON(aJCas);
		try {
			interp.set("lang", aJCas.getDocumentLanguage());
			interp.set("words",json.get("words"));
			interp.set("spaces",json.get("spaces"));
			if(aJCas.getDocumentLanguage().equals("de"))
				interp.exec("nlp = spacy.load('de_core_news_sm')");
			else
				interp.exec("nlp = spacy.load('en_core_web_sm')");
			interp.exec("doc = Doc(nlp.vocab, words=words, spaces=spaces)");
			interp.exec("nlp.parser(doc)");
			interp.exec("deps = ["+
					"{"+
					"'dep': token.dep_,"+
					"'idx': token.idx,"+
					"'length': len(token),"+
					"'is_space': token.is_space,"+
					"'head': {"+
					"'idx': token.head.idx,"+
					"'length': len(token.head),"+
					"'is_space': token.head.is_space"+
					"}"+
					"}"+
					"for token in doc]");
			interp.exec("System.out.println(deps)");

			ArrayList<HashMap<String, Object>> deps = (ArrayList<HashMap<String, Object>>) interp.getValue("deps");
			deps.forEach(dep -> {
				if (!(Boolean)dep.get("is_space")) {
					String depStr = dep.get("dep").toString().toUpperCase();

					int begin = ((Long)dep.get("idx")).intValue();
					int end = begin + ((Long)dep.get("length")).intValue();

					HashMap<String, Object>headToken = (HashMap<String, Object>) dep.get("head");
					int beginHead =((Long)headToken.get("idx")).intValue();
					int endHead = beginHead + ((Long)headToken.get("length")).intValue();

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
		} catch (JepException e) {
			e.printStackTrace();
		}*/
	}
}