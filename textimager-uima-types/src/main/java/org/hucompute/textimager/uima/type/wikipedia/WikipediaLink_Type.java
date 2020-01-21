
/* First created by JCasGen Tue Jan 21 12:20:14 CET 2020 */
package org.hucompute.textimager.uima.type.wikipedia;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** Wikipedia link
 * Updated by JCasGen Tue Jan 21 12:20:14 CET 2020
 * @generated */
public class WikipediaLink_Type extends de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WikipediaLink.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
 
  /** @generated */
  final Feature casFeat_WikiData;
  /** @generated */
  final int     casFeatCode_WikiData;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getWikiData(int addr) {
        if (featOkTst && casFeat_WikiData == null)
      jcas.throwFeatMissing("WikiData", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    return ll_cas.ll_getStringValue(addr, casFeatCode_WikiData);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setWikiData(int addr, String v) {
        if (featOkTst && casFeat_WikiData == null)
      jcas.throwFeatMissing("WikiData", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    ll_cas.ll_setStringValue(addr, casFeatCode_WikiData, v);}
    
  
 
  /** @generated */
  final Feature casFeat_WikiDataHyponyms;
  /** @generated */
  final int     casFeatCode_WikiDataHyponyms;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getWikiDataHyponyms(int addr) {
        if (featOkTst && casFeat_WikiDataHyponyms == null)
      jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    return ll_cas.ll_getRefValue(addr, casFeatCode_WikiDataHyponyms);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setWikiDataHyponyms(int addr, int v) {
        if (featOkTst && casFeat_WikiDataHyponyms == null)
      jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    ll_cas.ll_setRefValue(addr, casFeatCode_WikiDataHyponyms, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getWikiDataHyponyms(int addr, int i) {
        if (featOkTst && casFeat_WikiDataHyponyms == null)
      jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_WikiDataHyponyms), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_WikiDataHyponyms), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_WikiDataHyponyms), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setWikiDataHyponyms(int addr, int i, String v) {
        if (featOkTst && casFeat_WikiDataHyponyms == null)
      jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_WikiDataHyponyms), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_WikiDataHyponyms), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_WikiDataHyponyms), i, v);
  }
 
 
  /** @generated */
  final Feature casFeat_isInstance;
  /** @generated */
  final int     casFeatCode_isInstance;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIsInstance(int addr) {
        if (featOkTst && casFeat_isInstance == null)
      jcas.throwFeatMissing("isInstance", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_isInstance);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIsInstance(int addr, boolean v) {
        if (featOkTst && casFeat_isInstance == null)
      jcas.throwFeatMissing("isInstance", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_isInstance, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public WikipediaLink_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_WikiData = jcas.getRequiredFeatureDE(casType, "WikiData", "uima.cas.String", featOkTst);
    casFeatCode_WikiData  = (null == casFeat_WikiData) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_WikiData).getCode();

 
    casFeat_WikiDataHyponyms = jcas.getRequiredFeatureDE(casType, "WikiDataHyponyms", "uima.cas.StringArray", featOkTst);
    casFeatCode_WikiDataHyponyms  = (null == casFeat_WikiDataHyponyms) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_WikiDataHyponyms).getCode();

 
    casFeat_isInstance = jcas.getRequiredFeatureDE(casType, "isInstance", "uima.cas.Boolean", featOkTst);
    casFeatCode_isInstance  = (null == casFeat_isInstance) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_isInstance).getCode();

  }
}



    