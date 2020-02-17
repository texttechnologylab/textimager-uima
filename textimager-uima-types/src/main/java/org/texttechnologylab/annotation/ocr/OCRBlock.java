

/* First created by JCasGen Tue Feb 04 20:02:41 CET 2020 */
package org.texttechnologylab.annotation.ocr;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Feb 04 20:02:41 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/TextTechnologyOCR.xml
 * @generated */
public class OCRBlock extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OCRBlock.class);
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
  protected OCRBlock() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OCRBlock(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OCRBlock(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OCRBlock(JCas jcas, int begin, int end) {
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
  //* Feature: top

  /** getter for top - gets 
   * @generated
   * @return value of the feature 
   */
  public int getTop() {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_top == null)
      jcasType.jcas.throwFeatMissing("top", "org.texttechnologylab.annotation.ocr.OCRBlock");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_top);}
    
  /** setter for top - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTop(int v) {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_top == null)
      jcasType.jcas.throwFeatMissing("top", "org.texttechnologylab.annotation.ocr.OCRBlock");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_top, v);}    
   
    
  //*--------------*
  //* Feature: bottom

  /** getter for bottom - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBottom() {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_bottom == null)
      jcasType.jcas.throwFeatMissing("bottom", "org.texttechnologylab.annotation.ocr.OCRBlock");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_bottom);}
    
  /** setter for bottom - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBottom(int v) {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_bottom == null)
      jcasType.jcas.throwFeatMissing("bottom", "org.texttechnologylab.annotation.ocr.OCRBlock");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_bottom, v);}    
   
    
  //*--------------*
  //* Feature: left

  /** getter for left - gets 
   * @generated
   * @return value of the feature 
   */
  public int getLeft() {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_left == null)
      jcasType.jcas.throwFeatMissing("left", "org.texttechnologylab.annotation.ocr.OCRBlock");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_left);}
    
  /** setter for left - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLeft(int v) {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_left == null)
      jcasType.jcas.throwFeatMissing("left", "org.texttechnologylab.annotation.ocr.OCRBlock");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_left, v);}    
   
    
  //*--------------*
  //* Feature: right

  /** getter for right - gets 
   * @generated
   * @return value of the feature 
   */
  public int getRight() {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_right == null)
      jcasType.jcas.throwFeatMissing("right", "org.texttechnologylab.annotation.ocr.OCRBlock");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_right);}
    
  /** setter for right - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRight(int v) {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_right == null)
      jcasType.jcas.throwFeatMissing("right", "org.texttechnologylab.annotation.ocr.OCRBlock");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_right, v);}    
   
    
  //*--------------*
  //* Feature: blockType

  /** getter for blockType - gets 
   * @generated
   * @return value of the feature 
   */
  public String getBlockType() {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_blockType == null)
      jcasType.jcas.throwFeatMissing("blockType", "org.texttechnologylab.annotation.ocr.OCRBlock");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_blockType);}
    
  /** setter for blockType - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBlockType(String v) {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_blockType == null)
      jcasType.jcas.throwFeatMissing("blockType", "org.texttechnologylab.annotation.ocr.OCRBlock");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_blockType, v);}    
   
    
  //*--------------*
  //* Feature: blockName

  /** getter for blockName - gets 
   * @generated
   * @return value of the feature 
   */
  public String getBlockName() {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_blockName == null)
      jcasType.jcas.throwFeatMissing("blockName", "org.texttechnologylab.annotation.ocr.OCRBlock");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_blockName);}
    
  /** setter for blockName - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBlockName(String v) {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_blockName == null)
      jcasType.jcas.throwFeatMissing("blockName", "org.texttechnologylab.annotation.ocr.OCRBlock");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_blockName, v);}    
   
    
  //*--------------*
  //* Feature: valid

  /** getter for valid - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getValid() {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_valid == null)
      jcasType.jcas.throwFeatMissing("valid", "org.texttechnologylab.annotation.ocr.OCRBlock");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_valid);}
    
  /** setter for valid - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setValid(boolean v) {
    if (OCRBlock_Type.featOkTst && ((OCRBlock_Type)jcasType).casFeat_valid == null)
      jcasType.jcas.throwFeatMissing("valid", "org.texttechnologylab.annotation.ocr.OCRBlock");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRBlock_Type)jcasType).casFeatCode_valid, v);}    
  }

    