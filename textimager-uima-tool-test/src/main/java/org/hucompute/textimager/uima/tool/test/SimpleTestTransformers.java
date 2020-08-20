package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.transformers.BaseTransformers;
import org.hucompute.textimager.uima.transformers.NERTransformers;
import org.hucompute.textimager.uima.spacy.SpaCyNER;
import org.hucompute.textimager.uima.spacy.SpaCyTagger;
import org.hucompute.textimager.uima.spacy.SpaCyTokenizer;
import org.hucompute.textimager.uima.stanza.StanzaTagger;
import org.hucompute.textimager.uima.util.XmlFormatter;


import jep.JepException;

public class SimpleTestTransformers {
	
	public static void main(String[] args) throws UIMAException, JepException {
	
			
			JCas cas = JCasFactory.createText("Das iPhone ist von Apple. Bill Gates ist der Boss von Apple. Er hat 50% von 100� am 20.02.2222. Apple ist eine Firma.","de");
			
			AggregateBuilder builder = new AggregateBuilder();
			
			builder.add(createEngineDescription(NERTransformers.class,NERTransformers.PARAM_PYTHON_HOME,"C:\\Users\\PC\\AppData\\Local\\Programs\\Python\\Python38"));
			
		
			SimplePipeline.runPipeline(cas,builder.createAggregate());
		
			System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
		
			
		
		}
}
