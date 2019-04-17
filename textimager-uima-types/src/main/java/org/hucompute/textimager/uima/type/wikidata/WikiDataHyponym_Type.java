
/* First created by JCasGen Thu Mar 14 17:32:38 CET 2019 */
package org.hucompute.textimager.uima.type.wikidata;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Tue Apr 16 12:25:39 CEST 2019
 * @generated */
public class WikiDataHyponym_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WikiDataHyponym.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
 
  /** @generated */
  final Feature casFeat_id;
  /** @generated */
  final int     casFeatCode_id;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getId(int addr) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_typ;
  /** @generated */
  final int     casFeatCode_typ;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTyp(int addr) {
        if (featOkTst && casFeat_typ == null)
      jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    return ll_cas.ll_getStringValue(addr, casFeatCode_typ);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTyp(int addr, String v) {
        if (featOkTst && casFeat_typ == null)
      jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    ll_cas.ll_setStringValue(addr, casFeatCode_typ, v);}
    
  
 
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
      jcas.throwFeatMissing("depth", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    return ll_cas.ll_getIntValue(addr, casFeatCode_depth);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDepth(int addr, int v) {
        if (featOkTst && casFeat_depth == null)
      jcas.throwFeatMissing("depth", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    ll_cas.ll_setIntValue(addr, casFeatCode_depth, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public WikiDataHyponym_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_typ = jcas.getRequiredFeatureDE(casType, "typ", "uima.cas.String", featOkTst);
    casFeatCode_typ  = (null == casFeat_typ) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_typ).getCode();

 
    casFeat_depth = jcas.getRequiredFeatureDE(casType, "depth", "uima.cas.Integer", featOkTst);
    casFeatCode_depth  = (null == casFeat_depth) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_depth).getCode();

  }
}



    