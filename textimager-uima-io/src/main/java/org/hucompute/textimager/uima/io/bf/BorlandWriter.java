package org.hucompute.textimager.uima.io.bf;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class BorlandWriter extends JCasAnnotator_ImplBase {

        // File to save output
        public static final String PARAM_EXPORT_FILE = "exportFile";
        @ConfigurationParameter(name = PARAM_EXPORT_FILE)
        protected String exportFile;

        BufferedWriter newsWriter;

        @Override
        public void initialize(UimaContext aContext) throws ResourceInitializationException {
            super.initialize(aContext);

            try {
                newsWriter = new BufferedWriter(new FileWriter(exportFile));
                newsWriter.write("directed");
                newsWriter.newLine();
                newsWriter.write("SimilarityGraph");
                newsWriter.newLine();
                newsWriter.write("Vertex Attributes:");
                newsWriter.write("[Year¤Integer];");
                newsWriter.write("[Month¤Integer];");
                newsWriter.write("[Day¤Integer];");
                newsWriter.write("[Time¤String];");
                newsWriter.write("[Volume¤String]");
                newsWriter.write("[t2wTopicClassification¤IntegerDistribution];");
                newsWriter.write("[t2wRaumClassification¤IntegerDistribution];");
                newsWriter.write("[t2wZeitClassification¤IntegerDistribution];");
                newsWriter.write("[ddc2Classification¤IntegerDistribution];");
                newsWriter.newLine();
                newsWriter.write("Edge Attributes:");
                newsWriter.newLine();
                newsWriter.write("ProbabilityMassOfGraph: 0");
                newsWriter.newLine();
                newsWriter.write("Vertices:");
                newsWriter.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void collectionProcessComplete() throws AnalysisEngineProcessException {
            super.collectionProcessComplete();
            try {
                newsWriter.flush();
                newsWriter.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        @Override
        public void process(JCas jCas) throws AnalysisEngineProcessException {



        }

}
