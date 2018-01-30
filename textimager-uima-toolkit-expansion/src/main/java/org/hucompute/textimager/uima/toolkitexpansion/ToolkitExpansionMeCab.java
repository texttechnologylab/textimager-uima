package org.hucompute.textimager.uima.toolkitexpansion;

import static org.apache.uima.fit.util.JCasUtil.selectCovered;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
//import org.hucompute.textimager.uima.OpenerProject.JCastoKaf;
import org.junit.Test;
//import org.chasen.mecab.Tagger;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.CasConfigurableProviderBase;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProviderFactory;
//import de.tudarmstadt.ukp.dkpro.core.mecab.MeCabTagger;
import de.tudarmstadt.ukp.dkpro.core.api.resources.ResourceUtils;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import ixa.kaflib.KAFDocument;

@TypeCapability(inputs = { "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" }, outputs = {
		"de.tudarmstadt.ukp.dkpro.core.mecab.type.JapaneseToken",
		"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS",
		"de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.Lemma",
		"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence" })
public class ToolkitExpansionMeCab extends JCasAnnotator_ImplBase {
	/**
	 * Load the part-of-speech tag to UIMA type mapping from this location instead
	 * of locating the mapping automatically.
	 */
	public static final String PARAM_JRUBY_LOCATION = "PARAM_JRUBY_LOCATION";
	@ConfigurationParameter(name = PARAM_JRUBY_LOCATION, mandatory = false)
	protected String gccLocation;

	/**
	 * Use this language instead of the document language to resolve the model.
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;
	// language = "japanese";

	/**
	 * Override the default variant used to locate the model.
	 */
	public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;

	public static final String PARAM_POS_MAPPING_LOCATION = ComponentParameters.PARAM_POS_MAPPING_LOCATION;
	@ConfigurationParameter(name = PARAM_POS_MAPPING_LOCATION, mandatory = false)
	protected String posMappingLocation;
	private CasConfigurableProviderBase<File> modelProvider;
	private MappingProvider posMappingProvider;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		modelProvider = new CasConfigurableProviderBase<File>() {
			{
				setContextObject(ToolkitExpansionMeCab.this);

				setDefault(ARTIFACT_ID, "${groupId}.OpenerProject-model-tagger-${language}-${variant}");
				setDefault(LOCATION,
						"classpath:org/hucompute/textimager/uima/OpenerProject/lib/" + "tagger-${variant}.model");
				setDefault(VARIANT, "default");

				setOverride(LOCATION, modelLocation);
				setOverride(LANGUAGE, language);
				setOverride(VARIANT, variant);
			}

			@Override
			protected File produceResource(URL aUrl) throws IOException {
				return ResourceUtils.getUrlAsFile(aUrl, true);
			}
		};

		posMappingProvider = MappingProviderFactory.createPosMappingProvider(posMappingLocation, language,
				modelProvider);
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// Needed for Mapping
		CAS cas = aJCas.getCas();
		modelProvider.configure(cas);
		posMappingProvider.configure(cas);
		
		//Generate KAF File
		JCastoKaf jkaf = new JCastoKaf(aJCas);
		jkaf.add_POS_Lemma();
		KAFDocument kaf = jkaf.getKaf();
		String KAF_LOCATION = jkaf.KAF_LOCATION;
		kaf.save(KAF_LOCATION);
		
		//TODO: Path to gcc einfügen
		String pathToGcc= "~/jruby/bin/";
		if(gccLocation != null) pathToGcc=gccLocation;
		
		List<String> cmd = new ArrayList<String>();
	    cmd.add("/bin/sh");
	    cmd.add("-c");
	    cmd.add("export PATH=/usr/bin:$PATH && cat" 
	    + " \"" + KAF_LOCATION+"\"" + 
	    " | "+pathToGcc+"jruby -S tokenizer");

		// Define ProcessBuilder
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectError(Redirect.INHERIT);
        
	    //ProcessBuilder pb = new ProcessBuilder(PythonPATH, POLYGLOT_LOCATION + "language.py", "token",
	    //		sentence.getCoveredText());
	    //pb.redirectError(Redirect.INHERIT);
	    	    
	    
	    boolean success = false;
	    Process proc = null;
	    
	    try {
	        // Start Process
	        proc = pb.start();

	        // IN, OUT, ERROR Streams
	        PrintWriter out = new PrintWriter(
	        new OutputStreamWriter(proc.getOutputStream()));
	        BufferedReader in = new BufferedReader(
	        new InputStreamReader(proc.getInputStream()));
	        BufferedReader error = new BufferedReader(
	        new InputStreamReader(proc.getErrorStream()));

	        StringBuilder builder = new StringBuilder();
	        String line = null;
	        while ( (line = in.readLine()) != null) {
	        	builder.append(line);
	        	builder.append(System.getProperty("line.separator"));
	        }

	        String result = builder.toString();

	        //result in UIMA schreiben

	        // Get Errors
	        String errorString = "";
	        line = "";
	        try {
	        	while ((line = error.readLine()) != null) {
	        		errorString += line+"\n";
	        	}
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }

	        // Log Error
	        if(errorString != "") {
	        	getLogger().error(errorString);
	        }

	        success = true;
	    }
	    catch (IOException e) {
	        	throw new AnalysisEngineProcessException(e);
	    }

	    finally {
	        if (!success) {

	        }

	        if (proc != null) {
	        	proc.destroy();
	        }
	    }
		
		// Get JCas text and language
		String language = aJCas.getDocumentLanguage();
		String originalText = aJCas.getDocumentText();

		if (modelLocation == null)
			modelLocation = "classpath:/org/hucompute/textimager/uima/toolkitexpansion/";
		// Set Properties for pipe
		Properties properties = new Properties();
		properties.setProperty("language", language);
		properties.setProperty("model", modelLocation + language + "/" + language + "-pos-perceptron.bin");
		properties.setProperty("lemmatizerModel", modelLocation + language + "/" + language + "-lemma-perceptron.bin");

		// TODO StatisticalTagger tagger = new StatisticalTagger(properties);

		for (Sentence jCasSentence : JCasUtil.select(aJCas, Sentence.class)) {
			List<Token> jCasTokens = selectCovered(aJCas, Token.class, jCasSentence);
			List<String> tokenList = new ArrayList<String>();
			for (Token jCasToken : jCasTokens) {
				tokenList.add(jCasToken.getCoveredText());
			}
			// TODO List<String> posTags = tagger.posAnnotate(tokenList.toArray(new String[]
			// {}));
			// List<Morpheme> morphTags = tagger.getMorphemesFromStrings(posTags,
			// tokenList.toArray(new String[] {}));

			// TODO StatisticalLemmatizer lemmatizer = new
			// StatisticalLemmatizer(properties);
			// TODO List<String> lemmaTags = lemmatizer.lemmatize(tokenList.toArray(new
			// String[] {}), posTags.toArray(new String[] {}));

			int i = 0;
			for (Token jCasToken : jCasTokens) {
				int begin = jCasToken.getBegin();
				int end = jCasToken.getEnd();

				// TODO String tag = posTags.get(i);
				// TODO tag = Resources.getKafTagSet(tag, aJCas.getDocumentLanguage());
				// TODO Type posTag = posMappingProvider.getTagType(tag);
				// TODO POS posAnno = (POS) cas.createAnnotation(posTag, begin, end);
				// TODO posAnno.setPosValue(tag);
				// TODO posAnno.addToIndexes();

				// TODO Lemma lemma = new Lemma(aJCas, begin, end);
				// TODO lemma.setValue(lemmaTags.get(i));
				// TODO lemma.addToIndexes();

				i++;
			}
		}

	}
}
