package org.hucompute.textimager.uima.julie;

public class OpennlpParser extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/opennlpParser";
    }
}
