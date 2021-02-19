package org.hucompute.textimager.uima.geonames.gazetteer;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.SingleClassTreeGazetteer;
import org.texttechnologylab.annotation.GeoNamesEntity;

import java.util.HashSet;
import java.util.stream.Collectors;

public class GeonamesGazetteer extends SingleClassTreeGazetteer {
    @Override
    protected void addMyAnnotation(JCas aJCas, Annotation fromToken, Annotation toToken, String element, HashSet<Object> objects) {
        //GeoNamesEntity geoNames = new GeoNamesEntity(aJCas, fromToken.getBegin(), toToken.getEnd());
        GeoNamesEntity geoNames = (GeoNamesEntity) aJCas.getCas().createAnnotation(getTaggingType(element), fromToken.getBegin(), toToken.getEnd());

        String sID = objects.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        String[] sSplit = sID.split("~");

        if (sSplit[0] != null) {
            geoNames.setId(Integer.valueOf(sSplit[0]));
        }
        try {
            if (sSplit.length > 1 && sSplit[1] != null) {
                geoNames.setMainclass(sSplit[1]);
            }
            if (sSplit.length > 2 && sSplit[2] != null) {
                geoNames.setSubclass(sSplit[2]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        aJCas.addFsToIndexes(geoNames);
    }

    @Override
    protected String getGazetteerName() {
        return "biofid";
    }
}
