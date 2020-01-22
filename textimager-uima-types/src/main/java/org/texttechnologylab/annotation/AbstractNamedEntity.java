

/* First created by JCasGen Wed Jan 22 14:27:43 CET 2020 */
package org.texttechnologylab.annotation;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Jan 22 14:27:43 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/TextTechnologyTypes.xml
 * @generated */
public class AbstractNamedEntity extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AbstractNamedEntity.class);
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
  protected AbstractNamedEntity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public AbstractNamedEntity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AbstractNamedEntity(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public AbstractNamedEntity(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets 
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "org.texttechnologylab.annotation.AbstractNamedEntity");
    jcasType.ll_cas.ll_setStringValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: specific

  /** getter for specific - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getSpecific() {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_specific == null)
      jcasType.jcas.throwFeatMissing("specific", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_specific);}
    
  /** setter for specific - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpecific(boolean v) {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_specific == null)
      jcasType.jcas.throwFeatMissing("specific", "org.texttechnologylab.annotation.AbstractNamedEntity");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_specific, v);}    
   
    
  //*--------------*
  //* Feature: metaphor

  /** getter for metaphor - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getMetaphor() {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_metaphor == null)
      jcasType.jcas.throwFeatMissing("metaphor", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_metaphor);}
    
  /** setter for metaphor - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMetaphor(boolean v) {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_metaphor == null)
      jcasType.jcas.throwFeatMissing("metaphor", "org.texttechnologylab.annotation.AbstractNamedEntity");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_metaphor, v);}    
   
    
  //*--------------*
  //* Feature: metonym

  /** getter for metonym - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getMetonym() {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_metonym == null)
      jcasType.jcas.throwFeatMissing("metonym", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_metonym);}
    
  /** setter for metonym - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMetonym(boolean v) {
    if (AbstractNamedEntity_Type.featOkTst && ((AbstractNamedEntity_Type)jcasType).casFeat_metonym == null)
      jcasType.jcas.throwFeatMissing("metonym", "org.texttechnologylab.annotation.AbstractNamedEntity");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((AbstractNamedEntity_Type)jcasType).casFeatCode_metonym, v);}    
  }

    