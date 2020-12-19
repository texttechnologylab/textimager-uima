package org.hucompute.textimager.uima.text2scene;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import jep.JepException;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.AnnotationBase;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.resources.MappingProvider;
import org.texttechnologylab.annotation.NamedEntity;
import org.texttechnologylab.annotation.semaf.isospace.SpatialEntity;
import org.texttechnologylab.annotation.semaf.meta.MetaLink;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BertProcessor extends BertBase {

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
        mappingProvider = new MappingProvider();
        //mappingProvider.setDefault(MappingProvider.LOCATION, "classpath:/org/textimager/uima/flair/ner-default.map");
        mappingProvider.setDefault(MappingProvider.BASE_TYPE, SpatialEntity.class.getName());
        mappingProvider.setDefault(MappingProvider.LANGUAGE, "en");

        //if (StringUtils.isNotBlank(pMappingProviderLocation))
        //    mappingProvider.setOverride(MappingProvider.LOCATION, pMappingProviderLocation);
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
        mappingProvider.configure(jCas.getCas());

        /*Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);
        List<Integer> offsets = sentences.stream().map(Sentence::getBegin).collect(Collectors.toList());
        List<String> sentenceStrings = sentences.stream().map(Sentence::getCoveredText).collect(Collectors.toList());*/

        try {
            ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) interpreter.invoke("model.process_text",
                    jCas.getDocumentText(), 2);

            for (ArrayList<String> entry : result) {
                String prefix = entry.get(0);
                String word = entry.get(1);
                //int begin = Integer.parseInt(entry.get(1));
                //int end = Integer.parseInt(entry.get(2));

                //Type tagType = mappingProvider.getTagType(tagValue);
                SpatialEntity iso = new SpatialEntity(jCas);//new SpatialEntity(jCas.getView("type7:Token"), 0, 10);
                iso.setComment(word);
                //MetaLink mL = new MetaLink(null, iso, null, "MASK", "");
                MetaLink ml = new MetaLink(jCas);
                ml.setRel_type("MASK");
                ml.setComment(prefix);
                ml.setFigure(iso);
                ml.setTrigger(iso);

                //jCas.getCas().createAnnotation(mappingProvider.getTagType(), 0, 10);
                //AnnotationFS annotation = jCas.getCas().createAnnotation(tagType, begin, end);
                //annotation.setStringValue(tagType.getFeatureByBaseName("value"), tagValue);
                jCas.addFsToIndexes(iso);
                jCas.addFsToIndexes(ml);
            }
        } catch (JepException | ClassCastException e) {
            throw new AnalysisEngineProcessException(e);
        }
    }

}