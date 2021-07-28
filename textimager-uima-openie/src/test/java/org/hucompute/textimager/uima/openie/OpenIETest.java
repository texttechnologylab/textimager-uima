package org.hucompute.textimager.uima.openie;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;
import static org.dkpro.core.testing.AssertAnnotations.assertPOS;
import static org.dkpro.core.testing.AssertAnnotations.assertLemma;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;

import org.hucompute.textimager.uima.util.XmlFormatter;
import org.hucompute.textimager.uima.openie.OpenIEParser;

import de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Lemma;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class OpenIETest {

   @Test
   public void simpleExample() throws UIMAException{
      JCas cas = JCasFactory.createText("This is a good test. This is another test.", "en");

      new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

      AggregateBuilder builder = new AggregateBuilder();
      builder.add(createEngineDescription(
         OpenIEParser.class,
         OpenIEParser.PARAM_MODEL_LOCATION, "http://cistern.cis.lmu.de/marmot/models/CURRENT/spmrl/de.marmot"
      ));

      SimplePipeline.runPipeline(cas, builder.createAggregate());
      
      System.out.println(XmlFormatter.getPrettyString(cas.getCas()));
      // assertLemma(new String[]{"der","sein","ein","gut","Test","--"}, JCasUtil.select(cas, Lemma.class));
   }

}
