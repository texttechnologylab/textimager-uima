
/* First created by JCasGen Wed Jan 22 14:27:43 CET 2020 */
package org.texttechnologylab.annotation.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.texttechnologylab.annotation.NamedEntity_Type;

/** 
 * Updated by JCasGen Wed Jan 22 14:27:43 CET 2020
 * @generated */
public class Fungi_Type extends NamedEntity_Type {
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = Fungi.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.texttechnologylab.annotation.type.Fungi");



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public Fungi_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

  }
}



    