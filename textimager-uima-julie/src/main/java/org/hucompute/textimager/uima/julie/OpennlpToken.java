package org.hucompute.textimager.uima.julie;

public class OpennlpToken extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/opennlpToken";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
}
