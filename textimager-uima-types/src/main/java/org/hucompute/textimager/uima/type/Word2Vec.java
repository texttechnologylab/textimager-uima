/* First created by JCasGen Fri Jun 24 17:58:49 CEST 2016 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.DoubleArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Jul 05 10:11:41 CEST 2016
 * XML source: /home/ahemati/workspace/services/services-types/src/main/resources/desc/type/Word2Vec.xml
 * @generated */
public class Word2Vec extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Word2Vec.class);
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
  protected Word2Vec() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Word2Vec(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Word2Vec(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Word2Vec(JCas jcas, int begin, int end) {
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
    if (Word2Vec_Type.featOkTst && ((Word2Vec_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.Word2Vec");
    return (DoubleArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_embedding)));}
    
  /** setter for embedding - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEmbedding(DoubleArray v) {
    if (Word2Vec_Type.featOkTst && ((Word2Vec_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.Word2Vec");
    jcasType.ll_cas.ll_setRefValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_embedding, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for embedding - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public double getEmbedding(int i) {
    if (Word2Vec_Type.featOkTst && ((Word2Vec_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.Word2Vec");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_embedding), i);
    return jcasType.ll_cas.ll_getDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_embedding), i);}

  /** indexed setter for embedding - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setEmbedding(int i, double v) { 
    if (Word2Vec_Type.featOkTst && ((Word2Vec_Type)jcasType).casFeat_embedding == null)
      jcasType.jcas.throwFeatMissing("embedding", "org.hucompute.services.type.Word2Vec");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_embedding), i);
    jcasType.ll_cas.ll_setDoubleArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_embedding), i, v);}
   
    
  //*--------------*
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (Word2Vec_Type.featOkTst && ((Word2Vec_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.hucompute.services.type.Word2Vec");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (Word2Vec_Type.featOkTst && ((Word2Vec_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.hucompute.services.type.Word2Vec");
    jcasType.ll_cas.ll_setStringValue(addr, ((Word2Vec_Type)jcasType).casFeatCode_value, v);}    
  }

    