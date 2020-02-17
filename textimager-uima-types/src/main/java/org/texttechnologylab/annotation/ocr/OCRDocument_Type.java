
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
public class OCRDocument_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = OCRDocument.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.ocr.OCRDocument");
 
  /** @generated */
  final Feature casFeat_documentname;
  /** @generated */
  final int     casFeatCode_documentname;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDocumentname(int addr) {
        if (featOkTst && casFeat_documentname == null)
      jcas.throwFeatMissing("documentname", "org.texttechnologylab.annotation.ocr.OCRDocument");
    return ll_cas.ll_getStringValue(addr, casFeatCode_documentname);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDocumentname(int addr, String v) {
        if (featOkTst && casFeat_documentname == null)
      jcas.throwFeatMissing("documentname", "org.texttechnologylab.annotation.ocr.OCRDocument");
    ll_cas.ll_setStringValue(addr, casFeatCode_documentname, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public OCRDocument_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_documentname = jcas.getRequiredFeatureDE(casType, "documentname", "uima.cas.String", featOkTst);
    casFeatCode_documentname  = (null == casFeat_documentname) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_documentname).getCode();

  }
}



    