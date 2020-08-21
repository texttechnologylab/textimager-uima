

/* First created by JCasGen Mon Apr 08 12:26:54 CEST 2019 */
package org.hucompute.wikidragon.core.nlp.annotation;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Apr 08 12:26:54 CEST 2019
 * XML source: C:/Users/gleim/projects/WikiDragonUIMATypeSystem/desc/HtmlTagTypeSystemDescriptor.xml
 * @generated */
public class HtmlTag extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(HtmlTag.class);
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
  protected HtmlTag() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public HtmlTag(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public HtmlTag(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public HtmlTag(JCas jcas, int begin, int end) {
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
  //* Feature: tag

  /** getter for tag - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTag() {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_tag == null)
      jcasType.jcas.throwFeatMissing("tag", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return jcasType.ll_cas.ll_getStringValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_tag);}
    
  /** setter for tag - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTag(String v) {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_tag == null)
      jcasType.jcas.throwFeatMissing("tag", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    jcasType.ll_cas.ll_setStringValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_tag, v);}    
   
    
  //*--------------*
  //* Feature: attr

  /** getter for attr - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAttr() {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_attr == null)
      jcasType.jcas.throwFeatMissing("attr", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return jcasType.ll_cas.ll_getStringValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_attr);}
    
  /** setter for attr - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAttr(String v) {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_attr == null)
      jcasType.jcas.throwFeatMissing("attr", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    jcasType.ll_cas.ll_setStringValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_attr, v);}    
   
    
  //*--------------*
  //* Feature: depth

  /** getter for depth - gets 
   * @generated
   * @return value of the feature 
   */
  public int getDepth() {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_depth == null)
      jcasType.jcas.throwFeatMissing("depth", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return jcasType.ll_cas.ll_getIntValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_depth);}
    
  /** setter for depth - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDepth(int v) {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_depth == null)
      jcasType.jcas.throwFeatMissing("depth", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    jcasType.ll_cas.ll_setIntValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_depth, v);}    
   
    
  //*--------------*
  //* Feature: order

  /** getter for order - gets 
   * @generated
   * @return value of the feature 
   */
  public int getOrder() {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_order == null)
      jcasType.jcas.throwFeatMissing("order", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return jcasType.ll_cas.ll_getIntValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_order);}
    
  /** setter for order - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setOrder(int v) {
    if (HtmlTag_Type.featOkTst && ((HtmlTag_Type)jcasType).casFeat_order == null)
      jcasType.jcas.throwFeatMissing("order", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    jcasType.ll_cas.ll_setIntValue(addr, ((HtmlTag_Type)jcasType).casFeatCode_order, v);}    
  }

    