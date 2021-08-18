package org.hucompute.textimager.uima.julie;

public class Acronym extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/acronym";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
}
