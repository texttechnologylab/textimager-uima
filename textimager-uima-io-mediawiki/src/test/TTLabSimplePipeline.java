import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.util.LifeCycleUtil;
import org.apache.uima.jcas.JCas;

import java.io.IOException;

public class TTLabSimplePipeline {

    public static void runPipeline(final JCas jCas, final AnalysisEngine... engines) throws UIMAException, IOException {

        for (AnalysisEngine engine : engines) {
            engine.process(jCas);
            LifeCycleUtil.destroy(engine);

        }
    }

}
