package org.hucompute.textimager.uima.biofid.general.gazetteer;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.MultiClassTreeGazetteer;
import org.texttechnologylab.annotation.NamedEntity;

import java.util.HashSet;

public class BiofidGeneralGazetteer extends MultiClassTreeGazetteer {
    @Override
    protected void addMyAnnotation(JCas aJCas, Annotation fromToken, Annotation toToken, String element, HashSet<Object> objects) {
        getTaggingType(element).forEach(type -> {
            NamedEntity ne = (NamedEntity) aJCas.getCas().createAnnotation(type, fromToken.getBegin(), toToken.getEnd());
            aJCas.addFsToIndexes(ne);
        });
    }

    @Override
    protected String getGazetteerName() {
        return "biofid-general";
    }

    @Override
    protected boolean useSimpleLoading() {
        return true;
    }
}
