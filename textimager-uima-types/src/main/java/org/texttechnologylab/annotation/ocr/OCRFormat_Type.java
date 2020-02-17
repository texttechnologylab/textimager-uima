
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
public class OCRFormat_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = OCRFormat.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.ocr.OCRFormat");
 
  /** @generated */
  final Feature casFeat_lang;
  /** @generated */
  final int     casFeatCode_lang;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getLang(int addr) {
        if (featOkTst && casFeat_lang == null)
      jcas.throwFeatMissing("lang", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getStringValue(addr, casFeatCode_lang);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLang(int addr, String v) {
        if (featOkTst && casFeat_lang == null)
      jcas.throwFeatMissing("lang", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setStringValue(addr, casFeatCode_lang, v);}
    
  
 
  /** @generated */
  final Feature casFeat_ff;
  /** @generated */
  final int     casFeatCode_ff;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getFf(int addr) {
        if (featOkTst && casFeat_ff == null)
      jcas.throwFeatMissing("ff", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getStringValue(addr, casFeatCode_ff);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFf(int addr, String v) {
        if (featOkTst && casFeat_ff == null)
      jcas.throwFeatMissing("ff", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setStringValue(addr, casFeatCode_ff, v);}
    
  
 
  /** @generated */
  final Feature casFeat_fs;
  /** @generated */
  final int     casFeatCode_fs;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public float getFs(int addr) {
        if (featOkTst && casFeat_fs == null)
      jcas.throwFeatMissing("fs", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getFloatValue(addr, casFeatCode_fs);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setFs(int addr, float v) {
        if (featOkTst && casFeat_fs == null)
      jcas.throwFeatMissing("fs", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setFloatValue(addr, casFeatCode_fs, v);}
    
  
 
  /** @generated */
  final Feature casFeat_bold;
  /** @generated */
  final int     casFeatCode_bold;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getBold(int addr) {
        if (featOkTst && casFeat_bold == null)
      jcas.throwFeatMissing("bold", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_bold);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setBold(int addr, boolean v) {
        if (featOkTst && casFeat_bold == null)
      jcas.throwFeatMissing("bold", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_bold, v);}
    
  
 
  /** @generated */
  final Feature casFeat_italic;
  /** @generated */
  final int     casFeatCode_italic;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getItalic(int addr) {
        if (featOkTst && casFeat_italic == null)
      jcas.throwFeatMissing("italic", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_italic);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setItalic(int addr, boolean v) {
        if (featOkTst && casFeat_italic == null)
      jcas.throwFeatMissing("italic", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_italic, v);}
    
  
 
  /** @generated */
  final Feature casFeat_subscript;
  /** @generated */
  final int     casFeatCode_subscript;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getSubscript(int addr) {
        if (featOkTst && casFeat_subscript == null)
      jcas.throwFeatMissing("subscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_subscript);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSubscript(int addr, boolean v) {
        if (featOkTst && casFeat_subscript == null)
      jcas.throwFeatMissing("subscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_subscript, v);}
    
  
 
  /** @generated */
  final Feature casFeat_superscript;
  /** @generated */
  final int     casFeatCode_superscript;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getSuperscript(int addr) {
        if (featOkTst && casFeat_superscript == null)
      jcas.throwFeatMissing("superscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_superscript);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSuperscript(int addr, boolean v) {
        if (featOkTst && casFeat_superscript == null)
      jcas.throwFeatMissing("superscript", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_superscript, v);}
    
  
 
  /** @generated */
  final Feature casFeat_smallcaps;
  /** @generated */
  final int     casFeatCode_smallcaps;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getSmallcaps(int addr) {
        if (featOkTst && casFeat_smallcaps == null)
      jcas.throwFeatMissing("smallcaps", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_smallcaps);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSmallcaps(int addr, boolean v) {
        if (featOkTst && casFeat_smallcaps == null)
      jcas.throwFeatMissing("smallcaps", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_smallcaps, v);}
    
  
 
  /** @generated */
  final Feature casFeat_underline;
  /** @generated */
  final int     casFeatCode_underline;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getUnderline(int addr) {
        if (featOkTst && casFeat_underline == null)
      jcas.throwFeatMissing("underline", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_underline);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUnderline(int addr, boolean v) {
        if (featOkTst && casFeat_underline == null)
      jcas.throwFeatMissing("underline", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_underline, v);}
    
  
 
  /** @generated */
  final Feature casFeat_strikeout;
  /** @generated */
  final int     casFeatCode_strikeout;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getStrikeout(int addr) {
        if (featOkTst && casFeat_strikeout == null)
      jcas.throwFeatMissing("strikeout", "org.texttechnologylab.annotation.ocr.OCRFormat");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_strikeout);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setStrikeout(int addr, boolean v) {
        if (featOkTst && casFeat_strikeout == null)
      jcas.throwFeatMissing("strikeout", "org.texttechnologylab.annotation.ocr.OCRFormat");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_strikeout, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public OCRFormat_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_lang = jcas.getRequiredFeatureDE(casType, "lang", "uima.cas.String", featOkTst);
    casFeatCode_lang  = (null == casFeat_lang) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_lang).getCode();

 
    casFeat_ff = jcas.getRequiredFeatureDE(casType, "ff", "uima.cas.String", featOkTst);
    casFeatCode_ff  = (null == casFeat_ff) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_ff).getCode();

 
    casFeat_fs = jcas.getRequiredFeatureDE(casType, "fs", "uima.cas.Float", featOkTst);
    casFeatCode_fs  = (null == casFeat_fs) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_fs).getCode();

 
    casFeat_bold = jcas.getRequiredFeatureDE(casType, "bold", "uima.cas.Boolean", featOkTst);
    casFeatCode_bold  = (null == casFeat_bold) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_bold).getCode();

 
    casFeat_italic = jcas.getRequiredFeatureDE(casType, "italic", "uima.cas.Boolean", featOkTst);
    casFeatCode_italic  = (null == casFeat_italic) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_italic).getCode();

 
    casFeat_subscript = jcas.getRequiredFeatureDE(casType, "subscript", "uima.cas.Boolean", featOkTst);
    casFeatCode_subscript  = (null == casFeat_subscript) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_subscript).getCode();

 
    casFeat_superscript = jcas.getRequiredFeatureDE(casType, "superscript", "uima.cas.Boolean", featOkTst);
    casFeatCode_superscript  = (null == casFeat_superscript) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_superscript).getCode();

 
    casFeat_smallcaps = jcas.getRequiredFeatureDE(casType, "smallcaps", "uima.cas.Boolean", featOkTst);
    casFeatCode_smallcaps  = (null == casFeat_smallcaps) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_smallcaps).getCode();

 
    casFeat_underline = jcas.getRequiredFeatureDE(casType, "underline", "uima.cas.Boolean", featOkTst);
    casFeatCode_underline  = (null == casFeat_underline) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_underline).getCode();

 
    casFeat_strikeout = jcas.getRequiredFeatureDE(casType, "strikeout", "uima.cas.Boolean", featOkTst);
    casFeatCode_strikeout  = (null == casFeat_strikeout) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_strikeout).getCode();

  }
}



    