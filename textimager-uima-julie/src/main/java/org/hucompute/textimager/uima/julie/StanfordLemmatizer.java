package org.hucompute.textimager.uima.julie;

public class StanfordLemmatizer extends JulieBase {
    /**
     * Tagger address.
     * @return endpoint
     */
    @Override
    protected String getRestRoute() {
        return "/stanfordlemmatizer";
    }

    @Override
    protected String getAnnotatorVersion() {
        return "0.0.1";
    }
}
