

/* First created by JCasGen Tue Jan 21 12:16:52 CET 2020 */
package org.hucompute.textimager.uima.type.wikidata;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Jan 21 12:16:52 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/WikiDataHyponym.xml
 * @generated */
public class WikiDataHyponym extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(WikiDataHyponym.class);
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
  protected WikiDataHyponym() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public WikiDataHyponym(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public WikiDataHyponym(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public WikiDataHyponym(JCas jcas, int begin, int end) {
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
  //* Feature: id

  /** getter for id - gets start and end of the objects
   * @generated
   * @return value of the feature 
   */
  public String getId() {
    if (WikiDataHyponym_Type.featOkTst && ((WikiDataHyponym_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikiDataHyponym_Type)jcasType).casFeatCode_id);}
    
  /** setter for id - sets start and end of the objects 
   * @generated
   * @param v value to set into the feature 
   */
  public void setId(String v) {
    if (WikiDataHyponym_Type.featOkTst && ((WikiDataHyponym_Type)jcasType).casFeat_id == null)
      jcasType.jcas.throwFeatMissing("id", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikiDataHyponym_Type)jcasType).casFeatCode_id, v);}    
   
    
  //*--------------*
  //* Feature: typ

  /** getter for typ - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTyp() {
    if (WikiDataHyponym_Type.featOkTst && ((WikiDataHyponym_Type)jcasType).casFeat_typ == null)
      jcasType.jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikiDataHyponym_Type)jcasType).casFeatCode_typ);}
    
  /** setter for typ - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTyp(String v) {
    if (WikiDataHyponym_Type.featOkTst && ((WikiDataHyponym_Type)jcasType).casFeat_typ == null)
      jcasType.jcas.throwFeatMissing("typ", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikiDataHyponym_Type)jcasType).casFeatCode_typ, v);}    
   
    
  //*--------------*
  //* Feature: depth

  /** getter for depth - gets 
   * @generated
   * @return value of the feature 
   */
  public int getDepth() {
    if (WikiDataHyponym_Type.featOkTst && ((WikiDataHyponym_Type)jcasType).casFeat_depth == null)
      jcasType.jcas.throwFeatMissing("depth", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    return jcasType.ll_cas.ll_getIntValue(addr, ((WikiDataHyponym_Type)jcasType).casFeatCode_depth);}
    
  /** setter for depth - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDepth(int v) {
    if (WikiDataHyponym_Type.featOkTst && ((WikiDataHyponym_Type)jcasType).casFeat_depth == null)
      jcasType.jcas.throwFeatMissing("depth", "org.hucompute.textimager.uima.type.wikidata.WikiDataHyponym");
    jcasType.ll_cas.ll_setIntValue(addr, ((WikiDataHyponym_Type)jcasType).casFeatCode_depth, v);}    
  }

    