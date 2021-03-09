package org.hucompute.textimager.uima.gnd.gazetteer;

import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.SingleClassTreeGazetteer;
import org.json.JSONException;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;
import org.texttechnologylab.annotation.type.Person_HumanBeing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class GNDGazetteer extends SingleClassTreeGazetteer {
    @Override
    protected void addMyAnnotation(JCas aJCas, Annotation fromToken, Annotation toToken, Type type, HashSet<Object> objects) {
        // Prepare annotation, currently only "person"
        Person_HumanBeing person = (Person_HumanBeing) aJCas.getCas().createAnnotation(type, fromToken.getBegin(), toToken.getEnd());
        person.setValue("");

        // All additional anotation comments
        List<AnnotationComment> comments = new ArrayList<>();

        // Model/Annotator version
        AnnotationComment commentVersion = new AnnotationComment(aJCas);
        commentVersion.setReference(person);
        commentVersion.setKey("ttlab_annotator");
        commentVersion.setValue("ttlab_gnd_v_1.0.1");
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

                    // "fullname" is special, add to "person" annotation
                    if (key.equals("fullname")) {
                        person.setValue(value);
                    }
                    else {
                        // TODO empty values might be usefull?
                        if (!value.isEmpty()) {
                            // Add data to annotation
                            AnnotationComment comment = new AnnotationComment(aJCas);
                            comment.setReference(person);
                            comment.setKey(key);
                            comment.setValue(value);
                            comments.add(comment);
                        }
                    }
                });
            } catch (JSONException e) {
                // ignore errors, this is only extra metadata...
                e.printStackTrace();
            }
        }

        // Add all to cas
        aJCas.addFsToIndexes(person);
        for (AnnotationComment comment : comments) {
            aJCas.addFsToIndexes(comment);
        }

        // Additional comments from config
        addAdditionalComments(aJCas, person);
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
