
/* First created by JCasGen Tue Feb 04 20:02:41 CET 2020 */
package org.texttechnologylab.annotation.ocr;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Feb 04 20:02:41 CET 2020
 * @generated */
public class OCRPage_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = OCRPage.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.ocr.OCRPage");
 
  /** @generated */
  final Feature casFeat_width;
  /** @generated */
  final int     casFeatCode_width;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getWidth(int addr) {
        if (featOkTst && casFeat_width == null)
      jcas.throwFeatMissing("width", "org.texttechnologylab.annotation.ocr.OCRPage");
    return ll_cas.ll_getIntValue(addr, casFeatCode_width);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setWidth(int addr, int v) {
        if (featOkTst && casFeat_width == null)
      jcas.throwFeatMissing("width", "org.texttechnologylab.annotation.ocr.OCRPage");
    ll_cas.ll_setIntValue(addr, casFeatCode_width, v);}
    
  
 
  /** @generated */
  final Feature casFeat_height;
  /** @generated */
  final int     casFeatCode_height;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getHeight(int addr) {
        if (featOkTst && casFeat_height == null)
      jcas.throwFeatMissing("height", "org.texttechnologylab.annotation.ocr.OCRPage");
    return ll_cas.ll_getIntValue(addr, casFeatCode_height);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setHeight(int addr, int v) {
        if (featOkTst && casFeat_height == null)
      jcas.throwFeatMissing("height", "org.texttechnologylab.annotation.ocr.OCRPage");
    ll_cas.ll_setIntValue(addr, casFeatCode_height, v);}
    
  
 
  /** @generated */
  final Feature casFeat_resolution;
  /** @generated */
  final int     casFeatCode_resolution;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getResolution(int addr) {
        if (featOkTst && casFeat_resolution == null)
      jcas.throwFeatMissing("resolution", "org.texttechnologylab.annotation.ocr.OCRPage");
    return ll_cas.ll_getIntValue(addr, casFeatCode_resolution);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setResolution(int addr, int v) {
        if (featOkTst && casFeat_resolution == null)
      jcas.throwFeatMissing("resolution", "org.texttechnologylab.annotation.ocr.OCRPage");
    ll_cas.ll_setIntValue(addr, casFeatCode_resolution, v);}
    
  
 
  /** @generated */
  final Feature casFeat_pageId;
  /** @generated */
  final int     casFeatCode_pageId;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPageId(int addr) {
        if (featOkTst && casFeat_pageId == null)
      jcas.throwFeatMissing("pageId", "org.texttechnologylab.annotation.ocr.OCRPage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pageId);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPageId(int addr, String v) {
        if (featOkTst && casFeat_pageId == null)
      jcas.throwFeatMissing("pageId", "org.texttechnologylab.annotation.ocr.OCRPage");
    ll_cas.ll_setStringValue(addr, casFeatCode_pageId, v);}
    
  
 
  /** @generated */
  final Feature casFeat_pageNumber;
  /** @generated */
  final int     casFeatCode_pageNumber;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getPageNumber(int addr) {
        if (featOkTst && casFeat_pageNumber == null)
      jcas.throwFeatMissing("pageNumber", "org.texttechnologylab.annotation.ocr.OCRPage");
    return ll_cas.ll_getIntValue(addr, casFeatCode_pageNumber);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPageNumber(int addr, int v) {
        if (featOkTst && casFeat_pageNumber == null)
      jcas.throwFeatMissing("pageNumber", "org.texttechnologylab.annotation.ocr.OCRPage");
    ll_cas.ll_setIntValue(addr, casFeatCode_pageNumber, v);}
    
  
 
  /** @generated */
  final Feature casFeat_uri;
  /** @generated */
  final int     casFeatCode_uri;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUri(int addr) {
        if (featOkTst && casFeat_uri == null)
      jcas.throwFeatMissing("uri", "org.texttechnologylab.annotation.ocr.OCRPage");
    return ll_cas.ll_getStringValue(addr, casFeatCode_uri);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUri(int addr, String v) {
        if (featOkTst && casFeat_uri == null)
      jcas.throwFeatMissing("uri", "org.texttechnologylab.annotation.ocr.OCRPage");
    ll_cas.ll_setStringValue(addr, casFeatCode_uri, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public OCRPage_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_width = jcas.getRequiredFeatureDE(casType, "width", "uima.cas.Integer", featOkTst);
    casFeatCode_width  = (null == casFeat_width) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_width).getCode();

 
    casFeat_height = jcas.getRequiredFeatureDE(casType, "height", "uima.cas.Integer", featOkTst);
    casFeatCode_height  = (null == casFeat_height) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_height).getCode();

 
    casFeat_resolution = jcas.getRequiredFeatureDE(casType, "resolution", "uima.cas.Integer", featOkTst);
    casFeatCode_resolution  = (null == casFeat_resolution) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_resolution).getCode();

 
    casFeat_pageId = jcas.getRequiredFeatureDE(casType, "pageId", "uima.cas.String", featOkTst);
    casFeatCode_pageId  = (null == casFeat_pageId) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pageId).getCode();

 
    casFeat_pageNumber = jcas.getRequiredFeatureDE(casType, "pageNumber", "uima.cas.Integer", featOkTst);
    casFeatCode_pageNumber  = (null == casFeat_pageNumber) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pageNumber).getCode();

 
    casFeat_uri = jcas.getRequiredFeatureDE(casType, "uri", "uima.cas.String", featOkTst);
    casFeatCode_uri  = (null == casFeat_uri) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_uri).getCode();

  }
}



    