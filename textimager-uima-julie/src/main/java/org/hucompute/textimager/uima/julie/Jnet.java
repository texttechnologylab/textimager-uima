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
}
