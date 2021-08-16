package org.hucompute.textimager.uima.julie;

public class BioLemmatizer extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/biolemmatizer";
    }
}
