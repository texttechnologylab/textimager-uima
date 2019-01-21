

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
public class Head extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Head.class);
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
  protected Head() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Head(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Head(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Head(JCas jcas, int begin, int end) {
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
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_typ == null)
      jcasType.jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.segmentation.Head");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Head_Type)jcasType).casFeatCode_typ);}
    
  /** setter for typ - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTyp(String v) {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_typ == null)
      jcasType.jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.segmentation.Head");
    jcasType.ll_cas.ll_setStringValue(addr, ((Head_Type)jcasType).casFeatCode_typ, v);}    
   
    
  //*--------------*
  //* Feature: id

  /** getter for id - gets 
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.segmentation.Head");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Head_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.segmentation.Head");
    jcasType.ll_cas.ll_setStringValue(addr, ((Head_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: parent

  /** getter for parent - gets 
   * @generated
   * @return value of the feature 
   */
  public String getParent() {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "org.hucompute.textimager.uima.type.segmentation.Head");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Head_Type)jcasType).casFeatCode_parent);}
    
  /** setter for parent - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setParent(String v) {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_parent == null)
      jcasType.jcas.throwFeatMissing("parent", "org.hucompute.textimager.uima.type.segmentation.Head");
    jcasType.ll_cas.ll_setStringValue(addr, ((Head_Type)jcasType).casFeatCode_parent, v);}    
   
    
  //*--------------*
  //* Feature: rootEntries

  /** getter for rootEntries - gets 
   * @generated
   * @return value of the feature 
   */
  public String getRootEntries() {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_rootEntries == null)
      jcasType.jcas.throwFeatMissing("rootEntries", "org.hucompute.textimager.uima.type.segmentation.Head");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Head_Type)jcasType).casFeatCode_rootEntries);}
    
  /** setter for rootEntries - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRootEntries(String v) {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_rootEntries == null)
      jcasType.jcas.throwFeatMissing("rootEntries", "org.hucompute.textimager.uima.type.segmentation.Head");
    jcasType.ll_cas.ll_setStringValue(addr, ((Head_Type)jcasType).casFeatCode_rootEntries, v);}    
   
    
  //*--------------*
  //* Feature: children

  /** getter for children - gets 
   * @generated
   * @return value of the feature 
   */
  public String getChildren() {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_children == null)
      jcasType.jcas.throwFeatMissing("children", "org.hucompute.textimager.uima.type.segmentation.Head");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Head_Type)jcasType).casFeatCode_children);}
    
  /** setter for children - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setChildren(String v) {
    if (Head_Type.featOkTst && ((Head_Type)jcasType).casFeat_children == null)
      jcasType.jcas.throwFeatMissing("children", "org.hucompute.textimager.uima.type.segmentation.Head");
    jcasType.ll_cas.ll_setStringValue(addr, ((Head_Type)jcasType).casFeatCode_children, v);}    
  }

    