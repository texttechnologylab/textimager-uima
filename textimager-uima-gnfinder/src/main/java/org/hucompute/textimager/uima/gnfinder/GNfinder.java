package org.hucompute.textimager.uima.gnfinder;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.AnnotationComment;
import org.texttechnologylab.annotation.type.Taxon;
import org.texttechnologylab.utilities.helper.FileUtils;
import org.texttechnologylab.utilities.helper.TempFileHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class GNfinder extends JCasAnnotator_ImplBase {

    public static final String PARAM_ONLY_VERIFICATION = "bVerification";
    @ConfigurationParameter(name = PARAM_ONLY_VERIFICATION, mandatory = false, defaultValue = "true", description = "Use only found Taxa which are validated.")
    protected boolean bVerification;

    public static final String PARAM_BIN_GNFINDER = "pathBin";
    @ConfigurationParameter(name = PARAM_BIN_GNFINDER, mandatory = false, description = "Use only found Taxa which are validated.")
    protected String pathBin;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);
    }


    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

        File tf = null;

        try {
            tf = TempFileHandler.getTempFile("aaa", "bbb");

            FileUtils.writeContent(aJCas.getDocumentText(), tf);

            Process process = null;
            try {
                process = Runtime.getRuntime().exec(pathBin + " " + tf.getAbsolutePath() + " -v -f compact");

                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        process.getInputStream()));
                String s = null;
                StringBuilder sb = new StringBuilder();
                while (true) {
                    try {
                        if (!((s = reader.readLine()) != null)) break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sb.append(s);

                }

                JSONObject rObject = new JSONObject(sb.toString());

                JSONArray tArray = rObject.getJSONArray("names");

                for (int a = 0; a < tArray.length(); a++) {
                    JSONObject tObject = tArray.getJSONObject(a);

                    int iBegin = tObject.getInt("start");
                    int iEnd = tObject.getInt("end");

                    if (tObject.has("verification")) {

                        JSONObject verification = tObject.getJSONObject("verification");

                        if (verification.has("bestResult")) {

                            JSONObject bestResult = verification.getJSONObject("bestResult");

                            Taxon nTaxon = new Taxon(aJCas);
                            nTaxon.setBegin(iBegin);
                            nTaxon.setEnd(iEnd);
                            if (bestResult.has("outlink")) {
                                nTaxon.setIdentifier(bestResult.getString("outlink"));
                            }
                            if (bestResult.has("currentName")) {
                                nTaxon.setValue(bestResult.getString("currentName"));
                            }
                            nTaxon.addToIndexes();

                            AnnotationComment ac = new AnnotationComment(aJCas);
                            ac.setReference(nTaxon);
                            ac.setKey("gnfinder_verification");
                            ac.setValue(bestResult.toString());
                            ac.addToIndexes();
                        } else {

                            if (!bVerification) {

                                Taxon nTaxon = new Taxon(aJCas);
                                nTaxon.setBegin(iBegin);
                                nTaxon.setEnd(iEnd);
                                nTaxon.addToIndexes();
                                nTaxon.addToIndexes();

                                AnnotationComment ac = new AnnotationComment(aJCas);
                                ac.setReference(nTaxon);
                                ac.setKey("gnfinder_verification");
                                ac.setValue(verification.getString("curation"));
                                ac.addToIndexes();
                            }

                        }
                    } else {
                        System.out.println("stop");
                    }

                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tf != null) {
                tf.delete();
            }
        }


    }

}
