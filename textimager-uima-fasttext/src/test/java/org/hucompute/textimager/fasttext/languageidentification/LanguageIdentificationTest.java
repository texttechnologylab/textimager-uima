package org.hucompute.textimager.fasttext.languageidentification;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;

import static org.junit.Assert.*;


public class LanguageIdentificationTest {

	@Test
	public void simpleExampleDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.");
		DocumentMetaData.create(cas).setDocumentId("test");
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				LanguageIdentification.class
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		assertEquals(cas.getDocumentLanguage(), "de");
	}
	
	@Test
	public void simpleExampleEN() throws UIMAException{
		JCas cas = JCasFactory.createText("This is sentence in any language");
		DocumentMetaData.create(cas).setDocumentId("test");
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				LanguageIdentification.class
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		assertEquals(cas.getDocumentLanguage(), "en");
	}
	
	@Test
	public void simpleExampleLA() throws UIMAException{
		JCas cas = JCasFactory.createText("Portus Dives, olim insula Sancti Ioannis Portus Divitis (vulgo Puerto Rico, plenius Hispanice Estado Libre Asociado de Puerto Rico et Anglice Commonwealth of Puerto Rico), est Civitatum Foederatarum territorium non incorporatum cum statuto consortionis. Insula inter Antillas Maiores sita a septemtrione oceano Atlantico et a meridie mari Caribaeo eluitur. Territorium ipsā insulā Portoricensi seu Portudivitensi multisque insulis minoribus Viequensibus necnon Culebra et Mona constituitur.");
		DocumentMetaData.create(cas).setDocumentId("test");
		
		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				LanguageIdentification.class
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		assertEquals(cas.getDocumentLanguage(), "la");
	}
	

}
