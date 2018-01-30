package org.hucompute.textimager.uima.uaic.core;

import java.io.InputStream;

/**
 * Static class that takes care of the external resources needed for the UAIC toolkit
 *
 * @author Dinu Ganea
 */
public class ResourceStreamProvider {

    /**
     * Stream to the model file
     */
    private static InputStream modelFile;
    /**
     * Stream to the gaset file
     */
    private static InputStream tagsetFile;

    /**
     * Stream to the reduction file
     */
    private static InputStream reductionFile;

    static {
        modelFile = ResourceStreamProvider.class.getResourceAsStream("/raw/posRoDiacr.model");
        tagsetFile = ResourceStreamProvider.class.getResourceAsStream("/raw/guesserTagset.txt");
        reductionFile = ResourceStreamProvider.class.getResourceAsStream("/raw/posreduction.ggf");
    }

    /**
     * Get the input stream for the model file
     *
     * @return The input stream for the model file
     */
    public static InputStream getModelFile() {
        return modelFile;
    }

    /**
     * Get the input stream for the tagset file
     *
     * @return The input stream for the tagset file
     */
    public static InputStream getTagsetFile() {
        return tagsetFile;
    }

    /**
     * Get the input stream for the reduction file
     *
     * @return The input stream for the reduction file
     */
    public static InputStream getReductionFile() {
        return reductionFile;
    }

}
