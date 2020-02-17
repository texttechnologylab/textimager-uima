
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
public class OCRLine_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = OCRLine.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.ocr.OCRLine");
 
  /** @generated */
  final Feature casFeat_baseline;
  /** @generated */
  final int     casFeatCode_baseline;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getBaseline(int addr) {
        if (featOkTst && casFeat_baseline == null)
      jcas.throwFeatMissing("baseline", "org.texttechnologylab.annotation.ocr.OCRLine");
    return ll_cas.ll_getIntValue(addr, casFeatCode_baseline);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBaseline(int addr, int v) {
        if (featOkTst && casFeat_baseline == null)
      jcas.throwFeatMissing("baseline", "org.texttechnologylab.annotation.ocr.OCRLine");
    ll_cas.ll_setIntValue(addr, casFeatCode_baseline, v);}
    
  
 
  /** @generated */
  final Feature casFeat_top;
  /** @generated */
  final int     casFeatCode_top;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getTop(int addr) {
        if (featOkTst && casFeat_top == null)
      jcas.throwFeatMissing("top", "org.texttechnologylab.annotation.ocr.OCRLine");
    return ll_cas.ll_getIntValue(addr, casFeatCode_top);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTop(int addr, int v) {
        if (featOkTst && casFeat_top == null)
      jcas.throwFeatMissing("top", "org.texttechnologylab.annotation.ocr.OCRLine");
    ll_cas.ll_setIntValue(addr, casFeatCode_top, v);}
    
  
 
  /** @generated */
  final Feature casFeat_bottom;
  /** @generated */
  final int     casFeatCode_bottom;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getBottom(int addr) {
        if (featOkTst && casFeat_bottom == null)
      jcas.throwFeatMissing("bottom", "org.texttechnologylab.annotation.ocr.OCRLine");
    return ll_cas.ll_getIntValue(addr, casFeatCode_bottom);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBottom(int addr, int v) {
        if (featOkTst && casFeat_bottom == null)
      jcas.throwFeatMissing("bottom", "org.texttechnologylab.annotation.ocr.OCRLine");
    ll_cas.ll_setIntValue(addr, casFeatCode_bottom, v);}
    
  
 
  /** @generated */
  final Feature casFeat_left;
  /** @generated */
  final int     casFeatCode_left;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLeft(int addr) {
        if (featOkTst && casFeat_left == null)
      jcas.throwFeatMissing("left", "org.texttechnologylab.annotation.ocr.OCRLine");
    return ll_cas.ll_getIntValue(addr, casFeatCode_left);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLeft(int addr, int v) {
        if (featOkTst && casFeat_left == null)
      jcas.throwFeatMissing("left", "org.texttechnologylab.annotation.ocr.OCRLine");
    ll_cas.ll_setIntValue(addr, casFeatCode_left, v);}
    
  
 
  /** @generated */
  final Feature casFeat_right;
  /** @generated */
  final int     casFeatCode_right;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getRight(int addr) {
        if (featOkTst && casFeat_right == null)
      jcas.throwFeatMissing("right", "org.texttechnologylab.annotation.ocr.OCRLine");
    return ll_cas.ll_getIntValue(addr, casFeatCode_right);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRight(int addr, int v) {
        if (featOkTst && casFeat_right == null)
      jcas.throwFeatMissing("right", "org.texttechnologylab.annotation.ocr.OCRLine");
    ll_cas.ll_setIntValue(addr, casFeatCode_right, v);}
    
  
 
  /** @generated */
  final Feature casFeat_format;
  /** @generated */
  final int     casFeatCode_format;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFormat(int addr) {
        if (featOkTst && casFeat_format == null)
      jcas.throwFeatMissing("format", "org.texttechnologylab.annotation.ocr.OCRLine");
    return ll_cas.ll_getStringValue(addr, casFeatCode_format);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFormat(int addr, String v) {
        if (featOkTst && casFeat_format == null)
      jcas.throwFeatMissing("format", "org.texttechnologylab.annotation.ocr.OCRLine");
    ll_cas.ll_setStringValue(addr, casFeatCode_format, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public OCRLine_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_baseline = jcas.getRequiredFeatureDE(casType, "baseline", "uima.cas.Integer", featOkTst);
    casFeatCode_baseline  = (null == casFeat_baseline) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_baseline).getCode();

 
    casFeat_top = jcas.getRequiredFeatureDE(casType, "top", "uima.cas.Integer", featOkTst);
    casFeatCode_top  = (null == casFeat_top) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_top).getCode();

 
    casFeat_bottom = jcas.getRequiredFeatureDE(casType, "bottom", "uima.cas.Integer", featOkTst);
    casFeatCode_bottom  = (null == casFeat_bottom) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_bottom).getCode();

 
    casFeat_left = jcas.getRequiredFeatureDE(casType, "left", "uima.cas.Integer", featOkTst);
    casFeatCode_left  = (null == casFeat_left) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_left).getCode();

 
    casFeat_right = jcas.getRequiredFeatureDE(casType, "right", "uima.cas.Integer", featOkTst);
    casFeatCode_right  = (null == casFeat_right) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_right).getCode();

 
    casFeat_format = jcas.getRequiredFeatureDE(casType, "format", "uima.cas.String", featOkTst);
    casFeatCode_format  = (null == casFeat_format) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_format).getCode();

  }
}



    