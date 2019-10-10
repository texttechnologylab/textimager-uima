package org.hucompute.textimager.uima.stanfordnlp;

import java.util.ArrayList;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import jep.JepException;

/***
 * Prepare Conda env for StanfordNLP Annotator:
 * See Test for examples on the paths
 * 
 * conda create --name jeptest
 * conda install pip
 * pip install stanfordnlp
 * JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64 pip install jep
 */

@TypeCapability(
		outputs = {
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token",
				"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence",
		})
public class StanfordNLPTokenizer extends JepAnnotator {
	/**
     * Write Sentences
     */
    public static final String PARAM_WRITE_SENTENCES = "writeSentences";
    @ConfigurationParameter(name = PARAM_WRITE_SENTENCES, mandatory = false, defaultValue = "false")
    protected boolean writeSentences;
    
	/**
     * StanfordNLP model directory
     */
    public static final String PARAM_MODEL_LOCATION = "modelLocation";
    @ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = true, defaultValue = "/resources/nlp/models/stanfordnlp")
    protected String modelLocation;
    
	/**
     * StanfordNLP use GPU?
     */
    public static final String PARAM_USE_GPU = "useGPU";
    @ConfigurationParameter(name = PARAM_USE_GPU, mandatory = false, defaultValue = "true")
    protected boolean useGPU;
    
    @Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		try {
			interp.exec("import os");
			interp.exec("import sys");
			// TODO test in jar, export to tmp automatically?
			interp.exec("sys.path.append('src/main/python/')"); // FIXME: fix this relative path
			interp.exec("from textimager_stanfordnlp import TextImagerStanfordNLP");
			interp.exec("ti_nlp = TextImagerStanfordNLP('" + modelLocation + "', '" + (useGPU ? "True" : "False") + "')");
		} catch (JepException ex) {
			throw new ResourceInitializationException(ex);
		}
    }
    
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		String documentText = aJCas.getDocumentText();
		String lang = aJCas.getDocumentLanguage();
		
		int end = 0;
		int sentenceBegin = 0;
		boolean sentenceBeginUpdate = false;

		try {
			ArrayList<ArrayList<String>> sentences = (ArrayList<ArrayList<String>>) interp.invoke("ti_nlp.tokenize", lang, documentText);
		
			for (ArrayList<String> tokens : sentences) {
				for (String token : tokens) {
					// Try to match the StanfordNLP token
					int pos = documentText.indexOf(token, end);
					if (pos != -1) {
						int begin = pos;
						end = begin + token.length();
						
						if (sentenceBeginUpdate) {
							sentenceBeginUpdate = false;
							sentenceBegin = begin;
						}
						
						Token casToken = new Token(aJCas, begin, end);
						casToken.addToIndexes();
					}
				}
	
				if (writeSentences) {
					int sentenceEnd = end;
					sentenceBeginUpdate = true;
					
					Sentence casSentence = new Sentence(aJCas, sentenceBegin, sentenceEnd);
					casSentence.addToIndexes();
				}
			}
		} catch (JepException e) {
			throw new AnalysisEngineProcessException(e);
		}
	}
}
