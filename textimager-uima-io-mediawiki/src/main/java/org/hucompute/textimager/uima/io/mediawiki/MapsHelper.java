package org.hucompute.textimager.uima.io.mediawiki;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MapsHelper {
    Set<String> locations;
    public MapsHelper(JCas cas){
        locations = new HashSet<String>();
        for (NamedEntity ne : JCasUtil.select(cas, NamedEntity.class)) {
            if (ne.getValue().equals("LOC")) {
                String text = ne.getCoveredText();
                locations.add(text);
            }
        }
    }

    public String buildMapsParserString(){
        StringBuilder str = new StringBuilder();
        str.append("\n== Recognized Locations ==\n");
        if(locations.isEmpty() == true){
            return "[No locations found in present text]";

        }else{
            str.append("{{#display_map:");
            Iterator<String> it = locations.iterator();
            while(it.hasNext()) {
                str.append(it.next());
                str.append(";");
            }
            str.append("}}");
            return str.toString();
        }
    }
}

