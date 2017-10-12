package embeddingAnnotation.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;

public class EmbeddingAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(EmbeddingAnnotation.class);
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
  protected EmbeddingAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public EmbeddingAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public EmbeddingAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public EmbeddingAnnotation(JCas jcas, int begin, int end) {
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

  /** getter for value - gets start and end of the objects
   * @generated
   * @return value of the feature 
   */
  public String getValue() {
    if (EmbeddingAnnotation_Type.featOkTst && ((EmbeddingAnnotation_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "embeddingAnnotation.type.EmbeddingAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((EmbeddingAnnotation_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets start and end of the objects 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (EmbeddingAnnotation_Type.featOkTst && ((EmbeddingAnnotation_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "embeddingAnnotation.type.EmbeddingAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((EmbeddingAnnotation_Type)jcasType).casFeatCode_value, v);}    
  

	//*--------------*
	//* Feature: distance
	
	/** getter for distance - gets start and end of the objects
	 * @generated
	 * @return value of the feature 
	 */
	public String getDistance() {
	  if (EmbeddingAnnotation_Type.featOkTst && ((EmbeddingAnnotation_Type)jcasType).casFeat_distance == null)
	    jcasType.jcas.throwFeatMissing("distance", "embeddingAnnotation.type.EmbeddingAnnotation");
	  return jcasType.ll_cas.ll_getStringValue(addr, ((EmbeddingAnnotation_Type)jcasType).casFeatCode_distance);}
	  
	/** setter for value - sets start and end of the objects 
	 * @generated
	 * @param v value to set into the feature 
	 */
	public void setDistance(String v) {
	  if (EmbeddingAnnotation_Type.featOkTst && ((EmbeddingAnnotation_Type)jcasType).casFeat_distance == null)
	    jcasType.jcas.throwFeatMissing("distance", "embeddingAnnotation.type.EmbeddingAnnotation");
	  jcasType.ll_cas.ll_setStringValue(addr, ((EmbeddingAnnotation_Type)jcasType).casFeatCode_distance, v);}    
	}

    