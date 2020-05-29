package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.PublicInter;
import org.hucompute.textimager.uima.spacy.SpaCyTagger;
import org.hucompute.textimager.uima.spacy.SpaCyTokenizer;
import org.hucompute.textimager.uima.util.XmlFormatter;

import jep.JepException;


public class SimpleTestSpacy {

	public static void main(String[] args) throws UIMAException, JepException {

		
		JCas cas = JCasFactory.createText("Der Gefangene erhängte sich in seiner Zelle. Die Dorfbewohner erhängten den Viehdieb an einem Baum.","de");
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(SpaCyTokenizer.class,SpaCyTokenizer.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
		builder.add(createEngineDescription(SpaCyTagger.class,SpaCyTagger.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
		
		//aufsetzen des Interpreters
		PublicInter.setUpInter("C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38");
		
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
	}

}



