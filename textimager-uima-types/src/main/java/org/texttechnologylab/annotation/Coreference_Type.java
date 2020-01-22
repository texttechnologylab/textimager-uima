
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
public class Coreference_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Coreference.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.Coreference");
 
  /** @generated */
  final Feature casFeat_link;
  /** @generated */
  final int     casFeatCode_link;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getLink(int addr) {
        if (featOkTst && casFeat_link == null)
      jcas.throwFeatMissing("link", "org.texttechnologylab.annotation.Coreference");
    return ll_cas.ll_getRefValue(addr, casFeatCode_link);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLink(int addr, int v) {
        if (featOkTst && casFeat_link == null)
      jcas.throwFeatMissing("link", "org.texttechnologylab.annotation.Coreference");
    ll_cas.ll_setRefValue(addr, casFeatCode_link, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Coreference_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_link = jcas.getRequiredFeatureDE(casType, "link", "org.texttechnologylab.annotation.Coreference", featOkTst);
    casFeatCode_link  = (null == casFeat_link) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_link).getCode();

  }
}



    