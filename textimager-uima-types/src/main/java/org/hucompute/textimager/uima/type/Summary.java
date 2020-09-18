

/* First created by JCasGen Fri Sep 18 11:31:56 CEST 2020 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Fri Sep 18 11:31:56 CEST 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/Summary.xml
 * @generated */
public class Summary extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Summary.class);
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
  protected Summary() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Summary(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Summary(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Summary(JCas jcas, int begin, int end) {
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
  //* Feature: summary

  /** getter for summary - gets 
   * @generated
   * @return value of the feature 
   */
  public String getSummary() {
    if (Summary_Type.featOkTst && ((Summary_Type)jcasType).casFeat_summary == null)
      jcasType.jcas.throwFeatMissing("summary", "org.hucompute.textimager.uima.type.Summary");
    return jcasType.ll_cas.ll_getStringValue(addr, ((Summary_Type)jcasType).casFeatCode_summary);}
    
  /** setter for summary - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSummary(String v) {
    if (Summary_Type.featOkTst && ((Summary_Type)jcasType).casFeat_summary == null)
      jcasType.jcas.throwFeatMissing("summary", "org.hucompute.textimager.uima.type.Summary");
    jcasType.ll_cas.ll_setStringValue(addr, ((Summary_Type)jcasType).casFeatCode_summary, v);}    
  }

    