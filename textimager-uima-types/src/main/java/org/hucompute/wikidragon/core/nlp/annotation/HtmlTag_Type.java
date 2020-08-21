
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
public class HtmlTag_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = HtmlTag.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
 
  /** @generated */
  final Feature casFeat_tag;
  /** @generated */
  final int     casFeatCode_tag;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTag(int addr) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tag);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTag(int addr, String v) {
        if (featOkTst && casFeat_tag == null)
      jcas.throwFeatMissing("tag", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    ll_cas.ll_setStringValue(addr, casFeatCode_tag, v);}
    
  
 
  /** @generated */
  final Feature casFeat_attr;
  /** @generated */
  final int     casFeatCode_attr;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAttr(int addr) {
        if (featOkTst && casFeat_attr == null)
      jcas.throwFeatMissing("attr", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return ll_cas.ll_getStringValue(addr, casFeatCode_attr);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAttr(int addr, String v) {
        if (featOkTst && casFeat_attr == null)
      jcas.throwFeatMissing("attr", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    ll_cas.ll_setStringValue(addr, casFeatCode_attr, v);}
    
  
 
  /** @generated */
  final Feature casFeat_depth;
  /** @generated */
  final int     casFeatCode_depth;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getDepth(int addr) {
        if (featOkTst && casFeat_depth == null)
      jcas.throwFeatMissing("depth", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return ll_cas.ll_getIntValue(addr, casFeatCode_depth);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDepth(int addr, int v) {
        if (featOkTst && casFeat_depth == null)
      jcas.throwFeatMissing("depth", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    ll_cas.ll_setIntValue(addr, casFeatCode_depth, v);}
    
  
 
  /** @generated */
  final Feature casFeat_order;
  /** @generated */
  final int     casFeatCode_order;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getOrder(int addr) {
        if (featOkTst && casFeat_order == null)
      jcas.throwFeatMissing("order", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    return ll_cas.ll_getIntValue(addr, casFeatCode_order);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setOrder(int addr, int v) {
        if (featOkTst && casFeat_order == null)
      jcas.throwFeatMissing("order", "org.hucompute.wikidragon.core.nlp.annotation.HtmlTag");
    ll_cas.ll_setIntValue(addr, casFeatCode_order, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public HtmlTag_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_tag = jcas.getRequiredFeatureDE(casType, "tag", "uima.cas.String", featOkTst);
    casFeatCode_tag  = (null == casFeat_tag) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tag).getCode();

 
    casFeat_attr = jcas.getRequiredFeatureDE(casType, "attr", "uima.cas.String", featOkTst);
    casFeatCode_attr  = (null == casFeat_attr) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_attr).getCode();

 
    casFeat_depth = jcas.getRequiredFeatureDE(casType, "depth", "uima.cas.Integer", featOkTst);
    casFeatCode_depth  = (null == casFeat_depth) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_depth).getCode();

 
    casFeat_order = jcas.getRequiredFeatureDE(casType, "order", "uima.cas.Integer", featOkTst);
    casFeatCode_order  = (null == casFeat_order) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_order).getCode();

  }
}



    