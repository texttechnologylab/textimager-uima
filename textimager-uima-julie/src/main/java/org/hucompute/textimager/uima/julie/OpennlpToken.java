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
}
