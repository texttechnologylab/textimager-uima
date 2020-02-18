

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
public class OCRPage extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OCRPage.class);
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
  protected OCRPage() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OCRPage(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OCRPage(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OCRPage(JCas jcas, int begin, int end) {
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
  //* Feature: width

  /** getter for width - gets 
   * @generated
   * @return value of the feature 
   */
  public int getWidth() {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_width == null)
      jcasType.jcas.throwFeatMissing("width", "org.texttechnologylab.annotation.ocr.OCRPage");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_width);}
    
  /** setter for width - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setWidth(int v) {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_width == null)
      jcasType.jcas.throwFeatMissing("width", "org.texttechnologylab.annotation.ocr.OCRPage");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_width, v);}    
   
    
  //*--------------*
  //* Feature: height

  /** getter for height - gets 
   * @generated
   * @return value of the feature 
   */
  public int getHeight() {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_height == null)
      jcasType.jcas.throwFeatMissing("height", "org.texttechnologylab.annotation.ocr.OCRPage");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_height);}
    
  /** setter for height - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setHeight(int v) {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_height == null)
      jcasType.jcas.throwFeatMissing("height", "org.texttechnologylab.annotation.ocr.OCRPage");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_height, v);}    
   
    
  //*--------------*
  //* Feature: resolution

  /** getter for resolution - gets 
   * @generated
   * @return value of the feature 
   */
  public int getResolution() {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_resolution == null)
      jcasType.jcas.throwFeatMissing("resolution", "org.texttechnologylab.annotation.ocr.OCRPage");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_resolution);}
    
  /** setter for resolution - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setResolution(int v) {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_resolution == null)
      jcasType.jcas.throwFeatMissing("resolution", "org.texttechnologylab.annotation.ocr.OCRPage");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_resolution, v);}    
   
    
  //*--------------*
  //* Feature: pageId

  /** getter for pageId - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPageId() {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_pageId == null)
      jcasType.jcas.throwFeatMissing("pageId", "org.texttechnologylab.annotation.ocr.OCRPage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRPage_Type)jcasType).casFeatCode_pageId);}
    
  /** setter for pageId - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPageId(String v) {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_pageId == null)
      jcasType.jcas.throwFeatMissing("pageId", "org.texttechnologylab.annotation.ocr.OCRPage");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRPage_Type)jcasType).casFeatCode_pageId, v);}    
   
    
  //*--------------*
  //* Feature: pageNumber

  /** getter for pageNumber - gets 
   * @generated
   * @return value of the feature 
   */
  public int getPageNumber() {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_pageNumber == null)
      jcasType.jcas.throwFeatMissing("pageNumber", "org.texttechnologylab.annotation.ocr.OCRPage");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_pageNumber);}
    
  /** setter for pageNumber - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPageNumber(int v) {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_pageNumber == null)
      jcasType.jcas.throwFeatMissing("pageNumber", "org.texttechnologylab.annotation.ocr.OCRPage");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRPage_Type)jcasType).casFeatCode_pageNumber, v);}    
   
    
  //*--------------*
  //* Feature: uri

  /** getter for uri - gets 
   * @generated
   * @return value of the feature 
   */
  public String getUri() {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "org.texttechnologylab.annotation.ocr.OCRPage");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRPage_Type)jcasType).casFeatCode_uri);}
    
  /** setter for uri - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setUri(String v) {
    if (OCRPage_Type.featOkTst && ((OCRPage_Type)jcasType).casFeat_uri == null)
      jcasType.jcas.throwFeatMissing("uri", "org.texttechnologylab.annotation.ocr.OCRPage");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRPage_Type)jcasType).casFeatCode_uri, v);}    
  }

    