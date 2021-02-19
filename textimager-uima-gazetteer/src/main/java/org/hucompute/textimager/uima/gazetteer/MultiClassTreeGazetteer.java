package org.hucompute.textimager.uima.gazetteer;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.gazetteer.models.ITreeGazetteerModel;
import org.hucompute.textimager.uima.gazetteer.models.MultiClassTreeGazetteerModel;

import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * UIMA Engine for tagging taxa from taxonomic lists or gazetteers as resource.
 */
public abstract class MultiClassTreeGazetteer extends BaseTreeGazetteer {
	
	/**
	 * A class mapping for the {@link #PARAM_SOURCE_LOCATION} entries. Must be an array of fully qualified class names
	 * of the same length as the source locations array.
	 */
	public static final String PARAM_CLASS_MAPPING = "pClassMapping";
	@ConfigurationParameter(name = PARAM_CLASS_MAPPING)
	protected String[] pClassMapping;
	protected Type[] taggingTypes;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		if (sourceLocation.length != pClassMapping.length)
			throw new ResourceInitializationException(
					new InvalidParameterException(String.format(
							"Array lengths do not match!\n PARAM_SOURCE_LOCATION:%d <> PARAM_CLASS_MAPPING:%d",
							sourceLocation.length, pClassMapping.length)
					)
			);
		taggingTypes = new Type[pClassMapping.length];
	}
	
	@Override
	protected void createTreeModel() throws IOException, ClassNotFoundException {
		getLogger().info("Initializing MultiClassTreeGazetteerModel");
		stringTreeGazetteerModel = new MultiClassTreeGazetteerModel(
				sourceLocation,
				pUseLowercase,
				language,
				pMinLength,
				pGetAllSkips,
				pSplitHyphen,
				pAddAbbreviatedTaxa,
				pMinWordCount,
				tokenBoundaryRegex,
				getFilterSet(),
				getGazetteerName()
		);
		skipGramTreeRoot = ((ITreeGazetteerModel) stringTreeGazetteerModel).getTree();
		skipGramTreeDepth = skipGramTreeRoot.depth();
	}
	
	@Override
	protected void inferTaggingType(TypeSystem typeSystem) {
		for (int i = 0; i < pClassMapping.length; i++) {
			taggingTypes[i] = typeSystem.getType(pClassMapping[i]);
		}
	}
	
	@Override
	protected Type getTaggingType(String taxon) {
		return taggingTypes[((MultiClassTreeGazetteerModel) stringTreeGazetteerModel).getClassIdFromTaxon(taxon)];
	}
}
