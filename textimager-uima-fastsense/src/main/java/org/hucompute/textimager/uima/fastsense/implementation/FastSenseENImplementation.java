package org.hucompute.textimager.uima.fastsense.implementation;

import java.util.List;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Paragraph;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class FastSenseENImplementation {
	public JSONObject buildJSON(JCas aJCas) {
		JSONObject json = new JSONObject();
		
		for (Paragraph paragraph : JCasUtil.select(aJCas, Paragraph.class)) {
			JSONArray jsonP = new JSONArray();
			
			List<Token> tokens = JCasUtil.selectCovered(Token.class, paragraph);
			for (int ind = 0; ind < tokens.size(); ++ind) {
				Token token = tokens.get(ind);				
				JSONArray jsonT = new JSONArray();
				
				System.out.println(token.getPosValue());
				
				jsonT.put(token.getBegin());					// Start Index
				jsonT.put(token.getEnd());						// End Index
				jsonT.put(token.getCoveredText());				// Token
				jsonT.put(token.getPosValue());					// POS
				jsonT.put(whitespaceBeforeToken(tokens, ind));	// Whitespace before
				jsonT.put(whitespaceAfterToken(tokens, ind));	// Whitespace after
				jsonP.put(jsonT);
			}
			
			json.append("paragraphs", jsonP);
		}
	
		return json;
	}
	
	private String whitespaceBeforeToken(List<Token> tokens, int ind) {
		if (ind <= 0) {
			return "";
		}
		
		Token tokenPrev = tokens.get(ind-1);
		Token token = tokens.get(ind);
		
		if (tokenPrev.getEnd() == token.getBegin()) {
			return "";
		}
		
		return " ";
	}
	
	private String whitespaceAfterToken(List<Token> tokens, int ind) {
		if (ind >= tokens.size()-1) {
			return "";
		}
		
		Token token = tokens.get(ind);
		Token tokenNext = tokens.get(ind+1);
		
		if (token.getEnd() == tokenNext.getBegin()) {
			return "";
		}
		
		return " ";
	}

	public void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
		JSONArray results = jsonResult.getJSONArray("results");
		results.forEach(r -> {
				JSONObject result = (JSONObject)r;
				int begin = result.getInt("start");
				int end = result.getInt("end");
				CategoryCoveredTagged cat = new CategoryCoveredTagged(aJCas, begin, end);
				cat.setValue(result.getString("url"));
				cat.addToIndexes();
			});
	}
}
