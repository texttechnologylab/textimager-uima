package org.hucompute.textimager.uima.IXA;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.services.util.XmlFormatter;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;

public class IXATest {

	@Test
	public void PipelineTestFR() throws UIMAException, IOException{
		JCas cas = JCasFactory.createText("Ceci est un bon test. Ceci est un bon test.", "fr");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				IXATokenizer.class));
		builder.add(createEngineDescription(
				IXAPOS.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		//AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));
		
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));	
		File xml = new File("Out.xml");
		System.out.println(xml.getAbsolutePath());
		FileUtils.writeStringToFile(xml , XmlFormatter.getPrettyString(cas.getCas()));
	}

}
