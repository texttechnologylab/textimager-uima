
/* First created by JCasGen Thu Oct 12 17:21:27 CEST 2017 */
package de.tudarmstadt.ukp.dkpro.core.io.jwpl.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Wikipedia link
 * Updated by JCasGen Tue Apr 16 12:25:25 CEST 2019
 * @generated */
public class WikipediaLink_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WikipediaLink.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
 
  /** @generated */
  final Feature casFeat_LinkType;
  /** @generated */
  final int     casFeatCode_LinkType;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getLinkType(int addr) {
        if (featOkTst && casFeat_LinkType == null)
      jcas.throwFeatMissing("LinkType", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_LinkType);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setLinkType(int addr, String v) {
        if (featOkTst && casFeat_LinkType == null)
      jcas.throwFeatMissing("LinkType", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_LinkType, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Target;
  /** @generated */
  final int     casFeatCode_Target;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTarget(int addr) {
        if (featOkTst && casFeat_Target == null)
      jcas.throwFeatMissing("Target", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Target);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTarget(int addr, String v) {
        if (featOkTst && casFeat_Target == null)
      jcas.throwFeatMissing("Target", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_Target, v);}
    
  
 
  /** @generated */
  final Feature casFeat_Anchor;
  /** @generated */
  final int     casFeatCode_Anchor;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getAnchor(int addr) {
        if (featOkTst && casFeat_Anchor == null)
      jcas.throwFeatMissing("Anchor", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_Anchor);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setAnchor(int addr, String v) {
        if (featOkTst && casFeat_Anchor == null)
      jcas.throwFeatMissing("Anchor", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_Anchor, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public WikipediaLink_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_LinkType = jcas.getRequiredFeatureDE(casType, "LinkType", "uima.cas.String", featOkTst);
    casFeatCode_LinkType  = (null == casFeat_LinkType) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_LinkType).getCode();

 
    casFeat_Target = jcas.getRequiredFeatureDE(casType, "Target", "uima.cas.String", featOkTst);
    casFeatCode_Target  = (null == casFeat_Target) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Target).getCode();

 
    casFeat_Anchor = jcas.getRequiredFeatureDE(casType, "Anchor", "uima.cas.String", featOkTst);
    casFeatCode_Anchor  = (null == casFeat_Anchor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_Anchor).getCode();

  }
}



    