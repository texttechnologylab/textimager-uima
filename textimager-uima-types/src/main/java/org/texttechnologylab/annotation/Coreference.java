

/* First created by JCasGen Wed Jan 22 14:27:43 CET 2020 */
package org.texttechnologylab.annotation;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Wed Jan 22 14:27:43 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/TextTechnologyTypes.xml
 * @generated */
public class Coreference extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(Coreference.class);
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
  protected Coreference() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public Coreference(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public Coreference(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public Coreference(JCas jcas, int begin, int end) {
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
  //* Feature: link

  /** getter for link - gets 
   * @generated
   * @return value of the feature 
   */
  public Coreference getLink() {
    if (Coreference_Type.featOkTst && ((Coreference_Type)jcasType).casFeat_link == null)
      jcasType.jcas.throwFeatMissing("link", "org.texttechnologylab.annotation.Coreference");
    return (Coreference)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((Coreference_Type)jcasType).casFeatCode_link)));}
    
  /** setter for link - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setLink(Coreference v) {
    if (Coreference_Type.featOkTst && ((Coreference_Type)jcasType).casFeat_link == null)
      jcasType.jcas.throwFeatMissing("link", "org.texttechnologylab.annotation.Coreference");
    jcasType.ll_cas.ll_setRefValue(addr, ((Coreference_Type)jcasType).casFeatCode_link, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    