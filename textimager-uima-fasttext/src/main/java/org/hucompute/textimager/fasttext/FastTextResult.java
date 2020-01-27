package org.hucompute.textimager.fasttext;

import java.util.ArrayList;
import java.util.Collections;

public class FastTextResult {
    private ArrayList<ProbabilityLabel> results;
    private String status;
    private String message;

    public FastTextResult() {
        results = new ArrayList<>();
        status = "unknown";
        message = "";
    }

    public void error(String message) {
        this.status = "error";
        this.message = message;
    }

    public void success(String message) {
        this.status = "success";
        this.message = message;
    }

    public void parseResults(ArrayList<String> resultString) {
        // Ausgabe besteht immer aus "ddcTag probability"
        // Wir geben zurück:
        // - Label
        // - Den Probability Wert den fastText ausgiebt
        // - Angepassten Probability Wert, wie im JFastText Beispiel
        try {
            for (int ind = 0; ind < resultString.size(); ind += 2) {
                results.add(new ProbabilityLabel(resultString.get(ind), Double.parseDouble(resultString.get(ind + 1))));
            }
        } catch (Exception ex) {
            results = new ArrayList<>();
            error(ex.getMessage());
            return;
        }

        success("");
    }

    public ArrayList<ProbabilityLabel> getSortedResults(boolean cutoff) {
        ArrayList<ProbabilityLabel> labels = getResults();
        Collections.sort(labels, (r1, r2) -> ((r1.getLogProb() > r2.getLogProb()) ? -1 : ((r1.getLogProb() < r2.getLogProb()) ? 1 : 0)));

        // Alle entfernen die W'keit des kleinsten Labels haben
        if (cutoff) {
        	ProbabilityLabel lowest = labels.get(labels.size() - 1);
            labels.removeIf(v -> v.getLogProb() <= lowest.getLogProb());
            // Letztes wieder hinzufügen um niedrigsten Wert zu behalten
            // Entfernt, da die Reihenfolge der "0"-er Ergebnisse eigentlich nicht definiert ist können diese komplett weggelassen werden
            //labels.add(lowest);
        }

        return labels;
    }

    public ArrayList<ProbabilityLabel> getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
