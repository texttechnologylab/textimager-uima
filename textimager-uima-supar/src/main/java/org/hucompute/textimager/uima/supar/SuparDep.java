package org.hucompute.textimager.uima.supar;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.DockerRestAnnotator;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;

public class SuparDep extends DockerRestAnnotator {
    /**
     * Model name
     */
    public static final String PARAM_MODEL_NAME = "modelName";
    @ConfigurationParameter(name = PARAM_MODEL_NAME)
    protected String modelName;

    @Override
    protected String getDefaultDockerImage() {
        return "textimager-uima-service-supar-dep";
    }

    @Override
    protected String getDefaultDockerImageTag() {
        return "0.1";
    }

    @Override
    protected int getDefaultDockerPort() {
        return 8000;
    }

    @Override
    protected String getRestRoute() {
        return "/dep";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
    
    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
    	super.initialize(aContext);
    	
    	// TODO integrate docker mount functionality to allow container access to shared space
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) throws AnalysisEngineProcessException {
        JSONObject payload = new JSONObject();
        payload.put("lang", aJCas.getDocumentLanguage());
        payload.put("model", modelName);

        JSONArray sentences = new JSONArray();
        for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
            JSONObject sentenceObj = new JSONObject();
            sentenceObj.put("begin", sentence.getBegin());
            sentenceObj.put("end", sentence.getEnd());

            JSONArray tokens = new JSONArray();
            for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
                JSONObject tokenObj = new JSONObject();
                tokenObj.put("begin", token.getBegin());
                tokenObj.put("end", token.getEnd());
                tokenObj.put("text", token.getCoveredText());
                tokens.put(tokenObj);
            }
            sentenceObj.put("tokens", tokens);

            sentences.put(sentenceObj);
        }
        payload.put("sentences", sentences);

        return payload;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
        System.out.println("test");
        
        if (jsonResult.has("sentences")) {
        	JSONArray sentences = jsonResult.getJSONArray("sentences");
        	for (Object s : sentences) {
        		JSONObject sentence = (JSONObject) s;
        		JSONArray arcs = sentence.getJSONArray("arcs");
        		JSONArray rels = sentence.getJSONArray("rels");

        		JSONObject sentenceDetails = sentence.getJSONObject("sentence");
        		JSONArray tokens = sentenceDetails.getJSONArray("tokens");
        		for (int ind = 0; ind < tokens.length(); ind++) {
        			JSONObject token = tokens.getJSONObject(ind);
        			int begin = token.getInt("begin");
        			int end = token.getInt("end");
        			String rel = rels.getString(ind);
        			
        			// get dependent
    				Token dependent = JCasUtil.selectSingleAt(aJCas, Token.class, begin, end);

    				// set depending on dep
    				Token governor = null;

    				// add to cas
    				Dependency depAnno;
    				if (rel.equalsIgnoreCase("root")) {
    					depAnno = new ROOT(aJCas, begin, end);
    					depAnno.setDependencyType("--");

    					// governor = dependent
    					governor = dependent;
    				} else {
    					depAnno = new Dependency(aJCas, begin, end);
    					depAnno.setDependencyType(rel);

    					// get governor from specified line
    					// count starts at 1
            			int arc = arcs.getInt(ind) - 1;

    					JSONObject governorToken = tokens.getJSONObject(arc);
            			int governorBegin = governorToken.getInt("begin");
            			int governorEnd = governorToken.getInt("end");
    					governor = JCasUtil.selectSingleAt(aJCas, Token.class, governorBegin, governorEnd);
    				}
    				depAnno.setDependent(dependent);
    				depAnno.setGovernor(governor);
    				depAnno.setFlavor(DependencyFlavor.BASIC);
    				depAnno.addToIndexes();
        		}
        	}
        }
    }
}
