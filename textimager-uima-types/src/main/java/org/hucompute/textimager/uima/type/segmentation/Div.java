

/* First created by JCasGen Mon Jan 21 11:58:25 CET 2019 */
package org.hucompute.textimager.uima.type.segmentation;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Jan 21 11:58:25 CET 2019
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/Segmentation_custom.xml
 * @generated */
public class Div extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Div.class);
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
  protected Div() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Div(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Div(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Div(JCas jcas, int begin, int end) {
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
  //* Feature: typ

  /** getter for typ - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTyp() {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_typ == null)
      jcasType.jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.segmentation.Div");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Div_Type)jcasType).casFeatCode_typ);}
    
  /** setter for typ - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTyp(String v) {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_typ == null)
      jcasType.jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.segmentation.Div");
    jcasType.ll_cas.ll_setStringValue(addr, ((Div_Type)jcasType).casFeatCode_typ, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.segmentation.Div");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Div_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.segmentation.Div");
    jcasType.ll_cas.ll_setStringValue(addr, ((Div_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: section

  /** getter for section - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSection() {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_section == null)
      jcasType.jcas.throwFeatMissing("section", "org.hucompute.textimager.uima.type.segmentation.Div");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Div_Type)jcasType).casFeatCode_section);}
    
  /** setter for section - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSection(String v) {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_section == null)
      jcasType.jcas.throwFeatMissing("section", "org.hucompute.textimager.uima.type.segmentation.Div");
    jcasType.ll_cas.ll_setStringValue(addr, ((Div_Type)jcasType).casFeatCode_section, v);}    
   
    
  //*--------------*
  //* Feature: user

  /** getter for user - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUser() {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_user == null)
      jcasType.jcas.throwFeatMissing("user", "org.hucompute.textimager.uima.type.segmentation.Div");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Div_Type)jcasType).casFeatCode_user);}
    
  /** setter for user - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUser(String v) {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_user == null)
      jcasType.jcas.throwFeatMissing("user", "org.hucompute.textimager.uima.type.segmentation.Div");
    jcasType.ll_cas.ll_setStringValue(addr, ((Div_Type)jcasType).casFeatCode_user, v);}    
   
    
  //*--------------*
  //* Feature: timestamp

  /** getter for timestamp - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTimestamp() {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "org.hucompute.textimager.uima.type.segmentation.Div");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Div_Type)jcasType).casFeatCode_timestamp);}
    
  /** setter for timestamp - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTimestamp(String v) {
    if (Div_Type.featOkTst && ((Div_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "org.hucompute.textimager.uima.type.segmentation.Div");
    jcasType.ll_cas.ll_setStringValue(addr, ((Div_Type)jcasType).casFeatCode_timestamp, v);}    
  }

    