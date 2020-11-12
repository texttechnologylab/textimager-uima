
/* First created by JCasGen Fri Sep 18 11:31:56 CEST 2020 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Fri Sep 18 11:31:56 CEST 2020
 * @generated */
public class Summary_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Summary.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.Summary");
 
  /** @generated */
  final Feature casFeat_summary;
  /** @generated */
  final int     casFeatCode_summary;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getSummary(int addr) {
        if (featOkTst && casFeat_summary == null)
      jcas.throwFeatMissing("summary", "org.hucompute.textimager.uima.type.Summary");
    return ll_cas.ll_getStringValue(addr, casFeatCode_summary);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSummary(int addr, String v) {
        if (featOkTst && casFeat_summary == null)
      jcas.throwFeatMissing("summary", "org.hucompute.textimager.uima.type.Summary");
    ll_cas.ll_setStringValue(addr, casFeatCode_summary, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Summary_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_summary = jcas.getRequiredFeatureDE(casType, "summary", "uima.cas.String", featOkTst);
    casFeatCode_summary  = (null == casFeat_summary) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_summary).getCode();

  }
}



    