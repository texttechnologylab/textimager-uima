package org.hucompute.textimager.uima.util;


import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.junit.jupiter.api.Test;
import org.texttechnologylab.annotation.ocr.OCRToken;
import org.texttechnologylab.utilities.helper.FileUtils;

import java.io.File;
import java.io.IOException;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

/**
 * JCasCollectionReader to read a single article from multiple XML files.
 */
public class DubiaRemover extends JCasAnnotator_ImplBase {

    /**
     * Remove regular Tokens and
     */
    public static final String REMOVE_TOKEN = "remove_token";
    @ConfigurationParameter(name = REMOVE_TOKEN, mandatory = false, defaultValue = "false")
    protected boolean remove_token;

    /**
     * Remove nested Sentences
     */
    public static final String REMOVE_NESTED_SENTENCE = "remove_nested_sentence";
    @ConfigurationParameter(name = REMOVE_NESTED_SENTENCE, mandatory = false, defaultValue = "false")
    protected boolean remove_nested_sentence;


    /**
     * Remove nested Sentences
     */
    public static final String TRANSFER_TOKEN_TO_OCRTOKEN = "transfer_token_to_ocrtoken";
    @ConfigurationParameter(name = TRANSFER_TOKEN_TO_OCRTOKEN, mandatory = false, defaultValue = "false")
    protected boolean transfer_token_to_ocrtoken;




    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {


        if(DocumentMetaData.get(aJCas)!=null) {
            System.out.println("Remove Dubia from: " + DocumentMetaData.get(aJCas).getDocumentId());
        }
        else{
            if(aJCas.getDocumentText().length()>10){
                System.out.println("Remove Dubia from: "+aJCas.getDocumentText().substring(0, 10));
            }
            else{
                System.out.println("Remove Dubia from: "+aJCas.getDocumentText());
            }

        }


        if(remove_token){
            JCasUtil.select(aJCas, Token.class).forEach(t->{
                if(t.getType().getName().equalsIgnoreCase("de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token")){

                    if(transfer_token_to_ocrtoken){

                        JCasUtil.selectAt(aJCas, OCRToken.class, t.getBegin(), t.getEnd()).forEach(ocr->{
                            ocr.setLemma(t.getLemma());
                            ocr.setForm(t.getForm());
                            ocr.setPos(t.getPos());
                        });

                    }
                    else{
                        if(t.getLemma()!=null) {
                            t.getLemma().removeFromIndexes();
                        }
                        if(t.getPos()!=null){
                            t.getPos().removeFromIndexes();
                        }
                        if(t.getForm()!=null) {
                            t.getForm().removeFromIndexes();
                        }
                    }

                    t.setLemma(null);
                    t.setPos(null);
                    t.setForm(null);

                    t.removeFromIndexes();
                }
            });

        }

        if(remove_nested_sentence){
            JCasUtil.select(aJCas, Sentence.class).forEach(t->{

                JCasUtil.selectCovered(aJCas, Sentence.class, t).forEach(nestedSentence->{
                    if(!nestedSentence.equals(t)){
                        nestedSentence.removeFromIndexes();
                    }
                });

            });

        }

                JCasUtil.select(aJCas, Annotation.class).stream()
                    .filter(a -> {
                        return !a.getType().toString().equalsIgnoreCase(Annotation.class.getTypeName());
                    })
                    .filter(a->{
                        return a.getEnd()>0;
                    })
                    .forEach(a->{

                        JCasUtil.selectAt(aJCas, a.getClass(), a.getBegin(), a.getEnd()).stream().filter(b->!a.equals(b)).forEach(b->{
                            boolean remove = false;
                            if(a.getType().equals(b.getType())){

                                if(a instanceof Lemma){
                                    if(((Lemma)a).getValue().equalsIgnoreCase(((Lemma)b).getValue())){
                                        remove = true;
                                    }
                                }
                                else{
                                    remove = true;
                                }

                            }

                            if(remove){
                                if(a.getAddress()>b.getAddress()){
                                    a.removeFromIndexes();
                                }
                                else{
                                    b.removeFromIndexes();
                                }
                            }

                        });

                    });






    }


    @Test
    public void test() throws UIMAException, IOException {


        AnalysisEngineDescription dubia = createEngineDescription(DubiaRemover.class, REMOVE_TOKEN, true, REMOVE_NESTED_SENTENCE, true, TRANSFER_TOKEN_TO_OCRTOKEN, true);

        File nFile = new File("/home/gabrami/Downloads/4450070.xml.xmi");

        JCas test = JCasFactory.createJCas();
        CasIOUtil.readXmi(test, nFile);

        SimplePipeline.runPipeline(test, dubia);


        JCasUtil.selectAll(test).forEach(a->{
            System.out.println(a);
        });

        String stringToSave = XmlFormatter.getPrettyString(test);

        FileUtils.writeContent(stringToSave, new File("/home/gabrami/Downloads/re_4450070.xml.xmi"));

        //CasIOUtil.writeXmi(test, new File("/home/gabrami/Downloads/re_4450070.xml.xmi"));

    }
}
