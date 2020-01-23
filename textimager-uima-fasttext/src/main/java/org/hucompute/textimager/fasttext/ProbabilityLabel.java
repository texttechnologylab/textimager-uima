package org.hucompute.textimager.fasttext;

public class ProbabilityLabel {
    private String label;
    private double logProb;
    private double prob;

    ProbabilityLabel(String label, double logProb) {
        this.label = label;
        this.logProb = logProb;
        this.prob = Math.exp(this.logProb);
    }

    public String getLabel() {
        return label;
    }

    public double getLogProb() {
        return logProb;
    }

    public double getProb() {
        return prob;
    }
}
