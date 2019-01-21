
/* First created by JCasGen Mon Jan 21 11:55:57 CET 2019 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Mon Jan 21 11:55:57 CET 2019
 * @generated */
public class WikipediaInformation_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = WikipediaInformation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.WikipediaInformation");
 
  /** @generated */
  final Feature casFeat_pageURL;
  /** @generated */
  final int     casFeatCode_pageURL;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPageURL(int addr) {
        if (featOkTst && casFeat_pageURL == null)
      jcas.throwFeatMissing("pageURL", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pageURL);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPageURL(int addr, String v) {
        if (featOkTst && casFeat_pageURL == null)
      jcas.throwFeatMissing("pageURL", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_pageURL, v);}
    
  
 
  /** @generated */
  final Feature casFeat_revisionID;
  /** @generated */
  final int     casFeatCode_revisionID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getRevisionID(int addr) {
        if (featOkTst && casFeat_revisionID == null)
      jcas.throwFeatMissing("revisionID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_revisionID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setRevisionID(int addr, String v) {
        if (featOkTst && casFeat_revisionID == null)
      jcas.throwFeatMissing("revisionID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_revisionID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_namespaceID;
  /** @generated */
  final int     casFeatCode_namespaceID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNamespaceID(int addr) {
        if (featOkTst && casFeat_namespaceID == null)
      jcas.throwFeatMissing("namespaceID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_namespaceID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNamespaceID(int addr, String v) {
        if (featOkTst && casFeat_namespaceID == null)
      jcas.throwFeatMissing("namespaceID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_namespaceID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_namespace;
  /** @generated */
  final int     casFeatCode_namespace;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNamespace(int addr) {
        if (featOkTst && casFeat_namespace == null)
      jcas.throwFeatMissing("namespace", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_namespace);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNamespace(int addr, String v) {
        if (featOkTst && casFeat_namespace == null)
      jcas.throwFeatMissing("namespace", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_namespace, v);}
    
  
 
  /** @generated */
  final Feature casFeat_timestamp;
  /** @generated */
  final int     casFeatCode_timestamp;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTimestamp(int addr) {
        if (featOkTst && casFeat_timestamp == null)
      jcas.throwFeatMissing("timestamp", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_timestamp);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTimestamp(int addr, String v) {
        if (featOkTst && casFeat_timestamp == null)
      jcas.throwFeatMissing("timestamp", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_timestamp, v);}
    
  
 
  /** @generated */
  final Feature casFeat_title;
  /** @generated */
  final int     casFeatCode_title;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTitle(int addr) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_title);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTitle(int addr, String v) {
        if (featOkTst && casFeat_title == null)
      jcas.throwFeatMissing("title", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_title, v);}
    
  
 
  /** @generated */
  final Feature casFeat_pageID;
  /** @generated */
  final int     casFeatCode_pageID;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getPageID(int addr) {
        if (featOkTst && casFeat_pageID == null)
      jcas.throwFeatMissing("pageID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_pageID);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setPageID(int addr, String v) {
        if (featOkTst && casFeat_pageID == null)
      jcas.throwFeatMissing("pageID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setStringValue(addr, casFeatCode_pageID, v);}
    
  
 
  /** @generated */
  final Feature casFeat_categories;
  /** @generated */
  final int     casFeatCode_categories;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public int getCategories(int addr) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return ll_cas.ll_getRefValue(addr, casFeatCode_categories);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setCategories(int addr, int v) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    ll_cas.ll_setRefValue(addr, casFeatCode_categories, v);}
    
   /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @return value at index i in the array 
   */
  public String getCategories(int addr, int i) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    if (lowLevelTypeChecks)
      return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i);
	return ll_cas.ll_getStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i);
  }
   
  /** @generated
   * @param addr low level Feature Structure reference
   * @param i index of item in the array
   * @param v value to set
   */ 
  public void setCategories(int addr, int i, String v) {
        if (featOkTst && casFeat_categories == null)
      jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    if (lowLevelTypeChecks)
      ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i, v, true);
    jcas.checkArrayBounds(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i);
    ll_cas.ll_setStringArrayValue(ll_cas.ll_getRefValue(addr, casFeatCode_categories), i, v);
  }
 



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public WikipediaInformation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_pageURL = jcas.getRequiredFeatureDE(casType, "pageURL", "uima.cas.String", featOkTst);
    casFeatCode_pageURL  = (null == casFeat_pageURL) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pageURL).getCode();

 
    casFeat_revisionID = jcas.getRequiredFeatureDE(casType, "revisionID", "uima.cas.String", featOkTst);
    casFeatCode_revisionID  = (null == casFeat_revisionID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_revisionID).getCode();

 
    casFeat_namespaceID = jcas.getRequiredFeatureDE(casType, "namespaceID", "uima.cas.String", featOkTst);
    casFeatCode_namespaceID  = (null == casFeat_namespaceID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_namespaceID).getCode();

 
    casFeat_namespace = jcas.getRequiredFeatureDE(casType, "namespace", "uima.cas.String", featOkTst);
    casFeatCode_namespace  = (null == casFeat_namespace) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_namespace).getCode();

 
    casFeat_timestamp = jcas.getRequiredFeatureDE(casType, "timestamp", "uima.cas.String", featOkTst);
    casFeatCode_timestamp  = (null == casFeat_timestamp) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_timestamp).getCode();

 
    casFeat_title = jcas.getRequiredFeatureDE(casType, "title", "uima.cas.String", featOkTst);
    casFeatCode_title  = (null == casFeat_title) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_title).getCode();

 
    casFeat_pageID = jcas.getRequiredFeatureDE(casType, "pageID", "uima.cas.String", featOkTst);
    casFeatCode_pageID  = (null == casFeat_pageID) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_pageID).getCode();

 
    casFeat_categories = jcas.getRequiredFeatureDE(casType, "categories", "uima.cas.StringArray", featOkTst);
    casFeatCode_categories  = (null == casFeat_categories) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_categories).getCode();

  }
}



    