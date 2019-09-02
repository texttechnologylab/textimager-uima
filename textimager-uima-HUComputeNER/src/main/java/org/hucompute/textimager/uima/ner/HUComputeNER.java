package org.hucompute.textimager.uima.ner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.Type;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym;
import org.hucompute.textimager.uima.type.wikipedia.WikipediaLink;
import org.texttechnologylab.annotation.AbstractNamedEntity;
import org.texttechnologylab.annotation.type.Group_Collection;
import org.texttechnologylab.annotation.type.Location_Place;
import org.texttechnologylab.annotation.type.Other;
import org.texttechnologylab.annotation.type.Person_HumanBeing;
import org.texttechnologylab.annotation.type.Time;
import org.texttechnologylab.annotation.type.concept.Attribute_Property;
import org.texttechnologylab.annotation.type.concept.Cognition_Ideation;
import org.texttechnologylab.annotation.type.concept.Taxon;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ADJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.ADV;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.CONJ;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.PP;
import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.V;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.dictionaryannotator.PhraseTree;
import de.unihd.dbs.uima.types.heideltime.Timex3;

/**
 * UIMA XMI format writer.
 */
@TypeCapability(
		inputs={
		"de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData"})
public class HUComputeNER
extends JCasAnnotator_ImplBase
{

	/**
	 */
	public static final String PARAM_PRUNE = "PARAM_PRUNE";
	@ConfigurationParameter(name = PARAM_PRUNE, mandatory = true, defaultValue="false")
	protected boolean prune;

	/**
	 */
	public static final String PARAM_CONSTRAINT = "PARAM_CONSTRAINT";
	@ConfigurationParameter(name = PARAM_CONSTRAINT, mandatory = true, defaultValue="false")
	protected boolean constraint;

	public static final String PARAM_MODEL_LOCATION =
			ComponentParameters.PARAM_MODEL_LOCATION;
	@ConfigurationParameter(name = PARAM_MODEL_LOCATION
			, mandatory = false)
	protected String modelLocation;

	public static final String PARAM_NAMED_ENTITY_MAPPING_LOCATION =
			ComponentParameters.PARAM_NAMED_ENTITY_MAPPING_LOCATION;
	@ConfigurationParameter(name = PARAM_NAMED_ENTITY_MAPPING_LOCATION
			, mandatory = false)
	protected String mappingLocation;

	/**
	 * Use this language instead of the document language to resolve the model.
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false)
	protected String language;

	/**
	 * Variant of a model the model. Used to address a specific model if here are multiple models
	 * for one language.
	 */
	public static final String PARAM_VARIANT = ComponentParameters.PARAM_VARIANT;
	@ConfigurationParameter(name = PARAM_VARIANT, mandatory = false)
	protected String variant;

	/**
	 */
	public static final String PARAM_CLASS_MAP = "PARAM_CLASS_MAP";
	@ConfigurationParameter(name = PARAM_CLASS_MAP, mandatory = true)
	protected String classMapPath;

	int countToken = 0;
	int countAnnotations = 0;

	float portion = 0;
	int countSentences = 0;
	int countDoc = 0;

	int finalSentences = 0;
	int finalTokens = 0;
	int finalAnnotations = 0;

	HashMap<String, String>classMap = new HashMap<>();
	private MappingProvider mappingProvider;
	@Override
	public void initialize(UimaContext context) throws ResourceInitializationException {
		// TODO Auto-generated method stub
		super.initialize(context);
		try {
			for (String line : FileUtils.readLines(new File(classMapPath))) {
				String[]split = line.split("\t");
				if(split.length == 2 && !line.startsWith("#"))
					classMap.put(split[0],split[1]);
			}
			System.out.println(classMap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mappingProvider = new MappingProvider();
		mappingProvider
		.setDefaultVariantsLocation("org/hucompute/textimager/uima/ner/lib/ner-default-variants.map");
		mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/org/hucompute/textimager/uima/ner/lib/ner-${language}-${variant}.map");
		mappingProvider.setDefault(MappingProvider.BASE_TYPE, NamedEntity.class.getName());
		mappingProvider.setOverride(MappingProvider.LOCATION, mappingLocation);
		mappingProvider.setOverride(MappingProvider.LANGUAGE, language);
		mappingProvider.setOverride(MappingProvider.VARIANT, variant);
	}

	@Override
	public void process(JCas aJCas)
			throws AnalysisEngineProcessException
	{
		mappingProvider.configure(aJCas.getCas());

		System.out.println(countDoc++);
		try{
			if(!JCasUtil.select(aJCas, WikiDataHyponym.class).isEmpty() && !JCasUtil.select(aJCas, Token.class).isEmpty() ){

				//fehlerhafte hyponyms entfernen
				for (WikiDataHyponym wikihypo: JCasUtil.select(aJCas, WikiDataHyponym.class)) {
					if(wikihypo.getBegin() == 0 && wikihypo.getEnd() == 0)
						wikihypo.removeFromIndexes();
				}

				Map<Token, Collection<WikiDataHyponym>> indexHyponym = JCasUtil.indexCovering(aJCas, Token.class, WikiDataHyponym.class);
				Map<Token, Collection<Timex3>> indexTimex = JCasUtil.indexCovering(aJCas, Token.class, Timex3.class);
				HashMap<Token, Collection<NamedEntity>> indexEntitiy = new HashMap<>(JCasUtil.indexCovering(aJCas, Token.class, NamedEntity.class));
				Map<WikiDataHyponym, Collection<WikipediaLink>>indexWikidataWikipedia = JCasUtil.indexCovered(aJCas, WikiDataHyponym.class, WikipediaLink.class);

				for (Sentence sentence : JCasUtil.select(aJCas, Sentence.class)) {
					countSentences++;
					int countTokens = JCasUtil.selectCovered(Token.class, sentence).size();
					int countAnnos = 0;
					for (Token token : JCasUtil.selectCovered(Token.class, sentence)) {
						if(indexEntitiy.containsKey(token) && !indexEntitiy.get(token).isEmpty()){
							NamedEntity ent = indexEntitiy.get(token).iterator().next();
							if(ent.getClass() == org.texttechnologylab.annotation.type.Taxon.class)
								ent.setValue(((org.texttechnologylab.annotation.type.Taxon)ent).getIdentifier());
							if(ent.getValue() != null && ent.getValue().split("-").length > 1){
								String value = ent.getValue().split("-")[1];
								switch (value) {
								case "PER":
									Person_HumanBeing human = new Person_HumanBeing(aJCas,ent.getBegin(),ent.getEnd());
									human.setValue("Person_HumanBeing");
									if(JCasUtil.selectCovered(Person_HumanBeing.class, human).isEmpty()){
										human.addToIndexes();
										indexEntitiy.remove(token);
										ent.removeFromIndexes();
									}
									break;
								case "ORG":
									Group_Collection group = new Group_Collection(aJCas,ent.getBegin(),ent.getEnd());
									group.setValue("Group_Collection");
									if(JCasUtil.selectCovered(Group_Collection.class, group).isEmpty()){
										group.addToIndexes();
										indexEntitiy.remove(token);
										ent.removeFromIndexes();
									}
									break;
								case "LOC":
									Location_Place location_Place = new Location_Place(aJCas,ent.getBegin(),ent.getEnd());
									location_Place.setValue("Location_Place");
									if(JCasUtil.selectCovered(Location_Place.class, location_Place).isEmpty()){
										location_Place.addToIndexes();
										indexEntitiy.remove(token);
										ent.removeFromIndexes();
									}
									break;
								case "MISC":
									if(token.getBegin() == ent.getBegin() && token.getEnd() == ent.getEnd() && (token.getPos().getClass() == ADJ.class || token.getPos().getClass() == PP.class || token.getPos().getClass() == V.class ))
										ent.removeFromIndexes();
									else{
										Other other = new Other(aJCas,ent.getBegin(),ent.getEnd());
										other.setValue("Other");
										if(JCasUtil.selectCovered(Other.class, other).isEmpty()){
											other.addToIndexes();
											indexEntitiy.remove(token);
											ent.removeFromIndexes();
										}
									}
									break;
								default:
									break;
								}
							}
						}
						if(!indexHyponym.get(token).isEmpty()){
							countAnnos++;
							WikiDataHyponym lowestDepth = null;
							int lowestDeptSize = Integer.MAX_VALUE;
							for (WikiDataHyponym hyponym : indexHyponym.get(token)) {
								if(classMap.containsKey(hyponym.getId()) && hyponym.getDepth() < lowestDeptSize)
								{
									lowestDepth = hyponym;
									lowestDeptSize = hyponym.getDepth();
								}
							}
							if(lowestDepth != null){

								//								if(!indexEntitiy.get(token).isEmpty()){
								//									for (NamedEntity ne : indexEntitiy.get(token)) {
								//										ne.removeFromIndexes();
								//									}
								//								}

								for (NamedEntity ne : JCasUtil.selectCovering(NamedEntity.class, token)) {
									ne.removeFromIndexes();
								}
								boolean isInstance = indexWikidataWikipedia.get(lowestDepth).iterator().next().getIsInstance();
								Type type = mappingProvider.getTagType(lowestDepth.getId()+(isInstance?"":"_abstract"));
								Annotation ne = (Annotation) aJCas.getCas().createAnnotation(type, lowestDepth.getBegin(), lowestDepth.getEnd());
								if(ne instanceof NamedEntity)
								{
//									((NamedEntity)ne).setValue(classMap.get(lowestDepth.getId()));
									((NamedEntity)ne).setValue(
											"http://www.wikidata.org/entity/"+
											indexWikidataWikipedia.get(lowestDepth).iterator().next().getWikiData());
								}
								else{
//									((AbstractNamedEntity)ne).setValue(classMap.get(lowestDepth.getId())+"_abstract");
									((AbstractNamedEntity)ne).setValue(
											"http://www.wikidata.org/entity/"+
											indexWikidataWikipedia.get(lowestDepth).iterator().next().getWikiData());
								}

								if(token.getBegin() == ne.getBegin() && token.getEnd() == ne.getEnd() && (token.getPos().getClass() == ADJ.class || token.getPos().getClass() == PP.class || token.getPos().getClass() == V.class || token.getPos().getClass() == CONJ.class|| token.getPos().getClass() == ADV.class)){
									//									System.out.println("is ADJ");
								}
								else
									ne.addToIndexes();
							}
						}
						if(!indexTimex.get(token).isEmpty()){
							Timex3 time = indexTimex.get(token).iterator().next();
							if(JCasUtil.selectCovering(NamedEntity.class, token).isEmpty()){
								Time ne = new Time(aJCas, time.getBegin(), time.getEnd());
								ne.setValue("Time");
								ne.addToIndexes();
							}
						}
						if((token.getCoveredText().endsWith("heit")||
								token.getCoveredText().endsWith("heiten")||
								token.getCoveredText().endsWith("keit")||
								token.getCoveredText().endsWith("keiten")) && Character.isUpperCase(token.getCoveredText().charAt(0)) && 
								JCasUtil.selectCovering(NamedEntity.class, token).isEmpty())
						{

							Attribute_Property ne = new Attribute_Property(aJCas, token.getBegin(), token.getEnd());
							ne.setValue("Attribute_Property_abstract");
							ne.addToIndexes();
						}
						if(token.getLemma().getValue().endsWith("ismus") && Character.isUpperCase(token.getLemma().getValue().charAt(0)) && 
								JCasUtil.selectCovering(NamedEntity.class, token).isEmpty()){

							Cognition_Ideation ne = new Cognition_Ideation(aJCas, token.getBegin(), token.getEnd());
							ne.setValue("Cognition_Ideation_abstract");
							ne.addToIndexes();
						}
					}
					if(prune){
						portion += (float)countAnnos/countTokens;
						if((float)countAnnos/countTokens > 0.3 && sentence.getCoveredText().endsWith(".") && Character.isUpperCase(sentence.getCoveredText().charAt(0))
								&& !sentence.getCoveredText().contains(":") && countTokens > 6){
							finalSentences++;
							finalTokens += countTokens;
							finalAnnotations += countAnnos;
						}
						else{
							for (Annotation token : JCasUtil.selectCovered(Annotation.class,sentence)) {
								token.removeFromIndexes();
							};
							sentence.removeFromIndexes();
						}
						if(JCasUtil.selectCovered(WikiDataHyponym.class, sentence).size()==0){
							for (Annotation token : JCasUtil.selectCovered(Annotation.class,sentence)) {
								token.removeFromIndexes();
							};
							sentence.removeFromIndexes();
						}
					}
				}
				if(constraint){
					PhraseTree phrases = new PhraseTree();
					HashMap<String, String>phrasesClasses = new HashMap<>();
					for (NamedEntity ne: JCasUtil.select(aJCas, NamedEntity.class)) {
						String[] phraseSplit = ne.getCoveredText().split(" ");
						phrases.addPhrase(phraseSplit);
						phrasesClasses.put(ne.getCoveredText(), ne.getValue());
					}


					for (Sentence currSentence : JCasUtil.select(aJCas, Sentence.class)) {
						ArrayList<Token> tokens = new ArrayList<Token>(JCasUtil.selectCovered(Token.class, currSentence));

						for (int i = 0; i < tokens.size(); i++) {
							List<Token> tokensToSentenceEnd = tokens.subList(i, tokens.size() - 1);
							String[] sentenceToEnd = new String[tokens.size()];

							for (int j = 0; j < tokensToSentenceEnd.size(); j++) {
								sentenceToEnd[j] = tokensToSentenceEnd.get(j).getCoveredText();
							}

							String[] longestMatch = phrases.getLongestMatch(sentenceToEnd);

							if (longestMatch != null) {
								Token beginToken = tokens.get(i);
								Token endToken = tokens.get(i + longestMatch.length - 1);

								NamedEntity ne = new NamedEntity(aJCas,beginToken.getBegin(), endToken.getEnd());
								Type type = mappingProvider.getTagType(phrasesClasses.get(ne.getCoveredText()));
								Annotation nean = (Annotation) aJCas.getCas().createAnnotation(type, ne.getBegin(), ne.getEnd());
								if(ne instanceof NamedEntity)
									((NamedEntity)ne).setValue(phrasesClasses.get(ne.getCoveredText()));

								if(JCasUtil.selectCovering(NamedEntity.class, ne).isEmpty())
									nean.addToIndexes();
							}
						}
					}


					//abstract entity
					PhraseTree phrases_abstract = new PhraseTree();
					HashMap<String, String>phrasesClasses_abstract = new HashMap<>();
					for (AbstractNamedEntity ne: JCasUtil.select(aJCas, AbstractNamedEntity.class)) {
						String[] phraseSplit = ne.getCoveredText().split(" ");
						phrases_abstract.addPhrase(phraseSplit);
						phrasesClasses_abstract.put(ne.getCoveredText(), ne.getValue());
					}


					for (Sentence currSentence : JCasUtil.select(aJCas, Sentence.class)) {
						ArrayList<Token> tokens = new ArrayList<Token>(JCasUtil.selectCovered(Token.class, currSentence));

						for (int i = 0; i < tokens.size(); i++) {
							List<Token> tokensToSentenceEnd = tokens.subList(i, tokens.size() - 1);
							String[] sentenceToEnd = new String[tokens.size()];

							for (int j = 0; j < tokensToSentenceEnd.size(); j++) {
								sentenceToEnd[j] = tokensToSentenceEnd.get(j).getCoveredText();
							}

							String[] longestMatch = phrases_abstract.getLongestMatch(sentenceToEnd);

							if (longestMatch != null) {
								Token beginToken = tokens.get(i);
								Token endToken = tokens.get(i + longestMatch.length - 1);

								AbstractNamedEntity ne = new AbstractNamedEntity(aJCas,beginToken.getBegin(), endToken.getEnd());
								Type type = mappingProvider.getTagType(phrasesClasses_abstract.get(ne.getCoveredText()));
								Annotation nean = (Annotation) aJCas.getCas().createAnnotation(type, ne.getBegin(), ne.getEnd());
								if(ne instanceof AbstractNamedEntity)
									((AbstractNamedEntity)ne).setValue(phrasesClasses_abstract.get(ne.getCoveredText()));

								if(JCasUtil.selectCovering(AbstractNamedEntity.class, ne).isEmpty())
									nean.addToIndexes();
							}
						}
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
