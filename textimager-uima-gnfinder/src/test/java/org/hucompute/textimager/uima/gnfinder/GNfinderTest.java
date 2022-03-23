package org.hucompute.textimager.uima.gnfinder;

import de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.texttechnologylab.utilities.helper.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class GNfinderTest {
    @Test
    public void gnfinderTest() throws UIMAException, IOException {
        //JCas cas = JCasFactory.createText("Pomatomus saltator und Parus major im Beispiel.", "de");

        String sContent = FileUtils.getContentFromFile(new File("/home/gabrami/Projects/GitHub/textimager-uima/textimager-uima-gnfinder/src/main/resources/132824.txt"));
        JCas cas = JCasFactory.createText(sContent);

        AnalysisEngineDescription gnFinder = createEngineDescription(GNfinder.class,
                GNfinder.PARAM_ONLY_VERIFICATION, false
        );

        SimplePipeline.runPipeline(cas, gnFinder);

//        System.out.println(XmlFormatter.getPrettyString(cas));

        for (NamedEntity ne : JCasUtil.select(cas, NamedEntity.class)) {
            System.out.println("!" + ne.getCoveredText() + "!");
        }
    }
}

