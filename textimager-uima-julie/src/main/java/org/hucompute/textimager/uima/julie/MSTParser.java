package org.hucompute.textimager.uima.julie;

public class MSTParser extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/mstparser";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
}
