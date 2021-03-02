package org.hucompute.textimager.uima.gnd.gazetteer;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.SingleClassTreeGazetteer;
import org.texttechnologylab.annotation.type.Person_HumanBeing;

import java.util.HashSet;
import java.util.stream.Collectors;

public class GNDGazetteer extends SingleClassTreeGazetteer {
    @Override
    protected void addMyAnnotation(JCas aJCas, Annotation fromToken, Annotation toToken, String element, HashSet<Object> objects) {
        Person_HumanBeing person = (Person_HumanBeing) aJCas.getCas().createAnnotation(getTaggingType(element), fromToken.getBegin(), toToken.getEnd());

        String name = objects.stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        person.setValue(name);

        aJCas.addFsToIndexes(person);
    }

    @Override
    protected String getGazetteerName() {
        return "gnd";
    }

    @Override
    protected boolean useSimpleLoading() {
        return true;
    }
}
