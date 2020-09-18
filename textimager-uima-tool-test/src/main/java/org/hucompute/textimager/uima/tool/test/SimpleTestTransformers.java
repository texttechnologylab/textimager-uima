package org.hucompute.textimager.uima.tool.test;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.transformers.NERTransformers;
import org.hucompute.textimager.uima.transformers.SentAnaTransformers;
import org.hucompute.textimager.uima.transformers.TextSumTransformers;
import org.hucompute.textimager.uima.util.XmlFormatter;


import jep.JepException;

public class SimpleTestTransformers {

	public static void main(String[] args) throws UIMAException, JepException {


			JCas cas = JCasFactory.createText("William Henry Gates III (born October 28, 1955) is an American business magnate, software developer, investor, and philanthropist. He is best known as the co-founder of Microsoft Corporation.[2][3] During his career at Microsoft, Gates held the positions of chairman, chief executive officer (CEO), president and chief software architect, while also being the largest individual shareholder until May 2014. He is one of the best-known entrepreneurs and pioneers of the microcomputer revolution of the 1970s and 1980s. ","en");
			AggregateBuilder builder = new AggregateBuilder();

			//builder.add(createEngineDescription(NERTransformers.class,NERTransformers.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
			//builder.add(createEngineDescription(SentAnaTransformers.class,SentAnaTransformers.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
			builder.add(createEngineDescription(TextSumTransformers.class,TextSumTransformers.PARAM_PYTHON_HOME,"C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38"));
			SimplePipeline.runPipeline(cas,builder.createAggregate());
						
			System.out.println(XmlFormatter.getPrettyString(cas.getCas()));


		}
}
