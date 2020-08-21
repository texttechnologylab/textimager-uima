
/* First created by JCasGen Mon Apr 08 12:26:54 CEST 2019 */
package org.hucompute.wikidragon.core.nlp.annotation;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Apr 08 12:26:54 CEST 2019
 * @generated */
public class WikiTextSpan_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WikiTextSpan.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.wikidragon.core.nlp.annotation.WikiTextSpan");
 
  /** @generated */
  final Feature casFeat_uid;
  /** @generated */
  final int     casFeatCode_uid;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getUid(int addr) {
        if (featOkTst && casFeat_uid == null)
      jcas.throwFeatMissing("uid", "org.hucompute.wikidragon.core.nlp.annotation.WikiTextSpan");
    return ll_cas.ll_getStringValue(addr, casFeatCode_uid);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setUid(int addr, String v) {
        if (featOkTst && casFeat_uid == null)
      jcas.throwFeatMissing("uid", "org.hucompute.wikidragon.core.nlp.annotation.WikiTextSpan");
    ll_cas.ll_setStringValue(addr, casFeatCode_uid, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public WikiTextSpan_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_uid = jcas.getRequiredFeatureDE(casType, "uid", "uima.cas.String", featOkTst);
    casFeatCode_uid  = (null == casFeat_uid) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_uid).getCode();

  }
}



    