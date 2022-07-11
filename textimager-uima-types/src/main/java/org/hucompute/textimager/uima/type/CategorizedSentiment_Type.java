
/* First created by JCasGen Mon Jul 11 11:54:35 CEST 2022 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** 
 * Updated by JCasGen Mon Jul 11 11:54:35 CEST 2022
 * @generated */
public class CategorizedSentiment_Type extends Sentiment_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = CategorizedSentiment.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.CategorizedSentiment");
 
  /** @generated */
  final Feature casFeat_pos;
  /** @generated */
  final int     casFeatCode_pos;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getPos(int addr) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "org.hucompute.textimager.uima.type.CategorizedSentiment");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_pos);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPos(int addr, double v) {
        if (featOkTst && casFeat_pos == null)
      jcas.throwFeatMissing("pos", "org.hucompute.textimager.uima.type.CategorizedSentiment");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_pos, v);}
    
  
 
  /** @generated */
  final Feature casFeat_neu;
  /** @generated */
  final int     casFeatCode_neu;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getNeu(int addr) {
        if (featOkTst && casFeat_neu == null)
      jcas.throwFeatMissing("neu", "org.hucompute.textimager.uima.type.CategorizedSentiment");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_neu);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNeu(int addr, double v) {
        if (featOkTst && casFeat_neu == null)
      jcas.throwFeatMissing("neu", "org.hucompute.textimager.uima.type.CategorizedSentiment");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_neu, v);}
    
  
 
  /** @generated */
  final Feature casFeat_neg;
  /** @generated */
  final int     casFeatCode_neg;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getNeg(int addr) {
        if (featOkTst && casFeat_neg == null)
      jcas.throwFeatMissing("neg", "org.hucompute.textimager.uima.type.CategorizedSentiment");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_neg);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNeg(int addr, double v) {
        if (featOkTst && casFeat_neg == null)
      jcas.throwFeatMissing("neg", "org.hucompute.textimager.uima.type.CategorizedSentiment");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_neg, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public CategorizedSentiment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pos = jcas.getRequiredFeatureDE(casType, "pos", "uima.cas.Double", featOkTst);
    casFeatCode_pos  = (null == casFeat_pos) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pos).getCode();

 
    casFeat_neu = jcas.getRequiredFeatureDE(casType, "neu", "uima.cas.Double", featOkTst);
    casFeatCode_neu  = (null == casFeat_neu) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_neu).getCode();

 
    casFeat_neg = jcas.getRequiredFeatureDE(casType, "neg", "uima.cas.Double", featOkTst);
    casFeatCode_neg  = (null == casFeat_neg) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_neg).getCode();

  }
}



    