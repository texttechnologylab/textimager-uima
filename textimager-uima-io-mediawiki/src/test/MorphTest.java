import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.morph.MorphologicalFeatures;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.CasIOUtils;
import org.dkpro.core.matetools.MateMorphTagger;
import org.hucompute.textimager.uima.io.mediawiki.MediawikiWriter;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.hucompute.textimager.uima.io.mediawiki.MediawikiWriter.PARAM_TARGET_LOCATION;

public class MorphTest {

    @Test
    public void testMediaWikiWriter() throws Exception {
        JCas cas = JCasFactory.createJCas();

        CasIOUtils.load(new File("/home/gabrami/Downloads/abc22.xmi").toURL(), cas.getCas());


        List<MorphologicalFeatures> mList = new ArrayList<>(0);

        JCasUtil.select(cas, MorphologicalFeatures.class).forEach(mf->{
            mList.add(mf);
        });

        mList.forEach(ml->{
            ml.removeFromIndexes();
        });

        JCasUtil.select(cas, MorphologicalFeatures.class).forEach(mf->{
            System.out.println(mf.toString());
        });



        AggregateBuilder builder = new AggregateBuilder();

        builder.add(AnalysisEngineFactory.createEngineDescription(MateMorphTagger.class));
        builder.add(AnalysisEngineFactory.createEngineDescription(MediawikiWriter.class, PARAM_TARGET_LOCATION, "/tmp/"));

        SimplePipeline.runPipeline(cas,builder.createAggregate());

        System.out.println("warten");
    }

}
