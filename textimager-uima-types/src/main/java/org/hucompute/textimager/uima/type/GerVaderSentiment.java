

/* First created by JCasGen Mon Jul 12 09:58:08 CEST 2021 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;



/** 
 * Updated by JCasGen Mon Jul 12 10:10:18 CEST 2021
 * XML source: /home/daniel/data/hiwi/git/myyyvothrr/textimager-uima/textimager-uima-types/src/main/resources/desc/type/Sentiment.xml
 * @generated */
public class GerVaderSentiment extends Sentiment {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(GerVaderSentiment.class);
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
  protected GerVaderSentiment() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public GerVaderSentiment(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public GerVaderSentiment(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public GerVaderSentiment(JCas jcas, int begin, int end) {
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
  //* Feature: pos

  /** getter for pos - gets 
   * @generated
   * @return value of the feature 
   */
  public double getPos() {
    if (GerVaderSentiment_Type.featOkTst && ((GerVaderSentiment_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "org.hucompute.textimager.uima.type.GerVaderSentiment");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((GerVaderSentiment_Type)jcasType).casFeatCode_pos);}
    
  /** setter for pos - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPos(double v) {
    if (GerVaderSentiment_Type.featOkTst && ((GerVaderSentiment_Type)jcasType).casFeat_pos == null)
      jcasType.jcas.throwFeatMissing("pos", "org.hucompute.textimager.uima.type.GerVaderSentiment");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((GerVaderSentiment_Type)jcasType).casFeatCode_pos, v);}    
   
    
  //*--------------*
  //* Feature: neu

  /** getter for neu - gets 
   * @generated
   * @return value of the feature 
   */
  public double getNeu() {
    if (GerVaderSentiment_Type.featOkTst && ((GerVaderSentiment_Type)jcasType).casFeat_neu == null)
      jcasType.jcas.throwFeatMissing("neu", "org.hucompute.textimager.uima.type.GerVaderSentiment");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((GerVaderSentiment_Type)jcasType).casFeatCode_neu);}
    
  /** setter for neu - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNeu(double v) {
    if (GerVaderSentiment_Type.featOkTst && ((GerVaderSentiment_Type)jcasType).casFeat_neu == null)
      jcasType.jcas.throwFeatMissing("neu", "org.hucompute.textimager.uima.type.GerVaderSentiment");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((GerVaderSentiment_Type)jcasType).casFeatCode_neu, v);}    
   
    
  //*--------------*
  //* Feature: neg

  /** getter for neg - gets 
   * @generated
   * @return value of the feature 
   */
  public double getNeg() {
    if (GerVaderSentiment_Type.featOkTst && ((GerVaderSentiment_Type)jcasType).casFeat_neg == null)
      jcasType.jcas.throwFeatMissing("neg", "org.hucompute.textimager.uima.type.GerVaderSentiment");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((GerVaderSentiment_Type)jcasType).casFeatCode_neg);}
    
  /** setter for neg - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNeg(double v) {
    if (GerVaderSentiment_Type.featOkTst && ((GerVaderSentiment_Type)jcasType).casFeat_neg == null)
      jcasType.jcas.throwFeatMissing("neg", "org.hucompute.textimager.uima.type.GerVaderSentiment");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((GerVaderSentiment_Type)jcasType).casFeatCode_neg, v);}    
  }

    