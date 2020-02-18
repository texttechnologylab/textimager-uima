

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
public class OCRLine extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OCRLine.class);
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
  protected OCRLine() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OCRLine(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OCRLine(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OCRLine(JCas jcas, int begin, int end) {
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
  //* Feature: baseline

  /** getter for baseline - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBaseline() {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_baseline == null)
      jcasType.jcas.throwFeatMissing("baseline", "org.texttechnologylab.annotation.ocr.OCRLine");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_baseline);}
    
  /** setter for baseline - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBaseline(int v) {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_baseline == null)
      jcasType.jcas.throwFeatMissing("baseline", "org.texttechnologylab.annotation.ocr.OCRLine");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_baseline, v);}    
   
    
  //*--------------*
  //* Feature: top

  /** getter for top - gets 
   * @generated
   * @return value of the feature 
   */
  public int getTop() {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_top == null)
      jcasType.jcas.throwFeatMissing("top", "org.texttechnologylab.annotation.ocr.OCRLine");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_top);}
    
  /** setter for top - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTop(int v) {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_top == null)
      jcasType.jcas.throwFeatMissing("top", "org.texttechnologylab.annotation.ocr.OCRLine");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_top, v);}    
   
    
  //*--------------*
  //* Feature: bottom

  /** getter for bottom - gets 
   * @generated
   * @return value of the feature 
   */
  public int getBottom() {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_bottom == null)
      jcasType.jcas.throwFeatMissing("bottom", "org.texttechnologylab.annotation.ocr.OCRLine");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_bottom);}
    
  /** setter for bottom - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBottom(int v) {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_bottom == null)
      jcasType.jcas.throwFeatMissing("bottom", "org.texttechnologylab.annotation.ocr.OCRLine");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_bottom, v);}    
   
    
  //*--------------*
  //* Feature: left

  /** getter for left - gets 
   * @generated
   * @return value of the feature 
   */
  public int getLeft() {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_left == null)
      jcasType.jcas.throwFeatMissing("left", "org.texttechnologylab.annotation.ocr.OCRLine");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_left);}
    
  /** setter for left - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLeft(int v) {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_left == null)
      jcasType.jcas.throwFeatMissing("left", "org.texttechnologylab.annotation.ocr.OCRLine");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_left, v);}    
   
    
  //*--------------*
  //* Feature: right

  /** getter for right - gets 
   * @generated
   * @return value of the feature 
   */
  public int getRight() {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_right == null)
      jcasType.jcas.throwFeatMissing("right", "org.texttechnologylab.annotation.ocr.OCRLine");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_right);}
    
  /** setter for right - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRight(int v) {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_right == null)
      jcasType.jcas.throwFeatMissing("right", "org.texttechnologylab.annotation.ocr.OCRLine");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRLine_Type)jcasType).casFeatCode_right, v);}    
   
    
  //*--------------*
  //* Feature: format

  /** getter for format - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFormat() {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_format == null)
      jcasType.jcas.throwFeatMissing("format", "org.texttechnologylab.annotation.ocr.OCRLine");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRLine_Type)jcasType).casFeatCode_format);}
    
  /** setter for format - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFormat(String v) {
    if (OCRLine_Type.featOkTst && ((OCRLine_Type)jcasType).casFeat_format == null)
      jcasType.jcas.throwFeatMissing("format", "org.texttechnologylab.annotation.ocr.OCRLine");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRLine_Type)jcasType).casFeatCode_format, v);}    
  }

    