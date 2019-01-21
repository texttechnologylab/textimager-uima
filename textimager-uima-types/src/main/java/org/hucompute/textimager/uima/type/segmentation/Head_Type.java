
/* First created by JCasGen Mon Jan 21 11:58:25 CET 2019 */
package org.hucompute.textimager.uima.type.segmentation;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Jan 21 11:58:25 CET 2019
 * @generated */
public class Head_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Head.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.segmentation.Head");
 
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
      jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.segmentation.Head");
    return ll_cas.ll_getStringValue(addr, casFeatCode_typ);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTyp(int addr, String v) {
        if (featOkTst && casFeat_typ == null)
      jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.segmentation.Head");
    ll_cas.ll_setStringValue(addr, casFeatCode_typ, v);}
    
  
 
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
      jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.segmentation.Head");
    return ll_cas.ll_getStringValue(addr, casFeatCode_id);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setId(int addr, String v) {
        if (featOkTst && casFeat_id == null)
      jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.segmentation.Head");
    ll_cas.ll_setStringValue(addr, casFeatCode_id, v);}
    
  
 
  /** @generated */
  final Feature casFeat_parent;
  /** @generated */
  final int     casFeatCode_parent;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getParent(int addr) {
        if (featOkTst && casFeat_parent == null)
      jcas.throwFeatMissing("parent", "org.hucompute.textimager.uima.type.segmentation.Head");
    return ll_cas.ll_getStringValue(addr, casFeatCode_parent);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setParent(int addr, String v) {
        if (featOkTst && casFeat_parent == null)
      jcas.throwFeatMissing("parent", "org.hucompute.textimager.uima.type.segmentation.Head");
    ll_cas.ll_setStringValue(addr, casFeatCode_parent, v);}
    
  
 
  /** @generated */
  final Feature casFeat_rootEntries;
  /** @generated */
  final int     casFeatCode_rootEntries;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRootEntries(int addr) {
        if (featOkTst && casFeat_rootEntries == null)
      jcas.throwFeatMissing("rootEntries", "org.hucompute.textimager.uima.type.segmentation.Head");
    return ll_cas.ll_getStringValue(addr, casFeatCode_rootEntries);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRootEntries(int addr, String v) {
        if (featOkTst && casFeat_rootEntries == null)
      jcas.throwFeatMissing("rootEntries", "org.hucompute.textimager.uima.type.segmentation.Head");
    ll_cas.ll_setStringValue(addr, casFeatCode_rootEntries, v);}
    
  
 
  /** @generated */
  final Feature casFeat_children;
  /** @generated */
  final int     casFeatCode_children;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getChildren(int addr) {
        if (featOkTst && casFeat_children == null)
      jcas.throwFeatMissing("children", "org.hucompute.textimager.uima.type.segmentation.Head");
    return ll_cas.ll_getStringValue(addr, casFeatCode_children);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setChildren(int addr, String v) {
        if (featOkTst && casFeat_children == null)
      jcas.throwFeatMissing("children", "org.hucompute.textimager.uima.type.segmentation.Head");
    ll_cas.ll_setStringValue(addr, casFeatCode_children, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Head_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_typ = jcas.getRequiredFeatureDE(casType, "typ", "uima.cas.String", featOkTst);
    casFeatCode_typ  = (null == casFeat_typ) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_typ).getCode();

 
    casFeat_id = jcas.getRequiredFeatureDE(casType, "id", "uima.cas.String", featOkTst);
    casFeatCode_id  = (null == casFeat_id) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_id).getCode();

 
    casFeat_parent = jcas.getRequiredFeatureDE(casType, "parent", "uima.cas.String", featOkTst);
    casFeatCode_parent  = (null == casFeat_parent) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_parent).getCode();

 
    casFeat_rootEntries = jcas.getRequiredFeatureDE(casType, "rootEntries", "uima.cas.String", featOkTst);
    casFeatCode_rootEntries  = (null == casFeat_rootEntries) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_rootEntries).getCode();

 
    casFeat_children = jcas.getRequiredFeatureDE(casType, "children", "uima.cas.String", featOkTst);
    casFeatCode_children  = (null == casFeat_children) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_children).getCode();

  }
}



    