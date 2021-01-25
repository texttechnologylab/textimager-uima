package org.hucompute.textimager.uima.text2scene;

import jep.JepException;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.MappingProvider;
import org.texttechnologylab.annotation.semaf.isospace.SpatialEntity;
import org.texttechnologylab.annotation.semaf.meta.MetaLink;

import java.util.ArrayList;

public class BertProcessor extends BertBase {

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
        mappingProvider = new MappingProvider();
        mappingProvider.setDefault(MappingProvider.BASE_TYPE, SpatialEntity.class.getName());
        mappingProvider.setDefault(MappingProvider.LANGUAGE, "en");

        if (StringUtils.isNotBlank(language))
            mappingProvider.setOverride(MappingProvider.LANGUAGE, language);

        try {
            interpreter.exec("model = Bert()");
        } catch (JepException e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {

        try {
            ArrayList<ArrayList> result;
            result = (ArrayList<ArrayList>) interpreter.invoke("model.process_text",
                    jCas.getDocumentText(), 2);

            for (ArrayList entry : result) {
                String prefix = entry.get(0).toString();
                String word = entry.get(1).toString();

                SpatialEntity iso = new SpatialEntity(jCas);//new SpatialEntity(jCas.getView("type7:Token"), 0, 10);
                iso.setComment(word);
                iso.setBegin(((Long)entry.get(2)).intValue());
                iso.setEnd(((Long)entry.get(3)).intValue());

                MetaLink ml = new MetaLink(jCas);
                ml.setRel_type("MASK");
                ml.setComment(prefix);
                ml.setFigure(iso);
                ml.setTrigger(iso);

                jCas.addFsToIndexes(iso);
                jCas.addFsToIndexes(ml);
            }
        } catch (ClassCastException | JepException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

}