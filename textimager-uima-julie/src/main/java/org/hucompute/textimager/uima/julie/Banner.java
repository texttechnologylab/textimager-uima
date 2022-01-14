package org.hucompute.textimager.uima.julie;

public class Banner extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/banner";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
}