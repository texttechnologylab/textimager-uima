package org.hucompute.textimager.uima.julie;

public class Jnet extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/jnet";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
}
