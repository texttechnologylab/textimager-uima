
/* First created by JCasGen Wed Sep 01 09:32:12 CEST 2021 */
package org.hucompute.textimager.uima.type;

import org.apache.uima.cas.Feature;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/** 
 * Updated by JCasGen Mon Jul 11 11:54:35 CEST 2022
 * @generated */
public class VaderSentiment_Type extends CategorizedSentiment_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = VaderSentiment.typeIndexID;
  /** @generated
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.hucompute.textimager.uima.type.VaderSentiment");

  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type
	 */
  public VaderSentiment_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



