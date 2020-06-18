package org.hucompute.textimager.uima.spacy;

import org.apache.uima.UIMAException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;



public class SpaCyMultiTagger extends SpaCyBase{
	
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}
	
	
	
	public void process(JCas aJCas) throws AnalysisEngineProcessException{
		
		
		try {
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(createEngineDescription(SpaCyTokenizer.class,SpaCyTokenizer.PARAM_PYTHON_HOME,pythonHome));
			builder.add(createEngineDescription(SpaCyTagger.class,SpaCyTagger.PARAM_PYTHON_HOME,pythonHome));
			builder.add(createEngineDescription(SpaCyParser.class,SpaCyParser.PARAM_PYTHON_HOME,pythonHome));
			builder.add(createEngineDescription(SpaCyNER.class,SpaCyNER.PARAM_PYTHON_HOME,pythonHome));
			
			SimplePipeline.runPipeline(aJCas,builder.createAggregate());
		}
		catch(UIMAException e) {
			e.printStackTrace();
		}
		
	}
	
	
}
