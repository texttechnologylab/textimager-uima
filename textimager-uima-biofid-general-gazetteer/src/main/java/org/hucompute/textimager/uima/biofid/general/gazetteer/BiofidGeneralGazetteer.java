package org.hucompute.textimager.uima.biofid.general.gazetteer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.uima.cas.Type;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.hucompute.textimager.uima.gazetteer.MultiClassTreeGazetteer;
import org.texttechnologylab.annotation.AnnotationComment;
import org.texttechnologylab.annotation.NamedEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BiofidGeneralGazetteer extends MultiClassTreeGazetteer {
    protected static final ObjectMapper mapper = new ObjectMapper();

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
                Map<String, String> data = mapper.readValue(json, new TypeReference<Map<String, String>>(){});
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    // TODO empty values might be usefull?
                    if (!entry.getValue().isEmpty()) {
                        // Add data to annotation
                        AnnotationComment comment = new AnnotationComment(aJCas);
                        comment.setReference(ne);
                        comment.setKey(entry.getKey());
                        comment.setValue(entry.getValue());
                        comments.add(comment);
                    }
                }
            } catch (IOException e) {
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
