

/* First created by JCasGen Tue Jul 28 16:22:15 CEST 2020 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Sep 18 10:19:08 CEST 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/Sentiment.xml
 * @generated */
public class Sentiment extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Sentiment.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected Sentiment() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Sentiment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Sentiment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Sentiment(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: sentiment

  /** getter for sentiment - gets 
   * @generated
   * @return value of the feature 
   */
  public double getSentiment() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_sentiment == null)
      jcasType.jcas.throwFeatMissing("sentiment", "org.hucompute.textimager.uima.type.Sentiment");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Sentiment_Type)jcasType).casFeatCode_sentiment);}
    
  /** setter for sentiment - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSentiment(double v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_sentiment == null)
      jcasType.jcas.throwFeatMissing("sentiment", "org.hucompute.textimager.uima.type.Sentiment");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Sentiment_Type)jcasType).casFeatCode_sentiment, v);}    
   
    
  //*--------------*
  //* Feature: subjectivity

  /** getter for subjectivity - gets 
   * @generated
   * @return value of the feature 
   */
  public double getSubjectivity() {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_subjectivity == null)
      jcasType.jcas.throwFeatMissing("subjectivity", "org.hucompute.textimager.uima.type.Sentiment");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((Sentiment_Type)jcasType).casFeatCode_subjectivity);}
    
  /** setter for subjectivity - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubjectivity(double v) {
    if (Sentiment_Type.featOkTst && ((Sentiment_Type)jcasType).casFeat_subjectivity == null)
      jcasType.jcas.throwFeatMissing("subjectivity", "org.hucompute.textimager.uima.type.Sentiment");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((Sentiment_Type)jcasType).casFeatCode_subjectivity, v);}    
  }

    