package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger;
import org.hucompute.textimager.uima.spacy.SpaCyTagger;
import org.hucompute.textimager.uima.spacy.SpaCyTokenizer;
import org.hucompute.textimager.uima.stanza.StanzaTagger;
import org.hucompute.textimager.uima.util.XmlFormatter;


import jep.JepException;


public class SimpleTestSpacy {

	public static void main(String[] args) throws UIMAException, JepException {

		
		JCas cas = JCasFactory.createText("Das ist ein iPhone von   Apple.","de");
		
		AggregateBuilder builder = new AggregateBuilder();
		
//		builder.add(createEngineDescription(SpaCyTokenizer.class,SpaCyTokenizer.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy"));
//		builder.add(createEngineDescription(StanzaTagger.class,StanzaTagger.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/stanza"));
//		builder.add(createEngineDescription(SpaCyMultiTagger.class,SpaCyMultiTagger.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
		
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
	
		
	
	}

}



