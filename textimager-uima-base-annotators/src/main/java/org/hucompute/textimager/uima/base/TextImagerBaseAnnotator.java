package org.hucompute.textimager.uima.base;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.texttechnologylab.annotation.AnnotatorMetaData;

public abstract class TextImagerBaseAnnotator extends JCasAnnotator_ImplBase {
	public static final String PARAM_ANNOTATOR_META_NAME = "annotatorMetaName";
	@ConfigurationParameter(name = PARAM_ANNOTATOR_META_NAME, mandatory = false)
	protected String annotatorMetaName;

	public static final String PARAM_ANNOTATOR_META_VERSION = "annotatorMetaVersion";
	@ConfigurationParameter(name = PARAM_ANNOTATOR_META_VERSION, mandatory = false)
	protected String annotatorMetaVersion;

	public static final String PARAM_ANNOTATOR_META_MODEL_NAME = "annotatorMetaModelName";
	@ConfigurationParameter(name = PARAM_ANNOTATOR_META_MODEL_NAME, mandatory = false)
	protected String annotatorMetaModelName;

	public static final String PARAM_ANNOTATOR_META_MODEL_VERSION = "annotatorMetaModelVersion";
	@ConfigurationParameter(name = PARAM_ANNOTATOR_META_MODEL_VERSION, mandatory = false)
	protected String annotatorMetaModelVersion;

	// provide the annotator name
	// defaults to the class name
	protected String getAnnotatorName() {
		return getClass().getCanonicalName();
	}

	// provide a version string for this annotator
	protected abstract String getAnnotatorVersion();

	// provide name and version of used model
	protected abstract String getModelName();
	protected abstract String getModelVersion();

	// add a comment about the used annotator to an annotation
	protected void addAnnotatorComment(JCas aJCas, TOP ref) {
		AnnotatorMetaData metaData = new AnnotatorMetaData(aJCas);
		metaData.setReference(ref);

		if (annotatorMetaName != null && !annotatorMetaName.trim().isEmpty()) {
			metaData.setName(annotatorMetaName);
		}
		else {
			metaData.setName(getAnnotatorName());
		}

		if (annotatorMetaVersion != null && !annotatorMetaVersion.trim().isEmpty()) {
			metaData.setVersion(annotatorMetaVersion);
		}
		else {
			metaData.setVersion(getAnnotatorVersion());
		}

		if (annotatorMetaModelName != null && !annotatorMetaModelName.trim().isEmpty()) {
			metaData.setModelName(annotatorMetaModelName);
		}
		else {
			metaData.setModelName(getModelName());
		}

		if (annotatorMetaModelVersion != null && !annotatorMetaModelVersion.trim().isEmpty()) {
			metaData.setModelVersion(annotatorMetaModelVersion);
		}
		else {
			metaData.setModelVersion(getModelVersion());
		}

		metaData.addToIndexes();
	}
}
