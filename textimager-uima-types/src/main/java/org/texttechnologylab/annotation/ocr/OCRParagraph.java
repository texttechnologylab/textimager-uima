

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
public class OCRParagraph extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OCRParagraph.class);
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
  protected OCRParagraph() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OCRParagraph(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OCRParagraph(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OCRParagraph(JCas jcas, int begin, int end) {
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
  //* Feature: leftIndent

  /** getter for leftIndent - gets 
   * @generated
   * @return value of the feature 
   */
  public int getLeftIndent() {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_leftIndent == null)
      jcasType.jcas.throwFeatMissing("leftIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_leftIndent);}
    
  /** setter for leftIndent - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLeftIndent(int v) {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_leftIndent == null)
      jcasType.jcas.throwFeatMissing("leftIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_leftIndent, v);}    
   
    
  //*--------------*
  //* Feature: rightIndent

  /** getter for rightIndent - gets 
   * @generated
   * @return value of the feature 
   */
  public int getRightIndent() {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_rightIndent == null)
      jcasType.jcas.throwFeatMissing("rightIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_rightIndent);}
    
  /** setter for rightIndent - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRightIndent(int v) {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_rightIndent == null)
      jcasType.jcas.throwFeatMissing("rightIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_rightIndent, v);}    
   
    
  //*--------------*
  //* Feature: startIndent

  /** getter for startIndent - gets 
   * @generated
   * @return value of the feature 
   */
  public int getStartIndent() {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_startIndent == null)
      jcasType.jcas.throwFeatMissing("startIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_startIndent);}
    
  /** setter for startIndent - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStartIndent(int v) {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_startIndent == null)
      jcasType.jcas.throwFeatMissing("startIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_startIndent, v);}    
   
    
  //*--------------*
  //* Feature: lineSpacing

  /** getter for lineSpacing - gets 
   * @generated
   * @return value of the feature 
   */
  public int getLineSpacing() {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_lineSpacing == null)
      jcasType.jcas.throwFeatMissing("lineSpacing", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_lineSpacing);}
    
  /** setter for lineSpacing - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLineSpacing(int v) {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_lineSpacing == null)
      jcasType.jcas.throwFeatMissing("lineSpacing", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_lineSpacing, v);}    
   
    
  //*--------------*
  //* Feature: align

  /** getter for align - gets 
   * @generated
   * @return value of the feature 
   */
  public String getAlign() {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_align == null)
      jcasType.jcas.throwFeatMissing("align", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_align);}
    
  /** setter for align - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAlign(String v) {
    if (OCRParagraph_Type.featOkTst && ((OCRParagraph_Type)jcasType).casFeat_align == null)
      jcasType.jcas.throwFeatMissing("align", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRParagraph_Type)jcasType).casFeatCode_align, v);}    
  }

    