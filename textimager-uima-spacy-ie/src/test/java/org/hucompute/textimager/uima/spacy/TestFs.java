package org.hucompute.textimager.uima.spacy;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.util.CasIOUtil;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.texttechnologylab.annotation.administration.FinishAnnotation;

import java.io.File;
import java.io.IOException;

public class TestFs {

    public static void main(String[] args) throws UIMAException, IOException {

        // Erstellen einer leeren CAS
        JCas pCas = JCasFactory.createJCas();

        // Laden einer CAS aus dem Filesystem
//        CasIOUtil.readXmi(pCas.getCas(), new File(("/mnt/ssd/SRL/data/newRunOut/0433_9163849.xmi")));
        CasIOUtil.readXmi(pCas.getCas(), new File(("/mnt/ssd/SRL/data/biofid_new/ex/3673151.xmi")));


        // Iteration über alle Views
        pCas.getViewIterator().forEachRemaining(view->{
            if ((!view.getSofa().getSofaID().equals("uimadbid")) && (!view.getSofa().getSofaID().equals("IAA"))){
                System.out.println(view.getSofa().getSofaID());

                // Extraktion des Finish-Status
                JCasUtil.select(view, FinishAnnotation.class).forEach(a -> {
//                    System.out.println(a.getTool());
                    System.out.println(a.getType());
                });

            // Alle User-Annotierten Annotationen
//            JCasUtil.select(view, Fingerprint.class).stream().forEach(fp->{
                // Ausgabe der Referenz (was wurde annotiert)
//                System.out.println(fp.getReference().getType());
//            });
            JCasUtil.select(view, Annotation.class).stream().forEach(a->{
//                if (a.getType().getName().equals("de.unihd.dbs.uima.types.heideltime.Timex3")){
                  if (a.getType().getName().equals("org.texttechnologylab.annotation.GeoNamesEntity")){
                    System.out.println("    " + a.getType().getName() + " " + a.getBegin() + " " + a.getEnd());
                    }
                });

            }
        });



//        AggregateBuilder builder = new AggregateBuilder();
        // Hier kommt dein Werkzeug hinein.
//        builder.add(createEngineDescription(LanguageToolSegmenter.class));


        // Die Pipeline läuft....
//        SimplePipeline.runPipeline(pCas, builder.createAggregate());


        // Überprüfung ob deine Annotationen stimmen.
//        JCasUtil.select(pCas, Annotation.class).stream().forEach(a->{
//            System.out.println(a);
//        });



    }

}
