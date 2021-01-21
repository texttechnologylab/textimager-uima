import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.languagetool.LanguageToolSegmenter;
import de.unihd.dbs.uima.annotator.heideltime2.HeidelTime;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.texttechnologylab.utilities.helper.FileUtils;
import textimager.uima.io.abby.TTLabBioFIDPreProcessing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static textimager.uima.io.abby.TTLabBioFIDPreProcessing.compressGZ;

public class AddMetaData {

    public static void main(String[] args) throws IOException {

        AnalysisEngineDescription languageTool = null;
        try {
            languageTool = createEngineDescription(LanguageToolSegmenter.class);

        } catch (ResourceInitializationException e) {
            e.printStackTrace();
        }

        String sPath = args[0];
        String sTemp = args[1];

        AnalysisEngineDescription finalLanguageTool = languageTool;
        FileUtils.getFiles(sPath, ".xml.gz").stream()
//                        .filter(f -> !outSet.contains(f.getName()))
                //                .filter(f->f.getName().equals("90532.gz"))
                .forEach(f -> {
                    try {
                        System.out.println("Processing: " + f.getName());

                            File nFile = new File(sTemp + f.getName().replace(".xml", "") + ".xml");
                            if(nFile.exists()){
                                nFile.delete();
                            }

                            TTLabBioFIDPreProcessing.decompressGZ(Paths.get(f.getAbsolutePath()), Paths.get(nFile.getAbsolutePath()));

                            JCas pCas = JCasFactory.createJCas();

                            CasIOUtil.readXmi(pCas, nFile);

                            DocumentMetaData dmd = null;
                            if(JCasUtil.select(pCas, DocumentMetaData.class).size()==0) {
                                dmd = DocumentMetaData.create(pCas);
                            }
                            else {
                                dmd = DocumentMetaData.get(pCas);
                            }

                            dmd.setDocumentTitle(f.getName());
                            dmd.setDocumentId(f.getName());
                            dmd.setLanguage("de");


//                        AnalysisEngineDescription nerHucompute = createEngineDescription(HUComputeNER.class);
//                        AnalysisEngineDescription heideltime = createEngineDescription(HeidelTime.class);

                        JCasUtil.select(pCas, Sentence.class).forEach(s->{
                            s.removeFromIndexes();
                        });

                        SimplePipeline.runPipeline(pCas, finalLanguageTool);

//                        System.out.println("Sentences: "+JCasUtil.select(pCas, Sentence.class).size());

                        CasIOUtil.writeXmi(pCas, nFile);

                        compressGZ(Paths.get(nFile.getAbsolutePath()), Paths.get(f.getAbsolutePath()));

                        nFile.delete();

                    } catch (UIMAException | IOException e) {
                        e.printStackTrace();
                    }
                });


    }

}
