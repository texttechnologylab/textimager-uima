

/* First created by JCasGen Tue Feb 04 20:02:41 CET 2020 */
package org.texttechnologylab.annotation.ocr;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** 
 * Updated by JCasGen Tue Feb 04 20:02:41 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/TextTechnologyOCR.xml
 * @generated */
public class OCRDocument extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(OCRDocument.class);
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
  protected OCRDocument() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public OCRDocument(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public OCRDocument(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public OCRDocument(JCas jcas, int begin, int end) {
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
  //* Feature: documentname

  /** getter for documentname - gets 
   * @generated
   * @return value of the feature 
   */
  public String getDocumentname() {
    if (OCRDocument_Type.featOkTst && ((OCRDocument_Type)jcasType).casFeat_documentname == null)
      jcasType.jcas.throwFeatMissing("documentname", "org.texttechnologylab.annotation.ocr.OCRDocument");
    return jcasType.ll_cas.ll_getStringValue(addr, ((OCRDocument_Type)jcasType).casFeatCode_documentname);}
    
  /** setter for documentname - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentname(String v) {
    if (OCRDocument_Type.featOkTst && ((OCRDocument_Type)jcasType).casFeat_documentname == null)
      jcasType.jcas.throwFeatMissing("documentname", "org.texttechnologylab.annotation.ocr.OCRDocument");
    jcasType.ll_cas.ll_setStringValue(addr, ((OCRDocument_Type)jcasType).casFeatCode_documentname, v);}    
  }

    