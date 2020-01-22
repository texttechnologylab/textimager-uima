
/* First created by JCasGen Wed Jan 22 14:27:43 CET 2020 */
package de.tudarmstadt.ukp.dkpro.core.api.ner.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** Named entities refer e.g. to persons, locations, organizations and so on. They often consist of multiple tokens.
 * Updated by JCasGen Wed Jan 22 14:27:43 CET 2020
 * @generated */
public class NamedEntity_Type extends Annotation_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = NamedEntity.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
 
  /** @generated */
  final Feature casFeat_value;
  /** @generated */
  final int     casFeatCode_value;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}
    
  
 
  /** @generated */
  final Feature casFeat_identifier;
  /** @generated */
  final int     casFeatCode_identifier;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getIdentifier(int addr) {
        if (featOkTst && casFeat_identifier == null)
      jcas.throwFeatMissing("identifier", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    return ll_cas.ll_getStringValue(addr, casFeatCode_identifier);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIdentifier(int addr, String v) {
        if (featOkTst && casFeat_identifier == null)
      jcas.throwFeatMissing("identifier", "de.tudarmstadt.ukp.dkpro.core.api.ner.type.NamedEntity");
    ll_cas.ll_setStringValue(addr, casFeatCode_identifier, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public NamedEntity_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();

 
    casFeat_identifier = jcas.getRequiredFeatureDE(casType, "identifier", "uima.cas.String", featOkTst);
    casFeatCode_identifier  = (null == casFeat_identifier) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_identifier).getCode();

  }
}



    