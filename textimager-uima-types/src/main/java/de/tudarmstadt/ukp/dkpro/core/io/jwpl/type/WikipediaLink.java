

/* First created by JCasGen Tue Jan 21 12:20:14 CET 2020 */
package de.tudarmstadt.ukp.dkpro.core.io.jwpl.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Wikipedia link
 * Updated by JCasGen Tue Jan 21 12:20:14 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/wikipediaLink.xml
 * @generated */
public class WikipediaLink extends Annotation {
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
  //* Feature: LinkType

  /** getter for LinkType - gets The type of the link, e.g. internal, external, image, ...
   * @generated
   * @return value of the feature 
   */
  public String getLinkType() {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_LinkType == null)
      jcasType.jcas.throwFeatMissing("LinkType", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_LinkType);}
    
  /** setter for LinkType - sets The type of the link, e.g. internal, external, image, ... 
   * @generated
   * @param v value to set into the feature 
   */
  public void setLinkType(String v) {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_LinkType == null)
      jcasType.jcas.throwFeatMissing("LinkType", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_LinkType, v);}    
   
    
  //*--------------*
  //* Feature: Target

  /** getter for Target - gets The link target url
   * @generated
   * @return value of the feature 
   */
  public String getTarget() {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_Target);}
    
  /** setter for Target - sets The link target url 
   * @generated
   * @param v value to set into the feature 
   */
  public void setTarget(String v) {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_Target == null)
      jcasType.jcas.throwFeatMissing("Target", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_Target, v);}    
   
    
  //*--------------*
  //* Feature: Anchor

  /** getter for Anchor - gets The anchor of the link
   * @generated
   * @return value of the feature 
   */
  public String getAnchor() {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_Anchor == null)
      jcasType.jcas.throwFeatMissing("Anchor", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    return jcasType.ll_cas.ll_getStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_Anchor);}
    
  /** setter for Anchor - sets The anchor of the link 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnchor(String v) {
    if (WikipediaLink_Type.featOkTst && ((WikipediaLink_Type)jcasType).casFeat_Anchor == null)
      jcasType.jcas.throwFeatMissing("Anchor", "de.tudarmstadt.ukp.dkpro.core.io.jwpl.type.WikipediaLink");
    jcasType.ll_cas.ll_setStringValue(addr, ((WikipediaLink_Type)jcasType).casFeatCode_Anchor, v);}    
  }

    