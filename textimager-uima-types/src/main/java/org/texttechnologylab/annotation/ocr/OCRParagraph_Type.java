
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
public class OCRParagraph_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = OCRParagraph.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.ocr.OCRParagraph");
 
  /** @generated */
  final Feature casFeat_leftIndent;
  /** @generated */
  final int     casFeatCode_leftIndent;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLeftIndent(int addr) {
        if (featOkTst && casFeat_leftIndent == null)
      jcas.throwFeatMissing("leftIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return ll_cas.ll_getIntValue(addr, casFeatCode_leftIndent);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLeftIndent(int addr, int v) {
        if (featOkTst && casFeat_leftIndent == null)
      jcas.throwFeatMissing("leftIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    ll_cas.ll_setIntValue(addr, casFeatCode_leftIndent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_rightIndent;
  /** @generated */
  final int     casFeatCode_rightIndent;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getRightIndent(int addr) {
        if (featOkTst && casFeat_rightIndent == null)
      jcas.throwFeatMissing("rightIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return ll_cas.ll_getIntValue(addr, casFeatCode_rightIndent);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRightIndent(int addr, int v) {
        if (featOkTst && casFeat_rightIndent == null)
      jcas.throwFeatMissing("rightIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    ll_cas.ll_setIntValue(addr, casFeatCode_rightIndent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_startIndent;
  /** @generated */
  final int     casFeatCode_startIndent;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getStartIndent(int addr) {
        if (featOkTst && casFeat_startIndent == null)
      jcas.throwFeatMissing("startIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return ll_cas.ll_getIntValue(addr, casFeatCode_startIndent);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStartIndent(int addr, int v) {
        if (featOkTst && casFeat_startIndent == null)
      jcas.throwFeatMissing("startIndent", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    ll_cas.ll_setIntValue(addr, casFeatCode_startIndent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_lineSpacing;
  /** @generated */
  final int     casFeatCode_lineSpacing;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLineSpacing(int addr) {
        if (featOkTst && casFeat_lineSpacing == null)
      jcas.throwFeatMissing("lineSpacing", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return ll_cas.ll_getIntValue(addr, casFeatCode_lineSpacing);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLineSpacing(int addr, int v) {
        if (featOkTst && casFeat_lineSpacing == null)
      jcas.throwFeatMissing("lineSpacing", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    ll_cas.ll_setIntValue(addr, casFeatCode_lineSpacing, v);}
    
  
 
  /** @generated */
  final Feature casFeat_align;
  /** @generated */
  final int     casFeatCode_align;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAlign(int addr) {
        if (featOkTst && casFeat_align == null)
      jcas.throwFeatMissing("align", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    return ll_cas.ll_getStringValue(addr, casFeatCode_align);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAlign(int addr, String v) {
        if (featOkTst && casFeat_align == null)
      jcas.throwFeatMissing("align", "org.texttechnologylab.annotation.ocr.OCRParagraph");
    ll_cas.ll_setStringValue(addr, casFeatCode_align, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public OCRParagraph_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_leftIndent = jcas.getRequiredFeatureDE(casType, "leftIndent", "uima.cas.Integer", featOkTst);
    casFeatCode_leftIndent  = (null == casFeat_leftIndent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_leftIndent).getCode();

 
    casFeat_rightIndent = jcas.getRequiredFeatureDE(casType, "rightIndent", "uima.cas.Integer", featOkTst);
    casFeatCode_rightIndent  = (null == casFeat_rightIndent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_rightIndent).getCode();

 
    casFeat_startIndent = jcas.getRequiredFeatureDE(casType, "startIndent", "uima.cas.Integer", featOkTst);
    casFeatCode_startIndent  = (null == casFeat_startIndent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_startIndent).getCode();

 
    casFeat_lineSpacing = jcas.getRequiredFeatureDE(casType, "lineSpacing", "uima.cas.Integer", featOkTst);
    casFeatCode_lineSpacing  = (null == casFeat_lineSpacing) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lineSpacing).getCode();

 
    casFeat_align = jcas.getRequiredFeatureDE(casType, "align", "uima.cas.String", featOkTst);
    casFeatCode_align  = (null == casFeat_align) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_align).getCode();

  }
}



    