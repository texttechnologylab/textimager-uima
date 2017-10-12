package org.textimager.uima.OpenerProject;

import static de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations.assertPOS;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.testing.AssertAnnotations;

import org.hucompute.services.util.XmlFormatter;
import org.hucompute.textimager.uima.OpenerProject.*;


public class OpenerProjectTest {

	
	@Test
	public void TokenizerFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.", "fr");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token.getCoveredText());
		}

		AssertAnnotations.assertToken(new String[] {"Ceci", "est", "un", "bon", "test","."}, JCasUtil.select(cas, Token.class));

	}
	
	@Test
	public void TokenizerDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein \n guter Test.", "de");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		AssertAnnotations.assertToken(new String[] {"Das", "ist", "ein", "guter", "Test","."}, JCasUtil.select(cas, Token.class));

	}
	
	@Test
	public void LanguageTestDE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectLanguageIdentifier.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());

		assertEquals("Language", "de" , cas.getDocumentLanguage());
		
	}
	
	@Test
	public void LanguageTestFR() throws UIMAException{
		JCas cas = JCasFactory.createText("Ceci est un bon test.");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectLanguageIdentifier.class));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		assertEquals("Language", "fr" , cas.getDocumentLanguage());
		
	}
	@Test
	public void POS_DE() throws UIMAException{
		JCas cas = JCasFactory.createText("Das ist ein guter Test.", "fr");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		new Token(cas,0,3).addToIndexes();
		new Token(cas,4,7).addToIndexes();
		new Token(cas,8,11).addToIndexes();
		new Token(cas,12,17).addToIndexes();
		new Token(cas,18,22).addToIndexes();
		new Token(cas,22,23).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectPOSTagger.class
				,OpenerProjectPOSTagger.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/pos-default.map"
				,OpenerProjectPOSTagger.PARAM_JRUBY_LOCATION,"~/jruby/bin/"
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		
		for (Token token : JCasUtil.select(cas, Token.class)) {
			System.out.println(token);
		}

		assertPOS(
				new String[] { "PR", "V", "ART", "ADJ", "NN","O"},
				new String[] { "R", "V", "D", "A", "G","O"}, 
				JCasUtil.select(cas, POS.class));	
	}
	@Test
	public void NER_DE() throws UIMAException, IOException{
		JCas cas = JCasFactory.createText("Merkel wuchs in der DDR auf und war dort als Physikerin wissenschaftlich tätig. Bei der Bundestagswahl am 2. Dezember 1990 errang sie erstmals ein Bundestagsmandat; in allen darauffolgenden sechs Bundestagswahlen wurde sie in ihrem Wahlkreis in Vorpommern direkt gewählt.", "de");
//		JCas cas = JCasFactory.createText(
//				"After breakfast at the Elia Beach Hotel, I and my wife had a walk to Mykonos. There we were picked up and driven to Piraeus Port, where we had lunch with Mr. Vernicos at the Marine Club."
//				, "en");

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class
				));
		builder.add(createEngineDescription(
				OpenerProjectPOSTagger.class
				,OpenerProjectPOSTagger.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/pos-default.map",
				OpenerProjectPOSTagger.PARAM_JRUBY_LOCATION,"~/jruby/bin/"
				));
		builder.add(createEngineDescription(
				OpenerProjectNER.class
				,OpenerProjectNER.PARAM_NAMED_ENTITY_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/ner-default.map"
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	

		System.out.println(cas.getCas());
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
		
		File xml = new File("Out.xml");
		System.out.println(xml.getAbsolutePath());
		FileUtils.writeStringToFile(xml , XmlFormatter.getPrettyString(cas.getCas()));
	}
	
//	@Test
	public void Constituent() throws UIMAException, IOException{
		String text = new String(Files.readAllBytes(Paths.get("wiki_de_text")));
		JCas cas = JCasFactory.createText(text, "de");
//		JCas cas = JCasFactory.createText("Merkel wuchs in der DDR auf und war dort als Physikerin wissenschaftlich tätig. Bei der Bundestagswahl am 2. Dezember 1990 errang sie erstmals ein Bundestagsmandat; in allen darauffolgenden sechs Bundestagswahlen wurde sie in ihrem Wahlkreis in Vorpommern direkt gewählt.", "de");
//		JCas cas = JCasFactory.createText(
//				"After breakfast at the Elia Beach Hotel, I and my wife had a walk to Mykonos. There we were picked up and driven to Piraeus Port, where we had lunch with Mr. Vernicos at the Marine Club."
//				, "en");
		new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

		AggregateBuilder builder = new AggregateBuilder();
		builder.add(createEngineDescription(
				OpenerProjectTokenizer.class
				));
		builder.add(createEngineDescription(
				OpenerProjectPOSTagger.class
				,OpenerProjectPOSTagger.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/pos-default.map",
				OpenerProjectPOSTagger.PARAM_JRUBY_LOCATION,"~/jruby/bin/"
				));
		builder.add(createEngineDescription(
				OpenerProjectConstituentCoref.class,
				OpenerProjectConstituentCoref.PARAM_CONSTITUENT_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/constituent-en-default.map"
				));
		SimplePipeline.runPipeline(cas,builder.createAggregate());
	

		System.out.println(cas.getCas());
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
		
		File xml = new File("Out.xml");
		System.out.println(xml.getAbsolutePath());
		FileUtils.writeStringToFile(xml , XmlFormatter.getPrettyString(cas.getCas()));
	}
//	@Test
	public void FullPipeRuntime() throws UIMAException, IOException{
		String lan = "en";
		String text = new String(Files.readAllBytes(Paths.get("src/test/java/wiki_"+lan+"_text")));
		JCas cas = JCasFactory.createText(text,lan);
		
		AggregateBuilder token = new AggregateBuilder();
		token.add(createEngineDescription(
				OpenerProjectTokenizer.class));
		
		AggregateBuilder POS = new AggregateBuilder();
		POS.add(createEngineDescription(
				OpenerProjectPOSTagger.class
				,OpenerProjectPOSTagger.PARAM_POS_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/pos-default.map"
				));
		
		AggregateBuilder Const = new AggregateBuilder();
		Const.add(createEngineDescription(
				OpenerProjectConstituentCoref.class,
				OpenerProjectConstituentCoref.PARAM_CONSTITUENT_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/constituent-en-default.map"
				));
		
		AggregateBuilder NER = new AggregateBuilder();
		NER.add(createEngineDescription(
				OpenerProjectNER.class
				,OpenerProjectNER.PARAM_NAMED_ENTITY_MAPPING_LOCATION,"src/main/resources/org/hucompute/textimager/uima/OpenerProject/lib/ner-default.map"
				));
		
		long Time_1 = RunPipe(cas, token);
		System.out.println("Tokenizer: " + Time_1 +" seconds");
		long Time_2 = RunPipe(cas, POS);
		System.out.println("POS: " + Time_2 +" seconds");
		long Time_3 = RunPipe(cas, Const);
		System.out.println("Constituent: " + Time_3+" seconds");
		long Time_4 = RunPipe(cas, NER);
		System.out.println("NER: " + Time_4 +" seconds");
		
		
		System.out.println(XmlFormatter.getPrettyString(cas.getCas()));	
		File xml = new File("Out.xml");
		System.out.println(xml.getAbsolutePath());
		FileUtils.writeStringToFile(xml , XmlFormatter.getPrettyString(cas.getCas()));
		
		System.out.println("Tokenizer: " + Time_1 +" seconds");
		System.out.println("POS: " + Time_2 +" seconds");
		System.out.println("Constituent: " + Time_3+" seconds");
		System.out.println("NER: " + Time_4 +" seconds");
		System.out.println("Token: "+JCasUtil.select(cas, Token.class).size());
		System.out.println("Sentence: "+JCasUtil.select(cas, Sentence.class).size());

	}

	public long RunPipe(JCas cas,AggregateBuilder builder) throws AnalysisEngineProcessException, ResourceInitializationException {
		
		Timestamp timestamp1 = new Timestamp(System.currentTimeMillis());
		SimplePipeline.runPipeline(cas,builder.createAggregate());
		Timestamp timestamp2 = new Timestamp(System.currentTimeMillis());	
		Timestamp t3 = new Timestamp(timestamp2.getTime() - timestamp1.getTime());
		
		return (t3.getTime() / 1000);
	}
	
	

}
