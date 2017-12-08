package org.hucompute.textimager.uima.uaic.core;

import ro.uaic.info.nlptools.tools.UaicMorphologicalDictionary;

import java.io.IOException;

/**
 * Static class that takes care of loading the needed dictionaries
 *
 * @author Dinu Ganea
 */
public class DictionaryProvider {

    /**
     * The default dictionary used by the UIAC toolkit
     */
    private static UaicMorphologicalDictionary morphologicalDictionary = new UaicMorphologicalDictionary();

    static {
        morphologicalDictionary = new UaicMorphologicalDictionary();
        try {
            morphologicalDictionary.load(DictionaryProvider.class.getResourceAsStream("/dict/posDictRoDiacr.txt"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Get a instance of the morphological dictionary
     *
     * @return A instance of the morphological dictionary
     */
    public static UaicMorphologicalDictionary getMorphologicalDictionary() {
        return morphologicalDictionary;
    }

}
