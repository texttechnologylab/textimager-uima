

/* First created by JCasGen Tue Jan 21 11:16:28 CET 2020 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/**
 * Updated by JCasGen Tue Jan 21 11:16:28 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/LanguageTypeSystem.xml
 * @generated */
public class Language extends Annotation {
  /** @generated
   * @ordered
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Language.class);
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
  protected Language() {/* intentionally empty block */}

  /** Internal - constructor used by generator
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure
   */
  public Language(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   */
  public Language(JCas jcas) {
    super(jcas);
    readObject();
  }

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
  */
  public Language(JCas jcas, int begin, int end) {
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
  //* Feature: language

  /** getter for language - gets
   * @generated
   * @return value of the feature
   */
  public String getLanguage() {
    if (Language_Type.featOkTst && ((Language_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "org.hucompute.textimager.uima.type.Language");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Language_Type)jcasType).casFeatCode_language);}

  /** setter for language - sets
   * @generated
   * @param v value to set into the feature
   */
  public void setLanguage(String v) {
    if (Language_Type.featOkTst && ((Language_Type)jcasType).casFeat_language == null)
      jcasType.jcas.throwFeatMissing("language", "org.hucompute.textimager.uima.type.Language");
    jcasType.ll_cas.ll_setStringValue(addr, ((Language_Type)jcasType).casFeatCode_language, v);}
  }

