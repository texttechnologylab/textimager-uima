
/* First created by JCasGen Tue Jul 28 16:22:15 CEST 2020 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Jul 28 16:22:15 CEST 2020
 * @generated */
public class Sentiment_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Sentiment.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.Sentiment");
 
  /** @generated */
  final Feature casFeat_sentiment;
  /** @generated */
  final int     casFeatCode_sentiment;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getSentiment(int addr) {
        if (featOkTst && casFeat_sentiment == null)
      jcas.throwFeatMissing("sentiment", "org.hucompute.textimager.uima.type.Sentiment");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_sentiment);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSentiment(int addr, double v) {
        if (featOkTst && casFeat_sentiment == null)
      jcas.throwFeatMissing("sentiment", "org.hucompute.textimager.uima.type.Sentiment");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_sentiment, v);}
    
  
 
  /** @generated */
  final Feature casFeat_subjectivity;
  /** @generated */
  final int     casFeatCode_subjectivity;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public double getSubjectivity(int addr) {
        if (featOkTst && casFeat_subjectivity == null)
      jcas.throwFeatMissing("subjectivity", "org.hucompute.textimager.uima.type.Sentiment");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_subjectivity);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSubjectivity(int addr, double v) {
        if (featOkTst && casFeat_subjectivity == null)
      jcas.throwFeatMissing("subjectivity", "org.hucompute.textimager.uima.type.Sentiment");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_subjectivity, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Sentiment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_sentiment = jcas.getRequiredFeatureDE(casType, "sentiment", "uima.cas.Double", featOkTst);
    casFeatCode_sentiment  = (null == casFeat_sentiment) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_sentiment).getCode();

 
    casFeat_subjectivity = jcas.getRequiredFeatureDE(casType, "subjectivity", "uima.cas.Double", featOkTst);
    casFeatCode_subjectivity  = (null == casFeat_subjectivity) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_subjectivity).getCode();

  }
}



    