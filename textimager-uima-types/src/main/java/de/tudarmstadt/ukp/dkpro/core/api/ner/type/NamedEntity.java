

/* First created by JCasGen Wed Jan 22 14:27:43 CET 2020 */
package de.tudarmstadt.ukp.dkpro.core.api.ner.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;


/** Named entities refer e.g. to persons, locations, organizations and so on. They often consist of multiple tokens.
 * Updated by JCasGen Wed Jan 22 14:27:43 CET 2020
 * XML source: /home/ahemati/workspaceGitNew/textimager-uima/textimager-uima-types/src/main/resources/desc/type/TextTechnologyTypes.xml
 * @generated */
public class NamedEntity extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(NamedEntity.class);
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
  protected NamedEntity() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public NamedEntity(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public NamedEntity(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public NamedEntity(JCas jcas, int begin, int end) {
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
  //* Feature: value

  /** getter for value - gets The class/category of the named entity, e.g. person, location, etc.
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (NamedEntity_Type.featOkTst && ((NamedEntity_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NamedEntity_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets The class/category of the named entity, e.g. person, location, etc. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (NamedEntity_Type.featOkTst && ((NamedEntity_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    jcasType.ll_cas.ll_setStringValue(addr, ((NamedEntity_Type)jcasType).casFeatCode_value, v);}    
   
    
  //*--------------*
  //* Feature: identifier

  /** getter for identifier - gets Identifier of the named entity, e.g. a reference into a person database.
   * @generated
   * @return value of the feature 
   */
  public String getIdentifier() {
    if (NamedEntity_Type.featOkTst && ((NamedEntity_Type)jcasType).casFeat_identifier == null)
      jcasType.jcas.throwFeatMissing("identifier", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    return jcasType.ll_cas.ll_getStringValue(addr, ((NamedEntity_Type)jcasType).casFeatCode_identifier);}
    
  /** setter for identifier - sets Identifier of the named entity, e.g. a reference into a person database. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setIdentifier(String v) {
    if (NamedEntity_Type.featOkTst && ((NamedEntity_Type)jcasType).casFeat_identifier == null)
      jcasType.jcas.throwFeatMissing("identifier", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    jcasType.ll_cas.ll_setStringValue(addr, ((NamedEntity_Type)jcasType).casFeatCode_identifier, v);}    
  }

    