package org.hucompute.textimager.uima.gazetteer;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.hucompute.textimager.uima.gazetteer.models.TreeGazetteerModel;

import java.io.IOException;

/**
 * UIMA Engine for tagging taxa from taxonomic lists or gazetteers as resource.
 */
public abstract class SingleClassTreeGazetteer extends BaseTreeGazetteer {

	/**
	 * {@link Type} name (fully qualified class name) of the class to tag. Must subclass {@link NamedEntity}.
	 */
	public static final String PARAM_TAGGING_TYPE_NAME = "pTaggingTypeName";
	@ConfigurationParameter(
			name = PARAM_TAGGING_TYPE_NAME
	)
	protected String pTaggingTypeName;


	protected void createTreeModel() throws IOException, ClassNotFoundException {
		getLogger().info(String.format("Initializing StringTreeGazetteerModel for %s", Class.forName(pTaggingTypeName).getSimpleName()));
		stringTreeGazetteerModel = new TreeGazetteerModel(
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
		skipGramTreeRoot = stringTreeGazetteerModel.getTree();
		skipGramTreeDepth = skipGramTreeRoot.depth();
	}

	@Override
	protected void inferTaggingType(TypeSystem typeSystem) {
		taggingType = typeSystem.getType(pTaggingTypeName);
	}

	@Override
	protected Type getTaggingType(String taxon) {
		return this.taggingType;
	}

}
