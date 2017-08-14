package embeddingAnnotation.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * @generated */
public class EmbeddingAnnotation_Type extends Annotation_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (EmbeddingAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = EmbeddingAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new EmbeddingAnnotation(addr, EmbeddingAnnotation_Type.this);
  			   EmbeddingAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new EmbeddingAnnotation(addr, EmbeddingAnnotation_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = EmbeddingAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("embeddingAnnotation.type.EmbeddingAnnotation");
 
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
      jcas.throwFeatMissing("value", "embeddingAnnotation.type.EmbeddingAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_value);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
      jcas.throwFeatMissing("value", "embeddingAnnotation.type.EmbeddingAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_value, v);}

  /** @generated */
  final Feature casFeat_distance;
  /** @generated */
  final int     casFeatCode_distance;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getDistance(int addr) {
        if (featOkTst && casFeat_distance == null)
      jcas.throwFeatMissing("distance", "embeddingAnnotation.type.EmbeddingAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_distance);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setDistance(int addr, String v) {
        if (featOkTst && casFeat_distance == null)
      jcas.throwFeatMissing("distance", "embeddingAnnotation.type.EmbeddingAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_distance, v);}
  
  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public EmbeddingAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
    casFeatCode_value  = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_value).getCode();
    casFeat_distance = jcas.getRequiredFeatureDE(casType, "distance", "uima.cas.String", featOkTst);
    casFeatCode_distance  = (null == casFeat_distance) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_distance).getCode();
  }
}



    