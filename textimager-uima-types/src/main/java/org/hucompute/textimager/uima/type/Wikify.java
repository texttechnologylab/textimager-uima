

/* First created by JCasGen Mon Jan 21 11:55:39 CET 2019 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Jan 21 11:55:42 CET 2019
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/WikifyAnnotation.xml
 * @generated */
public class Wikify extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Wikify.class);
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
  protected Wikify() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Wikify(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Wikify(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Wikify(JCas jcas, int begin, int end) {
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
  //* Feature: title

  /** getter for title - gets start and end of the objects
   * @generated
   * @return value of the feature 
   */
  public String getTitle() {
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.hucompute.textimager.uima.type.Wikify");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Wikify_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets start and end of the objects 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTitle(String v) {
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.hucompute.textimager.uima.type.Wikify");
    jcasType.ll_cas.ll_setStringValue(addr, ((Wikify_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: link

  /** getter for link - gets start and end of the objects
   * @generated
   * @return value of the feature 
   */
  public String getLink() {
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_link == null)
      jcasType.jcas.throwFeatMissing("link", "org.hucompute.textimager.uima.type.Wikify");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Wikify_Type)jcasType).casFeatCode_link);}
    
  /** setter for link - sets start and end of the objects 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLink(String v) {
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_link == null)
      jcasType.jcas.throwFeatMissing("link", "org.hucompute.textimager.uima.type.Wikify");
    jcasType.ll_cas.ll_setStringValue(addr, ((Wikify_Type)jcasType).casFeatCode_link, v);}    
   
    
  //*--------------*
  //* Feature: elements

  /** getter for elements - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getElements() {
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_elements == null)
      jcasType.jcas.throwFeatMissing("elements", "org.hucompute.textimager.uima.type.Wikify");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Wikify_Type)jcasType).casFeatCode_elements)));}
    
  /** setter for elements - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setElements(FSArray v) {
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_elements == null)
      jcasType.jcas.throwFeatMissing("elements", "org.hucompute.textimager.uima.type.Wikify");
    jcasType.ll_cas.ll_setRefValue(addr, ((Wikify_Type)jcasType).casFeatCode_elements, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for elements - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public Annotation getElements(int i) {
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_elements == null)
      jcasType.jcas.throwFeatMissing("elements", "org.hucompute.textimager.uima.type.Wikify");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Wikify_Type)jcasType).casFeatCode_elements), i);
    return (Annotation)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Wikify_Type)jcasType).casFeatCode_elements), i)));}

  /** indexed setter for elements - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setElements(int i, Annotation v) { 
    if (Wikify_Type.featOkTst && ((Wikify_Type)jcasType).casFeat_elements == null)
      jcasType.jcas.throwFeatMissing("elements", "org.hucompute.textimager.uima.type.Wikify");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((Wikify_Type)jcasType).casFeatCode_elements), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((Wikify_Type)jcasType).casFeatCode_elements), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    