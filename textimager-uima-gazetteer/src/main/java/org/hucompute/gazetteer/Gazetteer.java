package org.hucompute.gazetteer;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;
import de.tudarmstadt.ukp.dkpro.core.api.resources.MappingProvider;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.SegmenterBase;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.gazetteer.Models.KeyValueObject;
import org.hucompute.gazetteer.Models.ObjectCollection;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UIMA Engine for tagging names gazetteers as resource.
 *
*/


public class Gazetteer extends SegmenterBase {
	
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
	@ConfigurationParameter(name = PARAM_SOURCE_LOCATION, mandatory = true, defaultValue = "")
	protected String sourceLocation;

	/**
	 * Location from which the taxon data is read.
	 */
	public static final String PARAM_PATTERNS = ComponentParameters.PARAM_PATTERNS;
	@ConfigurationParameter(name = PARAM_PATTERNS, mandatory = true, defaultValue = "Name,Uri,MainClass,SubClass")
	protected String paramPatterns;
	
	/**
	 * Boolean, if true use lower case.
	 */
	public static final String PARAM_USE_LOWERCASE = "pUseLowercase";
	@ConfigurationParameter(name = PARAM_USE_LOWERCASE, mandatory = false, defaultValue = "false")
	protected static Boolean pUseLowercase;

	MappingProvider namedEntityMappingProvider;
	
	protected final AtomicInteger atomicTaxonMatchCount = new AtomicInteger(0);
	protected ObjectCollection objects;
	
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		objects = new ObjectCollection(sourceLocation, paramPatterns);
	}
	
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		process(aJCas, aJCas.getDocumentText(), 0);
	}
	
	@Override
	protected void process(JCas aJCas, String text, int zoneBegin) throws AnalysisEngineProcessException {


	}
	
	/**
	 * Find and tag all occurrences of the given taxon skip gram in the
	 *
	 * @param aJCas
	 * @param tokens
	 * @param pObject
	 */

	public void tagAllMatches(JCas aJCas, final ArrayList<Token> tokens, KeyValueObject pObject) {

	}
	
}
