
/* First created by JCasGen Wed Jan 22 14:27:43 CET 2020 */
package org.texttechnologylab.annotation;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Wed Jan 22 14:27:43 CET 2020
 * @generated */
public class AbstractNamedEntity_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = AbstractNamedEntity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.AbstractNamedEntity");
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "org.texttechnologylab.annotation.AbstractNamedEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_specific;
  /** @generated */
  final int     casFeatCode_specific;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getSpecific(int addr) {
        if (featOkTst && casFeat_specific == null)
      jcas.throwFeatMissing("specific", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_specific);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setSpecific(int addr, boolean v) {
        if (featOkTst && casFeat_specific == null)
      jcas.throwFeatMissing("specific", "org.texttechnologylab.annotation.AbstractNamedEntity");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_specific, v);}
    
  
 
  /** @generated */
  final Feature casFeat_metaphor;
  /** @generated */
  final int     casFeatCode_metaphor;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getMetaphor(int addr) {
        if (featOkTst && casFeat_metaphor == null)
      jcas.throwFeatMissing("metaphor", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_metaphor);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMetaphor(int addr, boolean v) {
        if (featOkTst && casFeat_metaphor == null)
      jcas.throwFeatMissing("metaphor", "org.texttechnologylab.annotation.AbstractNamedEntity");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_metaphor, v);}
    
  
 
  /** @generated */
  final Feature casFeat_metonym;
  /** @generated */
  final int     casFeatCode_metonym;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getMetonym(int addr) {
        if (featOkTst && casFeat_metonym == null)
      jcas.throwFeatMissing("metonym", "org.texttechnologylab.annotation.AbstractNamedEntity");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_metonym);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMetonym(int addr, boolean v) {
        if (featOkTst && casFeat_metonym == null)
      jcas.throwFeatMissing("metonym", "org.texttechnologylab.annotation.AbstractNamedEntity");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_metonym, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public AbstractNamedEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_specific = jcas.getRequiredFeatureDE(casType, "specific", "uima.cas.Boolean", featOkTst);
    casFeatCode_specific  = (null == casFeat_specific) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_specific).getCode();

 
    casFeat_metaphor = jcas.getRequiredFeatureDE(casType, "metaphor", "uima.cas.Boolean", featOkTst);
    casFeatCode_metaphor  = (null == casFeat_metaphor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_metaphor).getCode();

 
    casFeat_metonym = jcas.getRequiredFeatureDE(casType, "metonym", "uima.cas.Boolean", featOkTst);
    casFeatCode_metonym  = (null == casFeat_metonym) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_metonym).getCode();

  }
}



    