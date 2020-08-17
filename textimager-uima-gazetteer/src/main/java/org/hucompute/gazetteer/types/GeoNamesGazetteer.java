package org.hucompute.gazetteer.types;

import com.google.common.collect.Lists;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.gazetteer.Gazetteer;
import org.hucompute.gazetteer.Models.KeyValueObject;
import org.texttechnologylab.annotation.GeoNamesEntity;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class GeoNamesGazetteer extends Gazetteer {

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        atomicTaxonMatchCount.set(0);
        ArrayList<Token> tokens = Lists.newArrayList(JCasUtil.select(aJCas, Token.class).stream().filter(t->{
            if(t.getPos()!=null){
                return t.getPosValue().equalsIgnoreCase("NE");
            }
            return false;
        }).collect(Collectors.toList()));

        super.objects.getObjects().parallelStream().forEach(el->{
            tagAllMatches(aJCas, tokens, el);
        });
    }

    @Override
    public void tagAllMatches(JCas aJCas, ArrayList<Token> tokens, KeyValueObject pObject) {

        final Set<Token> found = tokens.stream().filter(t-> {
            String sTest = t.getCoveredText();
            sTest = pUseLowercase ? sTest.toLowerCase(Locale.forLanguageTag(language)) : sTest;

            return sTest.equalsIgnoreCase((String) pObject.getValue("Name"));
        }).collect(Collectors.toSet());


        found.forEach(s->{

            GeoNamesEntity geoNamesEntity = new GeoNamesEntity(aJCas);
            geoNamesEntity.setBegin(s.getBegin());
            geoNamesEntity.setEnd(s.getEnd());
            geoNamesEntity.setMainclass(((String) pObject.getValue("MainClass")).replace("https://www.geonames.org/ontology#", ""));
            geoNamesEntity.setSubclass(((String) pObject.getValue("SubClass")).replace("https://www.geonames.org/ontology#", ""));

            String sID = (String) pObject.getValue("Uri");
            sID = sID.replace("<", "");
            sID = sID.replace(">", "");
            sID = sID.replaceAll("https://sws.geonames.org/", "");
            sID = sID.replaceAll("/", "");
            geoNamesEntity.setId(Integer.valueOf(sID));
            geoNamesEntity.addToIndexes();

        });

    }
}
