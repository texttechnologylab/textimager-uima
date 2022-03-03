
/* First created by JCasGen Mon Aug 02 09:48:13 CEST 2021 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation_Type;

/**
 * Updated by JCasGen Mon Aug 02 09:48:13 CEST 2021
 * @generated */
public class OpenIERelation_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = OpenIERelation.typeIndexID;
  /** @generated
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.OpenIERelation");

  /** @generated */
  final Feature casFeat_confidence;
  /** @generated */
  final int     casFeatCode_confidence;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public double getConfidence(int addr) {
        if (featOkTst && casFeat_confidence == null)
      jcas.throwFeatMissing("confidence", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getDoubleValue(addr, casFeatCode_confidence);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setConfidence(int addr, double v) {
        if (featOkTst && casFeat_confidence == null)
      jcas.throwFeatMissing("confidence", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setDoubleValue(addr, casFeatCode_confidence, v);}



  /** @generated */
  final Feature casFeat_beginArg1;
  /** @generated */
  final int     casFeatCode_beginArg1;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getBeginArg1(int addr) {
        if (featOkTst && casFeat_beginArg1 == null)
      jcas.throwFeatMissing("beginArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_beginArg1);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setBeginArg1(int addr, int v) {
        if (featOkTst && casFeat_beginArg1 == null)
      jcas.throwFeatMissing("beginArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setIntValue(addr, casFeatCode_beginArg1, v);}



  /** @generated */
  final Feature casFeat_endArg1;
  /** @generated */
  final int     casFeatCode_endArg1;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getEndArg1(int addr) {
        if (featOkTst && casFeat_endArg1 == null)
      jcas.throwFeatMissing("endArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_endArg1);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setEndArg1(int addr, int v) {
        if (featOkTst && casFeat_endArg1 == null)
      jcas.throwFeatMissing("endArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setIntValue(addr, casFeatCode_endArg1, v);}



  /** @generated */
  final Feature casFeat_valueArg1;
  /** @generated */
  final int     casFeatCode_valueArg1;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getValueArg1(int addr) {
        if (featOkTst && casFeat_valueArg1 == null)
      jcas.throwFeatMissing("valueArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_valueArg1);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setValueArg1(int addr, String v) {
        if (featOkTst && casFeat_valueArg1 == null)
      jcas.throwFeatMissing("valueArg1", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_valueArg1, v);}



  /** @generated */
  final Feature casFeat_beginRel;
  /** @generated */
  final int     casFeatCode_beginRel;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getBeginRel(int addr) {
        if (featOkTst && casFeat_beginRel == null)
      jcas.throwFeatMissing("beginRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_beginRel);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setBeginRel(int addr, int v) {
        if (featOkTst && casFeat_beginRel == null)
      jcas.throwFeatMissing("beginRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setIntValue(addr, casFeatCode_beginRel, v);}



  /** @generated */
  final Feature casFeat_endRel;
  /** @generated */
  final int     casFeatCode_endRel;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getEndRel(int addr) {
        if (featOkTst && casFeat_endRel == null)
      jcas.throwFeatMissing("endRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_endRel);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setEndRel(int addr, int v) {
        if (featOkTst && casFeat_endRel == null)
      jcas.throwFeatMissing("endRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setIntValue(addr, casFeatCode_endRel, v);}



  /** @generated */
  final Feature casFeat_valueRel;
  /** @generated */
  final int     casFeatCode_valueRel;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getValueRel(int addr) {
        if (featOkTst && casFeat_valueRel == null)
      jcas.throwFeatMissing("valueRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_valueRel);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setValueRel(int addr, String v) {
        if (featOkTst && casFeat_valueRel == null)
      jcas.throwFeatMissing("valueRel", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_valueRel, v);}



  /** @generated */
  final Feature casFeat_beginArg2;
  /** @generated */
  final int     casFeatCode_beginArg2;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getBeginArg2(int addr) {
        if (featOkTst && casFeat_beginArg2 == null)
      jcas.throwFeatMissing("beginArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_beginArg2);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setBeginArg2(int addr, int v) {
        if (featOkTst && casFeat_beginArg2 == null)
      jcas.throwFeatMissing("beginArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setIntValue(addr, casFeatCode_beginArg2, v);}



  /** @generated */
  final Feature casFeat_endArg2;
  /** @generated */
  final int     casFeatCode_endArg2;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public int getEndArg2(int addr) {
        if (featOkTst && casFeat_endArg2 == null)
      jcas.throwFeatMissing("endArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getIntValue(addr, casFeatCode_endArg2);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setEndArg2(int addr, int v) {
        if (featOkTst && casFeat_endArg2 == null)
      jcas.throwFeatMissing("endArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setIntValue(addr, casFeatCode_endArg2, v);}



  /** @generated */
  final Feature casFeat_valueArg2;
  /** @generated */
  final int     casFeatCode_valueArg2;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value
   */
  public String getValueArg2(int addr) {
        if (featOkTst && casFeat_valueArg2 == null)
      jcas.throwFeatMissing("valueArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_valueArg2);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set
   */
  public void setValueArg2(int addr, String v) {
        if (featOkTst && casFeat_valueArg2 == null)
      jcas.throwFeatMissing("valueArg2", "org.hucompute.textimager.uima.type.OpenIERelation");
    ll_cas.ll_setStringValue(addr, casFeatCode_valueArg2, v);}





  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type
	 */
  public OpenIERelation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());


    casFeat_confidence = jcas.getRequiredFeatureDE(casType, "confidence", "uima.cas.Double", featOkTst);
    casFeatCode_confidence  = (null == casFeat_confidence) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_confidence).getCode();


    casFeat_beginArg1 = jcas.getRequiredFeatureDE(casType, "beginArg1", "uima.cas.Integer", featOkTst);
    casFeatCode_beginArg1  = (null == casFeat_beginArg1) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_beginArg1).getCode();


    casFeat_endArg1 = jcas.getRequiredFeatureDE(casType, "endArg1", "uima.cas.Integer", featOkTst);
    casFeatCode_endArg1  = (null == casFeat_endArg1) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_endArg1).getCode();


    casFeat_valueArg1 = jcas.getRequiredFeatureDE(casType, "valueArg1", "uima.cas.String", featOkTst);
    casFeatCode_valueArg1  = (null == casFeat_valueArg1) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_valueArg1).getCode();


    casFeat_beginRel = jcas.getRequiredFeatureDE(casType, "beginRel", "uima.cas.Integer", featOkTst);
    casFeatCode_beginRel  = (null == casFeat_beginRel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_beginRel).getCode();


    casFeat_endRel = jcas.getRequiredFeatureDE(casType, "endRel", "uima.cas.Integer", featOkTst);
    casFeatCode_endRel  = (null == casFeat_endRel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_endRel).getCode();


    casFeat_valueRel = jcas.getRequiredFeatureDE(casType, "valueRel", "uima.cas.String", featOkTst);
    casFeatCode_valueRel  = (null == casFeat_valueRel) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_valueRel).getCode();


    casFeat_beginArg2 = jcas.getRequiredFeatureDE(casType, "beginArg2", "uima.cas.Integer", featOkTst);
    casFeatCode_beginArg2  = (null == casFeat_beginArg2) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_beginArg2).getCode();


    casFeat_endArg2 = jcas.getRequiredFeatureDE(casType, "endArg2", "uima.cas.Integer", featOkTst);
    casFeatCode_endArg2  = (null == casFeat_endArg2) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_endArg2).getCode();


    casFeat_valueArg2 = jcas.getRequiredFeatureDE(casType, "valueArg2", "uima.cas.String", featOkTst);
    casFeatCode_valueArg2  = (null == casFeat_valueArg2) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_valueArg2).getCode();

  }
}



