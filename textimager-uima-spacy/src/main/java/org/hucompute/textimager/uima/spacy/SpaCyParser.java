package org.hucompute.textimager.uima.spacy;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;

public class SpaCyParser extends SpaCyBase {
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
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
		JSONArray deps = jsonResult.getJSONArray("deps");
		deps.forEach(d -> {
				JSONObject dep = (JSONObject)d;				
				if (!dep.getBoolean("is_space")) {
					String depStr = dep.getString("dep").toUpperCase();
					
					int begin = dep.getInt("idx");
					int end = begin + dep.getInt("length");
					
					JSONObject headToken = dep.getJSONObject("head");
					int beginHead = headToken.getInt("idx");
					int endHead = beginHead + headToken.getInt("length");
					
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
	}

	@Override
	protected String getRestRoute() {
		return "/parser";
	}
}