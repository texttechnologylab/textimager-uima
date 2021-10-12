package org.hucompute.textimager.uima.marmot;

import static org.apache.uima.fit.util.JCasUtil.select;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import marmot.morph.MorphTagger;
import marmot.morph.Word;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.ModelProviderBase;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MarMoTLemma extends JCasAnnotator_ImplBase {

	/**
	 * Location from which the model is read.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;
	/**
	 * Use this language instead of the document language to resolve the model.
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	/**
	 * Override the default variant used to locate the model.
	 */
	public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;


	public static final String PARAM_MAX_SENTENCE_LENGTH = "PARAM_MAX_SENTENCE_LENGTH";
	@ConfigurationParameter(name = PARAM_MAX_SENTENCE_LENGTH, mandatory = false, defaultValue="-1")
	protected int maximumSentenceLength;

	private ModelProviderBase<MorphTagger> modelProvider;

	@Override
	public void initialize(UimaContext aContext)
			throws ResourceInitializationException {
		// TODO Auto-generated method stub
		super.initialize(aContext);

		modelProvider = new ModelProviderBase<MorphTagger>(this, "marmot", "lemma")
		{
			@Override
			protected MorphTagger produceResource(InputStream aStream) throws Exception {
				ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream(aStream));
				Object object = stream.readObject();
				stream.close();
				if (object == null) {
					throw new RuntimeException("Object couldn't be deserialized: ");
				}
				return (MorphTagger) object;
			}
		};


	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		long start = System.currentTimeMillis();
		//this.modelLocation="C:\\Users\\sodia\\.m2\\repository\\marmot_models\\all-tiger\\1.0all-tiger-1.0.marmot";
		//modelProvider.getResourceMetaData().get("location");
		//modelProvider.getResourceMetaData().setProperty("location","C:\\marmot\\x.marmot");
		//createPropertiesFile(aJCas);
		String language=aJCas.getDocumentLanguage();
		String aShortName ="marmot";
		String aType ="lemma";

		//String version ="default";
		/*
		String repoPath = aType+"\\"+language+"\\"+version+"\\" ;
		String fileName = language+"-"+version+"."+aShortName;
		String location = System.getProperty("user.home")+"\\"+".m2"+"\\"+"repository"+"\\"+repoPath+fileName;
		modelProvider.setDefault("location",location);*/
		modelProvider.configure(aJCas.getCas());

		//modelProvider.getResourceMetaData().setProperty("location","C:\\marmot\\x.marmot");
		for (Sentence sentence : select(aJCas, Sentence.class)) {
			List<Word> words = new ArrayList<>();
			List<Token>tokens =  JCasUtil.selectCovered(Token.class, sentence);
			if(maximumSentenceLength > 0 && tokens.size() > maximumSentenceLength){
				for (Token token : tokens) {
					token.removeFromIndexes();
				}
				sentence.removeFromIndexes();
				continue;
			}

			for (Token token : tokens) {
				words.add(new Word(token.getCoveredText()));
			}
			List<List<String>> tags = modelProvider.getResource().tag(new marmot.morph.Sentence(words));
			for (int i = 0; i < tags.size(); i++) {
				Lemma lemma = new Lemma(aJCas, tokens.get(i).getBegin(), tokens.get(i).getEnd());
				try{
					lemma.setValue(getLemma(tokens.get(i).getCoveredText(), tags.get(i).get(0)));
				}catch(StringIndexOutOfBoundsException e){
					lemma.setValue("_");
					System.out.println(sentence.getCoveredText());
				}
				lemma.addToIndexes();
				tokens.get(i).setLemma(lemma);
			}
		}

		System.out.println("processing time: " + (System.currentTimeMillis()-start));
	}

	private String getLemma(String token,String input) throws StringIndexOutOfBoundsException{
		String[]split = input.split("\\|");
		String lemma = token;
		if(split.length>1){
			lemma = split[1].split("=")[1].replace("_", "") + lemma.substring(Integer.parseInt(split[0].split("=")[1]));
			lemma = lemma.substring(0,lemma.length()-Integer.parseInt(split[2].split("=")[1]))+split[3].split("=")[1].replace("_", "") ;
		}
		return lemma;
	}

	public void createPropertiesFile(JCas aJCas){
		String language=aJCas.getDocumentLanguage();
		String aShortName ="marmot";
		String aType ="lemma";
		String path = System.getProperty("user.dir")+"\\"+"src"+"\\"+"main"+"\\"+"resources"+"\\"+"org"+"\\"+"hucompute"+"\\"+"textimager"+"\\"+"uima"+"\\"+"marmot"+"\\"+"lib";
		String filename = aType + "-" + language + "-" + "default" + ".properties";
		try {
			File myObj = new File(path+"\\"+filename);
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
				writeProperties(path+"\\"+filename,aJCas);
			} else {
				System.out.println("File already exists.");
			}
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}

	public void  writeProperties(String propFile, JCas aJCas){
		String language=aJCas.getDocumentLanguage();
		String aShortName ="marmot";
		String aType ="lemma";
		String version ="default";
		String timeStamp = new Date().toString();
		//String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH\\:mm").toString();

		String repoPath = aType+"\\"+language+"\\"+version+"\\" ;
		String fileName = language+"-"+version+"."+aShortName;
		String location = System.getProperty("user.home")+"\\"+".m2"+"\\"+"repository"+"\\"+repoPath+fileName;
		location = location.replace("\\", "\\\\");

		String properties="generated="+timeStamp+"\n" +
				"version=20170716.1 \n"+
				"language="+language+"\n"+
				"variant=default"+"\n" +
				"tool="+aType+"\n"  +
				"location="+location;

		try {
			FileWriter myWriter = new FileWriter(propFile);
			myWriter.write(properties);
			myWriter.close();
			System.out.println("Successfully wrote to the file.");
		} catch (IOException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
}


