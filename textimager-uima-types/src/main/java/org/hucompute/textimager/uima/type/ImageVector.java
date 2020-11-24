/* First created by JCasGen Fri Jul 01 16:48:00 CEST 2016 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.tcas.Annotation;


import org.apache.uima.jcas.cas.AnnotationBase;


/** 
 * Updated by JCasGen Tue Jul 05 12:10:20 CEST 2016
 * XML source: /home/uslu/workspace/services/services-types/src/main/resources/desc/type/ImageVector.xml
 * @generated */
public class ImageVector extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ImageVector.class);
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
  protected ImageVector() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public ImageVector(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public ImageVector(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public ImageVector(JCas jcas, int begin, int end) {
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
  //* Feature: embedding

  /** getter for embedding - gets 
   * @generated
   * @return value of the feature 
   */
  public DoubleArray getEmbedding() {
    if (ImageVector_Type.featOkTst && ((ImageVector_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.ImageVector");
    return (DoubleArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((ImageVector_Type)jcasType).casFeatCode_embedding)));}
    
  /** setter for embedding - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEmbedding(DoubleArray v) {
    if (ImageVector_Type.featOkTst && ((ImageVector_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.ImageVector");
    jcasType.ll_cas.ll_setRefValue(addr, ((ImageVector_Type)jcasType).casFeatCode_embedding, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for embedding - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public double getEmbedding(int i) {
    if (ImageVector_Type.featOkTst && ((ImageVector_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.ImageVector");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ImageVector_Type)jcasType).casFeatCode_embedding), i);
    return jcasType.ll_cas.ll_getDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ImageVector_Type)jcasType).casFeatCode_embedding), i);}

  /** indexed setter for embedding - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setEmbedding(int i, double v) { 
    if (ImageVector_Type.featOkTst && ((ImageVector_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.ImageVector");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((ImageVector_Type)jcasType).casFeatCode_embedding), i);
    jcasType.ll_cas.ll_setDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((ImageVector_Type)jcasType).casFeatCode_embedding), i, v);}
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (ImageVector_Type.featOkTst && ((ImageVector_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.hucompute.services.type.ImageVector");
    return jcasType.ll_cas.ll_getStringValue(addr, ((ImageVector_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (ImageVector_Type.featOkTst && ((ImageVector_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.hucompute.services.type.ImageVector");
    jcasType.ll_cas.ll_setStringValue(addr, ((ImageVector_Type)jcasType).casFeatCode_value, v);}    
  }

    