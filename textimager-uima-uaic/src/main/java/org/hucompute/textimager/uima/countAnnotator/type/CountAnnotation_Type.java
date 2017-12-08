
/* First created by JCasGen Tue Apr 12 15:59:03 CEST 2016 */
package org.hucompute.textimager.uima.countAnnotator.type;

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
 * Updated by JCasGen Tue Apr 12 15:59:03 CEST 2016
 *
 * @generated
 */
public class CountAnnotation_Type extends Annotation_Type {
    /**
     * @return the generator for this type
     * @generated
     */
    @Override
    protected FSGenerator getFSGenerator() {
        return fsGenerator;
    }

    /**
     * @generated
     */
    private final FSGenerator fsGenerator =
            new FSGenerator() {
                public FeatureStructure createFS(int addr, CASImpl cas) {
                    if (CountAnnotation_Type.this.useExistingInstance) {
                        // Return eq fs instance if already created
                        FeatureStructure fs = CountAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
                        if (null == fs) {
                            fs = new CountAnnotation(addr, CountAnnotation_Type.this);
                            CountAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
                            return fs;
                        }
                        return fs;
                    } else return new CountAnnotation(addr, CountAnnotation_Type.this);
                }
            };
    /**
     * @generated
     */
    @SuppressWarnings("hiding")
    public final static int typeIndexID = CountAnnotation.typeIndexID;
    /**
     * @generated
     * @modifiable
     */
    @SuppressWarnings("hiding")
    public final static boolean featOkTst = JCasRegistry.getFeatOkTst("countAnnotator.type.CountAnnotation");

    /**
     * @generated
     */
    final Feature casFeat_value;
    /**
     * @generated
     */
    final int casFeatCode_value;

    /**
     * @param addr low level Feature Structure reference
     * @return the feature value
     * @generated
     */
    public String getValue(int addr) {
        if (featOkTst && casFeat_value == null)
            jcas.throwFeatMissing("value", "countAnnotator.type.CountAnnotation");
        return ll_cas.ll_getStringValue(addr, casFeatCode_value);
    }

    /**
     * @param addr low level Feature Structure reference
     * @param v    value to set
     * @generated
     */
    public void setValue(int addr, String v) {
        if (featOkTst && casFeat_value == null)
            jcas.throwFeatMissing("value", "countAnnotator.type.CountAnnotation");
        ll_cas.ll_setStringValue(addr, casFeatCode_value, v);
    }


    /**
     * @generated
     */
    final Feature casFeat_count;
    /**
     * @generated
     */
    final int casFeatCode_count;

    /**
     * @param addr low level Feature Structure reference
     * @return the feature value
     * @generated
     */
    public int getCount(int addr) {
        if (featOkTst && casFeat_count == null)
            jcas.throwFeatMissing("count", "countAnnotator.type.CountAnnotation");
        return ll_cas.ll_getIntValue(addr, casFeatCode_count);
    }

    /**
     * @param addr low level Feature Structure reference
     * @param v    value to set
     * @generated
     */
    public void setCount(int addr, int v) {
        if (featOkTst && casFeat_count == null)
            jcas.throwFeatMissing("count", "countAnnotator.type.CountAnnotation");
        ll_cas.ll_setIntValue(addr, casFeatCode_count, v);
    }


    /**
     * initialize variables to correspond with Cas Type and Features
     *
     * @param jcas    JCas
     * @param casType Type
     * @generated
     */
    public CountAnnotation_Type(JCas jcas, Type casType) {
        super(jcas, casType);
        casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl) this.casType, getFSGenerator());


        casFeat_value = jcas.getRequiredFeatureDE(casType, "value", "uima.cas.String", featOkTst);
        casFeatCode_value = (null == casFeat_value) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_value).getCode();


        casFeat_count = jcas.getRequiredFeatureDE(casType, "count", "uima.cas.Integer", featOkTst);
        casFeatCode_count = (null == casFeat_count) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl) casFeat_count).getCode();

    }
}



    