package org.hucompute.textimager.uima.biofid.general.gazetteer;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.MultiClassTreeGazetteer;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class BiofidGeneralGazetteer extends MultiClassTreeGazetteer {
    @Override
    protected void addMyAnnotation(JCas aJCas, Annotation fromToken, Annotation toToken, Type type, HashSet<Object> objects) {
        NamedEntity ne = (NamedEntity) aJCas.getCas().createAnnotation(type, fromToken.getBegin(), toToken.getEnd());

        // All additional anotation comments
        List<AnnotationComment> comments = new ArrayList<>();

        // Annotator version
        AnnotationComment commentVersion = new AnnotationComment(aJCas);
        commentVersion.setReference(ne);
        commentVersion.setKey("ttlab_annotator");
        commentVersion.setValue("ttlab_biofid_general_v_1.0.1");
        comments.add(commentVersion);

        // Get data as json strings for this annotation
        List<String> jsons = objects.stream()
                .map(Object::toString)
                .filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.toList());

        // Get data from json, always a map of string -> string
        for (String json : jsons) {
            try {
                JSONObject data = new JSONObject(json);
                data.keys().forEachRemaining(key -> {
                    String value = data.getString(key);

                    // TODO empty values might be usefull?
                    if (!value.isEmpty()) {
                        // Add data to annotation
                        AnnotationComment comment = new AnnotationComment(aJCas);
                        comment.setReference(ne);
                        comment.setKey(key);
                        comment.setValue(value);
                        comments.add(comment);
                    }
                });
            } catch (JSONException e) {
                // ignore errors, this is only extra metadata...
                e.printStackTrace();
            }
        }

        // Add all to cas
        aJCas.addFsToIndexes(ne);
        for (AnnotationComment comment : comments) {
            aJCas.addFsToIndexes(comment);
        }

        // Additional comments from config
        addAdditionalComments(aJCas, ne);
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
