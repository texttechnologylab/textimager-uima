package org.hucompute.textimager.uima.tool.test;

import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.spacy.SpaCyTagger;
import org.hucompute.textimager.uima.spacy.SpaCyTokenizer;
import org.hucompute.textimager.uima.util.XmlFormatter;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;

public class SimpleTest {

	public static void main(String[] args) throws UIMAException {

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(SpaCyTokenizer.class,SpaCyTokenizer.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy"));
//		builder.add(createEngineDescription(SpaCyTagger.class,SpaCyTagger.PARAM_PYTHON_HOME,"/home/ahemati/miniconda3/envs/spacy"));
		
		JCas inputCas = JCasFactory.createText("Das ist ein Test.", "de");
		SimplePipeline.runPipeline(inputCas, builder.createAggregateDescription());
		
		System.out.println(XmlFormatter.getPrettyString(inputCas.getCas()));
	}

}
