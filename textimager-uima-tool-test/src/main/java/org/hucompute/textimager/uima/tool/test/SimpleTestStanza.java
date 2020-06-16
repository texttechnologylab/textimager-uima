package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import java.nio.file.Paths;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.base.PublicInter;
import org.hucompute.textimager.uima.spacy.SpaCyTokenizer;
import org.hucompute.textimager.uima.stanza.StanzaTagger;
import org.hucompute.textimager.uima.util.XmlFormatter;

import jep.JepException;


public class SimpleTestStanza {

	public static void main(String[] args) throws UIMAException, JepException {
		System.out.println(Paths.get(System.getProperty("user.home") ,"miniconda3/envs/stanza/bin/python").toAbsolutePath().toString());
		JCas cas = JCasFactory.createText("Die Gefangenen erhängten sich in ihrer Zelle. Die Dorfbewohner erhängten den Viehdieb an einem Baum.","de");
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(SpaCyTokenizer.class,SpaCyTokenizer.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy"));
		builder.add(createEngineDescription(StanzaTagger.class,StanzaTagger.PARAM_PYTHON_HOME,Paths.get(System.getProperty("user.home") ,"miniconda3/envs/stanza").toAbsolutePath().toString()));
		
		//aufsetzen des Interpreters
		PublicInter.setUpInter(Paths.get(System.getProperty("user.home") ,"miniconda3/envs/stanza").toAbsolutePath().toString());
		
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
	}

}
