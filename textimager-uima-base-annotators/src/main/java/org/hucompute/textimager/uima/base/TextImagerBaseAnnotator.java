package org.hucompute.textimager.uima.base;

import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.TOP;
import org.texttechnologylab.annotation.AnnotationComment;

public abstract class TextImagerBaseAnnotator extends JCasAnnotator_ImplBase {
	// provide a version string for this annotator
	protected abstract String getAnnotatorVersion();

	// provide the annotator name
	// defaults to the class name
	protected String getAnnotatorName() {
		return getClass().getCanonicalName();
	}

	// add a comment about the used annotator to an annotation
	protected void addAnnotatorComment(JCas aJCas, TOP ref) {
		AnnotationComment commentName = new AnnotationComment(aJCas);
		commentName.setReference(ref);
		commentName.setKey("_textimager_annotator_name");
		commentName.setValue(getAnnotatorName());
		commentName.addToIndexes();

		AnnotationComment commentVersion = new AnnotationComment(aJCas);
		commentVersion.setReference(ref);
		commentVersion.setKey("_textimager_annotator_version");
		commentVersion.setValue(getAnnotatorVersion());
		commentVersion.addToIndexes();
	}
}
