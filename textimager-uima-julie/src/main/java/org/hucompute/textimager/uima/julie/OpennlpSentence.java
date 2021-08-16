package org.hucompute.textimager.uima.julie;

public class OpennlpSentence extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/opennlpSentence";
    }
}

