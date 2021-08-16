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
}
