

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
public class OCRFormat extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OCRFormat.class);
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
  protected OCRFormat() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OCRFormat(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OCRFormat(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OCRFormat(JCas jcas, int begin, int end) {
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
  //* Feature: lang

  /** getter for lang - gets 
   * @generated
   * @return value of the feature 
   */
  public String getLang() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_lang == null)
      jcasType.jcas.throwFeatMissing("lang", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_lang);}
    
  /** setter for lang - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLang(String v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_lang == null)
      jcasType.jcas.throwFeatMissing("lang", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_lang, v);}    
   
    
  //*--------------*
  //* Feature: ff

  /** getter for ff - gets 
   * @generated
   * @return value of the feature 
   */
  public String getFf() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_ff == null)
      jcasType.jcas.throwFeatMissing("ff", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_ff);}
    
  /** setter for ff - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFf(String v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_ff == null)
      jcasType.jcas.throwFeatMissing("ff", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_ff, v);}    
   
    
  //*--------------*
  //* Feature: fs

  /** getter for fs - gets 
   * @generated
   * @return value of the feature 
   */
  public float getFs() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_fs == null)
      jcasType.jcas.throwFeatMissing("fs", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_fs);}
    
  /** setter for fs - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setFs(float v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_fs == null)
      jcasType.jcas.throwFeatMissing("fs", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setFloatValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_fs, v);}    
   
    
  //*--------------*
  //* Feature: bold

  /** getter for bold - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getBold() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_bold == null)
      jcasType.jcas.throwFeatMissing("bold", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_bold);}
    
  /** setter for bold - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setBold(boolean v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_bold == null)
      jcasType.jcas.throwFeatMissing("bold", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_bold, v);}    
   
    
  //*--------------*
  //* Feature: italic

  /** getter for italic - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getItalic() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_italic == null)
      jcasType.jcas.throwFeatMissing("italic", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_italic);}
    
  /** setter for italic - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setItalic(boolean v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_italic == null)
      jcasType.jcas.throwFeatMissing("italic", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_italic, v);}    
   
    
  //*--------------*
  //* Feature: subscript

  /** getter for subscript - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getSubscript() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_subscript == null)
      jcasType.jcas.throwFeatMissing("subscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_subscript);}
    
  /** setter for subscript - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubscript(boolean v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_subscript == null)
      jcasType.jcas.throwFeatMissing("subscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_subscript, v);}    
   
    
  //*--------------*
  //* Feature: superscript

  /** getter for superscript - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getSuperscript() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_superscript == null)
      jcasType.jcas.throwFeatMissing("superscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_superscript);}
    
  /** setter for superscript - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSuperscript(boolean v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_superscript == null)
      jcasType.jcas.throwFeatMissing("superscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_superscript, v);}    
   
    
  //*--------------*
  //* Feature: smallcaps

  /** getter for smallcaps - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getSmallcaps() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_smallcaps == null)
      jcasType.jcas.throwFeatMissing("smallcaps", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_smallcaps);}
    
  /** setter for smallcaps - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSmallcaps(boolean v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_smallcaps == null)
      jcasType.jcas.throwFeatMissing("smallcaps", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_smallcaps, v);}    
   
    
  //*--------------*
  //* Feature: underline

  /** getter for underline - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getUnderline() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_underline == null)
      jcasType.jcas.throwFeatMissing("underline", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_underline);}
    
  /** setter for underline - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUnderline(boolean v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_underline == null)
      jcasType.jcas.throwFeatMissing("underline", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_underline, v);}    
   
    
  //*--------------*
  //* Feature: strikeout

  /** getter for strikeout - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getStrikeout() {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_strikeout == null)
      jcasType.jcas.throwFeatMissing("strikeout", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_strikeout);}
    
  /** setter for strikeout - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setStrikeout(boolean v) {
    if (OCRFormat_Type.featOkTst && ((OCRFormat_Type)jcasType).casFeat_strikeout == null)
      jcasType.jcas.throwFeatMissing("strikeout", "org.texttechnologylab.annotation.ocr.OCRFormat");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRFormat_Type)jcasType).casFeatCode_strikeout, v);}    
  }

    