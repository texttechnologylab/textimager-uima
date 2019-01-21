

/* First created by JCasGen Mon Jan 21 11:55:57 CET 2019 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Mon Jan 21 11:55:57 CET 2019
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/WikipediaInformation.xml
 * @generated */
public class WikipediaInformation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WikipediaInformation.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected WikipediaInformation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public WikipediaInformation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public WikipediaInformation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public WikipediaInformation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: pageURL

  /** getter for pageURL - gets Description of the Synset
   * @generated
   * @return value of the feature 
   */
  public String getPageURL() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_pageURL == null)
      jcasType.jcas.throwFeatMissing("pageURL", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_pageURL);}
    
  /** setter for pageURL - sets Description of the Synset 
   * @generated
   * @param v value to set into the feature 
   */
  public void setPageURL(String v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_pageURL == null)
      jcasType.jcas.throwFeatMissing("pageURL", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_pageURL, v);}    
   
    
  //*--------------*
  //* Feature: revisionID

  /** getter for revisionID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getRevisionID() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_revisionID == null)
      jcasType.jcas.throwFeatMissing("revisionID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_revisionID);}
    
  /** setter for revisionID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setRevisionID(String v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_revisionID == null)
      jcasType.jcas.throwFeatMissing("revisionID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_revisionID, v);}    
   
    
  //*--------------*
  //* Feature: namespaceID

  /** getter for namespaceID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNamespaceID() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_namespaceID == null)
      jcasType.jcas.throwFeatMissing("namespaceID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_namespaceID);}
    
  /** setter for namespaceID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNamespaceID(String v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_namespaceID == null)
      jcasType.jcas.throwFeatMissing("namespaceID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_namespaceID, v);}    
   
    
  //*--------------*
  //* Feature: namespace

  /** getter for namespace - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNamespace() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_namespace == null)
      jcasType.jcas.throwFeatMissing("namespace", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_namespace);}
    
  /** setter for namespace - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNamespace(String v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_namespace == null)
      jcasType.jcas.throwFeatMissing("namespace", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_namespace, v);}    
   
    
  //*--------------*
  //* Feature: timestamp

  /** getter for timestamp - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTimestamp() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_timestamp);}
    
  /** setter for timestamp - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTimestamp(String v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_timestamp == null)
      jcasType.jcas.throwFeatMissing("timestamp", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_timestamp, v);}    
   
    
  //*--------------*
  //* Feature: title

  /** getter for title - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTitle() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_title);}
    
  /** setter for title - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTitle(String v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_title == null)
      jcasType.jcas.throwFeatMissing("title", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_title, v);}    
   
    
  //*--------------*
  //* Feature: pageID

  /** getter for pageID - gets 
   * @generated
   * @return value of the feature 
   */
  public String getPageID() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_pageID == null)
      jcasType.jcas.throwFeatMissing("pageID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_pageID);}
    
  /** setter for pageID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setPageID(String v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_pageID == null)
      jcasType.jcas.throwFeatMissing("pageID", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_pageID, v);}    
   
    
  //*--------------*
  //* Feature: categories

  /** getter for categories - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getCategories() {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_categories)));}
    
  /** setter for categories - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setCategories(StringArray v) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.ll_cas.ll_setRefValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_categories, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for categories - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getCategories(int i) {
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_categories), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_categories), i);}

  /** indexed setter for categories - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setCategories(int i, String v) { 
    if (WikipediaInformation_Type.featOkTst && ((WikipediaInformation_Type)jcasType).casFeat_categories == null)
      jcasType.jcas.throwFeatMissing("categories", "org.hucompute.textimager.uima.type.WikipediaInformation");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_categories), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaInformation_Type)jcasType).casFeatCode_categories), i, v);}
  }

    