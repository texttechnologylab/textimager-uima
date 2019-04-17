

/* First created by JCasGen Thu Oct 12 17:21:27 CEST 2017 */
package org.hucompute.textimager.uima.type.wikipedia;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringArray;


/** Wikipedia link
 * Updated by JCasGen Tue Apr 16 12:25:25 CEST 2019
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/wikipediaLink.xml
 * @generated */
public class WikipediaLink extends de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WikipediaLink.class);
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
  protected WikipediaLink() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public WikipediaLink(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public WikipediaLink(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public WikipediaLink(JCas jcas, int begin, int end) {
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
  //* Feature: WikiData

  /** getter for WikiData - gets WikiData Id
   * @generated
   * @return value of the feature 
   */
  public String getWikiData() {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_WikiData == null)
      jcasType.jcas.throwFeatMissing("WikiData", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiData);}
    
  /** setter for WikiData - sets WikiData Id 
   * @generated
   * @param v value to set into the feature 
   */
  public void setWikiData(String v) {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_WikiData == null)
      jcasType.jcas.throwFeatMissing("WikiData", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiData, v);}    
   
    
  //*--------------*
  //* Feature: WikiDataHyponyms

  /** getter for WikiDataHyponyms - gets WikiData Hyponyms
   * @generated
   * @return value of the feature 
   */
  public StringArray getWikiDataHyponyms() {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_WikiDataHyponyms == null)
      jcasType.jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    return (StringArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiDataHyponyms)));}
    
  /** setter for WikiDataHyponyms - sets WikiData Hyponyms 
   * @generated
   * @param v value to set into the feature 
   */
  public void setWikiDataHyponyms(StringArray v) {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_WikiDataHyponyms == null)
      jcasType.jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    jcasType.ll_cas.ll_setRefValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiDataHyponyms, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for WikiDataHyponyms - gets an indexed value - WikiData Hyponyms
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getWikiDataHyponyms(int i) {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_WikiDataHyponyms == null)
      jcasType.jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiDataHyponyms), i);
    return jcasType.ll_cas.ll_getStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiDataHyponyms), i);}

  /** indexed setter for WikiDataHyponyms - sets an indexed value - WikiData Hyponyms
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setWikiDataHyponyms(int i, String v) { 
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_WikiDataHyponyms == null)
      jcasType.jcas.throwFeatMissing("WikiDataHyponyms", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiDataHyponyms), i);
    jcasType.ll_cas.ll_setStringArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_WikiDataHyponyms), i, v);}
   
    
  //*--------------*
  //* Feature: isInstance

  /** getter for isInstance - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsInstance() {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_isInstance == null)
      jcasType.jcas.throwFeatMissing("isInstance", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_isInstance);}
    
  /** setter for isInstance - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsInstance(boolean v) {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_isInstance == null)
      jcasType.jcas.throwFeatMissing("isInstance", "org.hucompute.textimager.uima.type.wikipedia.WikipediaLink");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_isInstance, v);}    
  }

    