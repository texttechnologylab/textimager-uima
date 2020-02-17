

/* First created by JCasGen Tue Feb 04 20:02:41 CET 2020 */
package org.texttechnologylab.annotation.ocr;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.StringList;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;


/** 
 * Updated by JCasGen Tue Feb 04 20:02:41 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/TextTechnologyOCR.xml
 * @generated */
public class OCRToken extends Token {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OCRToken.class);
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
  protected OCRToken() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OCRToken(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OCRToken(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OCRToken(JCas jcas, int begin, int end) {
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
  //* Feature: subTokenList

  /** getter for subTokenList - gets 
   * @generated
   * @return value of the feature 
   */
  public StringList getSubTokenList() {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_subTokenList == null)
      jcasType.jcas.throwFeatMissing("subTokenList", "org.texttechnologylab.annotation.ocr.OCRToken");
    return (StringList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((OCRToken_Type)jcasType).casFeatCode_subTokenList)));}
    
  /** setter for subTokenList - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSubTokenList(StringList v) {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_subTokenList == null)
      jcasType.jcas.throwFeatMissing("subTokenList", "org.texttechnologylab.annotation.ocr.OCRToken");
    jcasType.ll_cas.ll_setRefValue(addr, ((OCRToken_Type)jcasType).casFeatCode_subTokenList, jcasType.ll_cas.ll_getFSRef(v));}    
   
    
  //*--------------*
  //* Feature: isWordFromDictionary

  /** getter for isWordFromDictionary - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsWordFromDictionary() {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_isWordFromDictionary == null)
      jcasType.jcas.throwFeatMissing("isWordFromDictionary", "org.texttechnologylab.annotation.ocr.OCRToken");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_isWordFromDictionary);}
    
  /** setter for isWordFromDictionary - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsWordFromDictionary(boolean v) {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_isWordFromDictionary == null)
      jcasType.jcas.throwFeatMissing("isWordFromDictionary", "org.texttechnologylab.annotation.ocr.OCRToken");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_isWordFromDictionary, v);}    
   
    
  //*--------------*
  //* Feature: isWordNormal

  /** getter for isWordNormal - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsWordNormal() {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_isWordNormal == null)
      jcasType.jcas.throwFeatMissing("isWordNormal", "org.texttechnologylab.annotation.ocr.OCRToken");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_isWordNormal);}
    
  /** setter for isWordNormal - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsWordNormal(boolean v) {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_isWordNormal == null)
      jcasType.jcas.throwFeatMissing("isWordNormal", "org.texttechnologylab.annotation.ocr.OCRToken");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_isWordNormal, v);}    
   
    
  //*--------------*
  //* Feature: isWordNumeric

  /** getter for isWordNumeric - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getIsWordNumeric() {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_isWordNumeric == null)
      jcasType.jcas.throwFeatMissing("isWordNumeric", "org.texttechnologylab.annotation.ocr.OCRToken");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_isWordNumeric);}
    
  /** setter for isWordNumeric - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setIsWordNumeric(boolean v) {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_isWordNumeric == null)
      jcasType.jcas.throwFeatMissing("isWordNumeric", "org.texttechnologylab.annotation.ocr.OCRToken");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_isWordNumeric, v);}    
   
    
  //*--------------*
  //* Feature: containsHyphen

  /** getter for containsHyphen - gets 
   * @generated
   * @return value of the feature 
   */
  public boolean getContainsHyphen() {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_containsHyphen == null)
      jcasType.jcas.throwFeatMissing("containsHyphen", "org.texttechnologylab.annotation.ocr.OCRToken");
    return jcasType.ll_cas.ll_getBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_containsHyphen);}
    
  /** setter for containsHyphen - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setContainsHyphen(boolean v) {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_containsHyphen == null)
      jcasType.jcas.throwFeatMissing("containsHyphen", "org.texttechnologylab.annotation.ocr.OCRToken");
    jcasType.ll_cas.ll_setBooleanValue(addr, ((OCRToken_Type)jcasType).casFeatCode_containsHyphen, v);}    
   
    
  //*--------------*
  //* Feature: suspiciousChars

  /** getter for suspiciousChars - gets 
   * @generated
   * @return value of the feature 
   */
  public int getSuspiciousChars() {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_suspiciousChars == null)
      jcasType.jcas.throwFeatMissing("suspiciousChars", "org.texttechnologylab.annotation.ocr.OCRToken");
    return jcasType.ll_cas.ll_getIntValue(addr, ((OCRToken_Type)jcasType).casFeatCode_suspiciousChars);}
    
  /** setter for suspiciousChars - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSuspiciousChars(int v) {
    if (OCRToken_Type.featOkTst && ((OCRToken_Type)jcasType).casFeat_suspiciousChars == null)
      jcasType.jcas.throwFeatMissing("suspiciousChars", "org.texttechnologylab.annotation.ocr.OCRToken");
    jcasType.ll_cas.ll_setIntValue(addr, ((OCRToken_Type)jcasType).casFeatCode_suspiciousChars, v);}    
  }

    