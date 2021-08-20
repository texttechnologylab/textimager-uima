package org.hucompute.textimager.uima.openie;

import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

import org.apache.uima.UIMAException;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import org.hucompute.textimager.uima.util.XmlFormatter;
import org.hucompute.textimager.uima.openie.OpenIEParser;
import org.hucompute.textimager.uima.type.OpenIERelation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;

public class OpenIETest {


   @Test
   public void simpleExample() throws UIMAException{
      JCas cas = JCasFactory.createText("The U.S. president Barack Obama gave his speech on Tuesday to thousands of people.", "en");

      new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

      AggregateBuilder builder = new AggregateBuilder();
      builder.add(createEngineDescription(
         OpenIEParser.class
      ));

      SimplePipeline.runPipeline(cas, builder.createAggregate());
      
      System.out.println(XmlFormatter.getPrettyString(cas.getCas()));

      ArrayList<OpenIERelation> relation = new ArrayList<>(JCasUtil.select(cas, OpenIERelation.class));

      assertEquals(relation.get(0).getBeginArg1(), 0);
      assertEquals(relation.get(0).getEndArg1(), 31);
      assertEquals(relation.get(0).getValueArg1(), "The U.S. president Barack Obama");
      assertEquals(relation.get(0).getBeginRel(), 32);
      assertEquals(relation.get(0).getEndRel(), 36);
      assertEquals(relation.get(0).getValueRel(), "gave");
      assertEquals(relation.get(0).getBeginArg2(), 59);
      assertEquals(relation.get(0).getEndArg2(), 81);
      assertEquals(relation.get(0).getValueArg2(), "to thousands of people");
      assertEquals(relation.get(0).getBegin(), 0);
      assertEquals(relation.get(0).getEnd(), 81);
      assertEquals(relation.get(0).getConfidence(), 0.9168198459177435, 0.001);
   }

   @Test
   public void multipleSentenceExample() throws UIMAException{
      JCas cas = JCasFactory.createText("15 people killed in a bomb attack. Taliban claims responsibilty.", "en");

      new Sentence(cas,0,cas.getDocumentText().length()).addToIndexes();

      AggregateBuilder builder = new AggregateBuilder();
      builder.add(createEngineDescription(
         OpenIEParser.class
      ));

      SimplePipeline.runPipeline(cas, builder.createAggregate());
      
      System.out.println(XmlFormatter.getPrettyString(cas.getCas()));

      ArrayList<OpenIERelation> relation = new ArrayList<>(JCasUtil.select(cas, OpenIERelation.class));

      assertEquals(relation.get(0).getBeginArg1(), 0);
      assertEquals(relation.get(0).getEndArg1(), 9);
      assertEquals(relation.get(0).getValueArg1(), "15 people");
      assertEquals(relation.get(0).getBeginRel(), 10);
      assertEquals(relation.get(0).getEndRel(), 16);
      assertEquals(relation.get(0).getValueRel(), "killed");
      assertEquals(relation.get(0).getBeginArg2(), 17);
      assertEquals(relation.get(0).getEndArg2(), 33);
      assertEquals(relation.get(0).getValueArg2(), "L:in a bomb attack");
      assertEquals(relation.get(0).getBegin(), 0);
      assertEquals(relation.get(0).getEnd(), 33);
      assertEquals(relation.get(0).getConfidence(), 0.9030401971054747, 0.001);

      assertEquals(relation.get(1).getBeginArg1(), 35);
      assertEquals(relation.get(1).getEndArg1(), 42);
      assertEquals(relation.get(1).getValueArg1(), "Taliban");
      assertEquals(relation.get(1).getBeginRel(), 43);
      assertEquals(relation.get(1).getEndRel(), 49);
      assertEquals(relation.get(1).getValueRel(), "claims");
      assertEquals(relation.get(1).getBeginArg2(), 50);
      assertEquals(relation.get(1).getEndArg2(), 63);
      assertEquals(relation.get(1).getValueArg2(), "responsibilty");
      assertEquals(relation.get(1).getBegin(), 35);
      assertEquals(relation.get(1).getEnd(), 63);
      assertEquals(relation.get(1).getConfidence(), 0.8016573080327372, 0.001);
   }

}
