package org.hucompute.textimager.disambiguation.verbs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.ExternalResource;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.parameter.ComponentParameters;
import org.dkpro.core.api.resources.CasConfigurableProviderBase;
import org.dkpro.core.api.resources.ModelProviderBase;
import org.dkpro.core.api.resources.ResourceUtils;

import com.github.jfasttext.JFastText;
import com.github.jfasttext.JFastText.ProbLabel;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS_VERB;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.WordSense;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tuebingen.uni.sfs.germanet.api.GermaNet;
import de.tuebingen.uni.sfs.germanet.api.LexUnit;
import de.tuebingen.uni.sfs.germanet.api.WordCategory;

public class VerbsDisambiguation extends JCasAnnotator_ImplBase{

	/**
	 * Location from which the model is read.
	 */
	public static final String PARAM_MODEL_LOCATION = ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION, mandatory = false)
	protected String modelLocation;
	private CasConfigurableProviderBase<JFastText> modelProvider;

	/**
	 * Variant of a model the model. Used to address a specific model if here are multiple models
	 * for one language.
	 */
	public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	public static final String PARAM_GERMANET_PATH = "germanetPath";
	@ConfigurationParameter(name = PARAM_GERMANET_PATH, mandatory = false)
	protected String germanetPath;
	
	public static final String PARAM_GERMANET = "gnet";
	@ExternalResource(key=PARAM_GERMANET, mandatory = false, description = "You can pass a GermaNet object instead of a path, to avoid loading germanet multiple times")
	private GNetWrapper gnetwrapper;
	
	protected String verblemmaIdsPath;
	

	public static final String PARAM_ACTIVATE_REDUCER = "ACTIVATE_REDUCER";
	@ConfigurationParameter(name = PARAM_ACTIVATE_REDUCER, mandatory = false,defaultValue="false")
	protected boolean activateReducer;

	HashMap<String, HashSet<String>>verbLemmaIds = new HashMap<>();
	
	TreeReducer tr = null;
	private GermaNet gnet;

	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		super.initialize(context);
		if (gnetwrapper == null) {
			try {
				gnet = new GermaNet(new File(germanetPath));
			} catch (XMLStreamException | IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			gnet = gnetwrapper.getGnet();
		}
		if(activateReducer){
			 tr = new TreeReducer();
			 tr.loadgnet(gnet);
			 tr.reduce();
		}
		modelProvider = new ModelProviderBase<JFastText>()
		{
			{
				setContextObject(VerbsDisambiguation.this);

				setDefault(ARTIFACT_ID, "${groupId}.fasttext-languageidentification-model-${language}-${variant}");
				setDefault(LOCATION,
						"classpath:/${package}/lib/verbsdisambiguation-${language}-${variant}.properties");
				setDefault(VARIANT, "small");

				setOverride(LOCATION, modelLocation);
				setOverride(LANGUAGE, "de");
				setOverride(VARIANT, variant);
				
			}

			@Override
			protected JFastText produceResource(URL aUrl)
					throws IOException
			{
				if (tr == null) {
					try {
						List<String> lines;
						verblemmaIdsPath = aUrl.getFile()+".verbLemmaIds";
						
						lines = FileUtils.readLines(new File(verblemmaIdsPath));
						for (String string : lines) {
							String[]split = string.split("\t");
							HashSet<String>ids = new HashSet<>();
							for (int i = 1; i < split.length; i++) {
								ids.add(split[i]);
							}
							verbLemmaIds.put(split[0], ids);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					verbLemmaIds = tr.getLemmaIds();
				}
				
				JFastText fasttext = new JFastText();
				File profileFolder = ResourceUtils.getUrlAsFile(aUrl, true);
				fasttext.loadModel(profileFolder.getAbsolutePath());
				return fasttext;
			}
		};
	}
	

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {

		modelProvider.configure(aJCas.getCas());
		
		System.out.println("Starting NN Disambiguation...");
		
		for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
			aJCas.getCas().createAnnotation(sentence.getType(), 1, 0);

			for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
				String lemma = token.getLemma().getValue();
				if(gnet.getLexUnits(lemma, WordCategory.verben).size() == 1){
					WordSense sense = new WordSense(aJCas, token.getBegin(), token.getEnd());
					sense.setValue(Integer.toString(gnet.getLexUnits(lemma, WordCategory.verben).get(0).getId()));
					System.out.println("Single sense verb: " + lemma + "\t" + sense.getValue());
//					sense.setConfidence(1);
					sense.addToIndexes();
					continue;
				}
				

				if(token.getPos().getClass() == POS_VERB.class){
					if(JCasUtil.selectCovered(WordSense.class, token).size() > 0) {
						System.out.println("Verb " + lemma + " already annotated");
						continue;
					}
					
					if(gnet.getLexUnits(lemma, WordCategory.verben).isEmpty()){
						WordSense sense = new WordSense(aJCas, token.getBegin(), token.getEnd());
						sense.setValue(Integer.toString(-1));
						System.out.println("No valid senses for " + lemma);
//						sense.setConfidence(1);
						sense.addToIndexes();
					}
					else if(verbLemmaIds.containsKey(lemma)){
						System.out.println("Using FastText...");
						String toAnalize = sentence.getCoveredText();
						for (String string : sentence.getCoveredText().split(",|;|:| und ")) {
							if(sentence.getCoveredText().indexOf(string) <= (token.getBegin()-sentence.getBegin()) && sentence.getCoveredText().indexOf(string)+string.length() >= token.getEnd()-sentence.getBegin())
							{
								toAnalize = (string);
								break;
							}
						}
						toAnalize = toAnalize.replace("\"", " \"")
								.replace(",", " ,")
								.replace(".", " . ")
								.replace(":", " :")
								.replace(";", " ;")
								.replace("?", " ?")
								.replace("!", " !")
								.replace("(", " (")
								.replace(")", " )")
								.replace("-", " -")
								.replaceAll(" -(\\w)", " - $1").trim();
						System.out.println(toAnalize);
						List<ProbLabel> probLabel = modelProvider.getResource().predictProba(toAnalize,100000);
						for (ProbLabel probLabel2 : probLabel) {
							if(verbLemmaIds.get(lemma).contains(probLabel2.label.replace("__label__", ""))){
								WordSense sense = new WordSense(aJCas, token.getBegin(), token.getEnd());
								
								// Do reverse mapping to base GermaNet LexUnit ids
								if (tr == null) sense.setValue(probLabel2.label.replace("__label__", ""));
								else sense.setValue(tr.reverseMap(lemma, probLabel2.label.replace("__label__", "")));
//								sense.setConfidence(Math.exp(probLabel2.logProb));
								sense.addToIndexes();
								System.out.println("Verb " + lemma + " annotated with sense " + sense.getValue());
								break;
							}
						}
					}
				}
			}
		}
	}

}
