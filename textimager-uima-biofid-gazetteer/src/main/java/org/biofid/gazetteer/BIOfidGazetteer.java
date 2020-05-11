package org.biofid.gazetteer;

import com.google.common.collect.Lists;
import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.biofid.gazetteer.Models.SkipGramGazetteerModel;
import org.texttechnologylab.annotation.type.Taxon;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * UIMA Engine for tagging taxa from taxonomic lists or gazetteers as resource.
 *
 * @deprecated Use {@link BIOfidTreeGazetteer} instead. Will be removed in future versions.
 */
@Deprecated

public class BIOfidGazetteer extends SegmenterBase {
	
	/**
	 * Text and model language. Default is "de".
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false, defaultValue = "de")
	protected static String language;
	
	/**
	 * Location from which the taxon data is read.
	 */
	public static final String PARAM_SOURCE_LOCATION = ComponentParameters.PARAM_SOURCE_LOCATION;
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = false, defaultValue = "https://www.texttechnologylab.org/files/BIOfidTaxa.zip")
	protected String[] sourceLocation;
	
	/**
	 * Minimum skip-gram string length
	 */
	public static final String PARAM_MIN_LENGTH = "pMinLength";
	@ConfigurationParameter(name = PARAM_MIN_LENGTH, mandatory = false, defaultValue = "5")
	protected Integer pMinLength;
	
	/**
	 * Boolean, if true use lower case.
	 */
	public static final String PARAM_USE_LOWERCASE = "pUseLowercase";
	@ConfigurationParameter(name = PARAM_USE_LOWERCASE, mandatory = false, defaultValue = "false")
	protected static Boolean pUseLowercase;
	
	/**
	 * Boolean, if true get all m-skip-n-grams for which n > 2 holds, not just 1-skip-(n-1)-grams.
	 */
	public static final String PARAM_GET_ALL_SKIPS = "pGetAllSkips";
	@ConfigurationParameter(name = PARAM_GET_ALL_SKIPS, mandatory = false, defaultValue = "false")
	protected static Boolean pGetAllSkips;
	
	/**
	 * Boolean, if not false, split taxa on spaces and hyphens too.
	 */
	public static final String PARAM_SPLIT_HYPEN = "pSplitHyphen";
	@ConfigurationParameter(name = PARAM_SPLIT_HYPEN, mandatory = false, defaultValue = "true")
	protected static Boolean pSplitHyphen;
	
	MappingProvider namedEntityMappingProvider;
	
	final AtomicInteger atomicTaxonMatchCount = new AtomicInteger(0);
	SkipGramGazetteerModel skipGramGazetteerModel;
	
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		namedEntityMappingProvider = new MappingProvider();
		namedEntityMappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/org/hucompute/textimager/biofid/lib/ner-default.map");
		namedEntityMappingProvider.setDefault(MappingProvider.BASE_TYPE, NamedEntity.class.getName());
		namedEntityMappingProvider.setOverride(MappingProvider.LANGUAGE, "de");
		
		try {
			skipGramGazetteerModel = new SkipGramGazetteerModel(sourceLocation, pUseLowercase, language, pMinLength, pGetAllSkips, pSplitHyphen);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		process(aJCas, aJCas.getDocumentText(), 0);
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {
		namedEntityMappingProvider.configure(aJCas.getCas());
		
		atomicTaxonMatchCount.set(0);
		ArrayList<Token> tokens = Lists.newArrayList(JCasUtil.select(aJCas, Token.class));
		
		skipGramGazetteerModel.stream()
				.parallel()
				.forEach(taxonSkipGram -> tagAllMatches(aJCas, tokens, taxonSkipGram));
	}
	
	/**
	 * Find and tag all occurrences of the given taxon skip gram in the
	 *
	 * @param aJCas
	 * @param tokens
	 * @param taxonSkipGram
	 */
	private void tagAllMatches(JCas aJCas, final ArrayList<Token> tokens, String taxonSkipGram) {
		final String[] skipGramSplit = taxonSkipGram.split(" ");
		
		final ArrayList<String> tokenCoveredText = tokens.stream()
				.map(Token::getCoveredText)
				.map(s -> pUseLowercase ? s.toLowerCase(Locale.forLanguageTag(language)) : s)
				.collect(Collectors.toCollection(ArrayList::new));
		
		List<String> tokenSubList = tokenCoveredText.subList(0, tokenCoveredText.size());
		
		int taxonStartIndex;
		int currOffset = 0;
		do {
			taxonStartIndex = tokenSubList.indexOf(skipGramSplit[0]);
			if (taxonStartIndex > -1) {
				try {
					// This boolean stays true, if all tokens from the taxon can be matched to a sub list
					boolean fullMatch = true;
					int matchLength = 0;
					if (skipGramSplit.length > 1) {
						for (int i = 1; i < skipGramSplit.length; i++) {
							if (taxonStartIndex + i >= tokenSubList.size())
								break;
							fullMatch &= tokenSubList.get(taxonStartIndex + i).equals(skipGramSplit[i]);
							matchLength = i;
						}
					}
					
					if (fullMatch) {
						Token fromToken = tokens.get(currOffset + taxonStartIndex);
						Token toToken = tokens.get(currOffset + taxonStartIndex + matchLength);
						Taxon taxon = new Taxon(aJCas, fromToken.getBegin(), toToken.getEnd());
						
						String uris = skipGramGazetteerModel.getUriFromSkipGram(taxonSkipGram).stream()
								.map(URI::toString)
								.collect(Collectors.joining(","));
						taxon.setValue(uris);
						aJCas.addFsToIndexes(taxon);
						atomicTaxonMatchCount.incrementAndGet();
					}
					
					currOffset += taxonStartIndex + matchLength + 1;
					if (matchLength >= tokenSubList.size() || currOffset >= tokens.size())
						break;
					
					tokenSubList = tokenCoveredText.subList(currOffset, tokens.size());
				} catch (IndexOutOfBoundsException e) {
					throw e; // FIXME
				}
			}
		} while (taxonStartIndex > -1);
	}
	
}
