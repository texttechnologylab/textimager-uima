

/* First created by JCasGen Mon Aug 02 09:48:13 CEST 2021 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Aug 02 09:48:13 CEST 2021
 * XML source: /home/daniel/data/hiwi/git/myyyvothrr/textimager-uima/textimager-uima-types/src/main/resources/desc/type/OpenIERelation.xml
 * @generated */
public class OpenIERelation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OpenIERelation.class);
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
  protected OpenIERelation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OpenIERelation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OpenIERelation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OpenIERelation(JCas jcas, int begin, int end) {
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
  //* Feature: confidence

  /** getter for confidence - gets 
   * @generated
   * @return value of the feature 
   */
  public double getConfidence() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getDoubleValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_confidence);}
    
  /** setter for confidence - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setConfidence(double v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_confidence == null)
      jcasType.jcas.throwFeatMissing("confidence", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setDoubleValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_confidence, v);}    
   
    
  //*--------------*
  //* Feature: beginArg1

  /** getter for beginArg1 - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBeginArg1() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_beginArg1 == null)
      jcasType.jcas.throwFeatMissing("beginArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_beginArg1);}
    
  /** setter for beginArg1 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBeginArg1(int v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_beginArg1 == null)
      jcasType.jcas.throwFeatMissing("beginArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_beginArg1, v);}    
   
    
  //*--------------*
  //* Feature: endArg1

  /** getter for endArg1 - gets 
   * @generated
   * @return value of the feature 
   */
  public int getEndArg1() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_endArg1 == null)
      jcasType.jcas.throwFeatMissing("endArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_endArg1);}
    
  /** setter for endArg1 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndArg1(int v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_endArg1 == null)
      jcasType.jcas.throwFeatMissing("endArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_endArg1, v);}    
   
    
  //*--------------*
  //* Feature: valueArg1

  /** getter for valueArg1 - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValueArg1() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_valueArg1 == null)
      jcasType.jcas.throwFeatMissing("valueArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_valueArg1);}
    
  /** setter for valueArg1 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValueArg1(String v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_valueArg1 == null)
      jcasType.jcas.throwFeatMissing("valueArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_valueArg1, v);}    
   
    
  //*--------------*
  //* Feature: beginRel

  /** getter for beginRel - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBeginRel() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_beginRel == null)
      jcasType.jcas.throwFeatMissing("beginRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_beginRel);}
    
  /** setter for beginRel - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBeginRel(int v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_beginRel == null)
      jcasType.jcas.throwFeatMissing("beginRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_beginRel, v);}    
   
    
  //*--------------*
  //* Feature: endRel

  /** getter for endRel - gets 
   * @generated
   * @return value of the feature 
   */
  public int getEndRel() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_endRel == null)
      jcasType.jcas.throwFeatMissing("endRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_endRel);}
    
  /** setter for endRel - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndRel(int v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_endRel == null)
      jcasType.jcas.throwFeatMissing("endRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_endRel, v);}    
   
    
  //*--------------*
  //* Feature: valueRel

  /** getter for valueRel - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValueRel() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_valueRel == null)
      jcasType.jcas.throwFeatMissing("valueRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_valueRel);}
    
  /** setter for valueRel - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValueRel(String v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_valueRel == null)
      jcasType.jcas.throwFeatMissing("valueRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_valueRel, v);}    
   
    
  //*--------------*
  //* Feature: beginArg2

  /** getter for beginArg2 - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBeginArg2() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_beginArg2 == null)
      jcasType.jcas.throwFeatMissing("beginArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_beginArg2);}
    
  /** setter for beginArg2 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBeginArg2(int v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_beginArg2 == null)
      jcasType.jcas.throwFeatMissing("beginArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_beginArg2, v);}    
   
    
  //*--------------*
  //* Feature: endArg2

  /** getter for endArg2 - gets 
   * @generated
   * @return value of the feature 
   */
  public int getEndArg2() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_endArg2 == null)
      jcasType.jcas.throwFeatMissing("endArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_endArg2);}
    
  /** setter for endArg2 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEndArg2(int v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_endArg2 == null)
      jcasType.jcas.throwFeatMissing("endArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setIntValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_endArg2, v);}    
   
    
  //*--------------*
  //* Feature: valueArg2

  /** getter for valueArg2 - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValueArg2() {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_valueArg2 == null)
      jcasType.jcas.throwFeatMissing("valueArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_valueArg2);}
    
  /** setter for valueArg2 - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValueArg2(String v) {
    if (OpenIERelation_Type.featOkTst && ((OpenIERelation_Type)jcasType).casFeat_valueArg2 == null)
      jcasType.jcas.throwFeatMissing("valueArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    jcasType.ll_cas.ll_setStringValue(addr, ((OpenIERelation_Type)jcasType).casFeatCode_valueArg2, v);}    
  }

    