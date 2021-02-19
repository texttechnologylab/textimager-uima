package org.hucompute.textimager.uima.biofid.gazetteer;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.SingleClassTreeGazetteer;

import java.util.HashSet;
import java.util.stream.Collectors;

public class BiofidGazetteer extends SingleClassTreeGazetteer {
    @Override
    protected void addMyAnnotation(JCas aJCas, Annotation fromToken, Annotation toToken, String element, HashSet<Object> objects) {
        NamedEntity annotation = (NamedEntity) aJCas.getCas().createAnnotation(getTaggingType(element), fromToken.getBegin(), toToken.getEnd());

        String uris = objects.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        annotation.setValue(uris);
        aJCas.addFsToIndexes(annotation);
    }

    @Override
    protected String getGazetteerName() {
        return "biofid";
    }
}
