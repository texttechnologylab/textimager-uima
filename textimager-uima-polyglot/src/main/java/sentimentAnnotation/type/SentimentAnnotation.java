package sentimentAnnotation.type;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.tcas.Annotation;

public class SentimentAnnotation extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(SentimentAnnotation.class);
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
  protected SentimentAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public SentimentAnnotation(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public SentimentAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public SentimentAnnotation(JCas jcas, int begin, int end) {
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
    if (SentimentAnnotation_Type.featOkTst && ((SentimentAnnotation_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "sentimentAnnotation.type.SentimentAnnotation");
    return jcasType.ll_cas.ll_getStringValue(addr, ((SentimentAnnotation_Type)jcasType).casFeatCode_value);}
    
  /** setter for value - sets start and end of the objects 
   * @generated
   * @param v value to set into the feature 
   */
  public void setValue(String v) {
    if (SentimentAnnotation_Type.featOkTst && ((SentimentAnnotation_Type)jcasType).casFeat_value == null)
      jcasType.jcas.throwFeatMissing("value", "sentimentAnnotation.type.SentimentAnnotation");
    jcasType.ll_cas.ll_setStringValue(addr, ((SentimentAnnotation_Type)jcasType).casFeatCode_value, v);}    
  }

    