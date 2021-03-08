package org.hucompute.textimager.uima.geonames.gazetteer;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.SingleClassTreeGazetteer;
import org.texttechnologylab.annotation.AnnotationComment;
import org.texttechnologylab.annotation.GeoNamesEntity;

import java.util.HashSet;
import java.util.stream.Collectors;

public class GeonamesGazetteer extends SingleClassTreeGazetteer {
    @Override
    protected void addMyAnnotation(JCas aJCas, Annotation fromToken, Annotation toToken, String element, HashSet<Object> objects) {
        getTaggingType(element).forEach(type -> {
            //GeoNamesEntity geoNames = new GeoNamesEntity(aJCas, fromToken.getBegin(), toToken.getEnd());
            GeoNamesEntity geoNames = (GeoNamesEntity) aJCas.getCas().createAnnotation(type, fromToken.getBegin(), toToken.getEnd());

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

            // Annotator version
            AnnotationComment commentVersion = new AnnotationComment(aJCas);
            commentVersion.setReference(geoNames);
            commentVersion.setKey("ttlab_annotator");
            commentVersion.setValue("ttlab_geonamesl_v_1.0.1");
            aJCas.addFsToIndexes(commentVersion);

            // Additional comments from config
            addAdditionalComments(aJCas, geoNames);
        });
    }

    @Override
    protected String getGazetteerName() {
        return "biofid";
    }
}
