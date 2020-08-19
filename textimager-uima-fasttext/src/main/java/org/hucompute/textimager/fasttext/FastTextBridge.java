package org.hucompute.textimager.fasttext;

import org.apache.uima.analysis_engine.annotator.AnnotatorProcessException;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.*;
import java.util.*;

// Steuert fastText über StdIn/StdOut
public class FastTextBridge {
    private class FastTextProcess {
        // fastText Parameter
        private int num_labels;
        private String model;
        private String language;
        private String fasttext;

        // fastText Process
        private Process process;
        private BufferedWriter in;
        private Scanner out;

        boolean is_loaded;

        FastTextProcess(String language, String model, String fasttext, int num_labels) throws FileNotFoundException {
            if (!new File(fasttext).exists()) {
                throw new FileNotFoundException("fastText executable not found at \"" + fasttext + "\"");
            }

            if (!new File(model).exists()) {
                throw new FileNotFoundException("fastText model file not found at \"" + model + "\"");
            }

            if (num_labels < 1) {
                throw new IllegalArgumentException("fastText num_labels must be at leas 1");
            }

            this.language = language;
            this.model = model;
            this.num_labels = num_labels;
            this.fasttext = fasttext;

            this.is_loaded = false;

            System.out.println("[" + this.language + "] Initialized fastText process");
        }

        void start() throws ResourceInitializationException {
        	System.out.println("[" + this.language + "] Starting fastText process with location [" + this.fasttext + "], model [" + this.model + "], labels num [" + this.num_labels + "]");

            // Starten mit Predict, - zum lesen von stdin und k (Anzahl Ausgaben)
            ProcessBuilder builder = new ProcessBuilder(fasttext, "predict-prob", model, "-", String.valueOf(num_labels));
            try {
                process = builder.start();

                // get stdin of shell
                in = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

                // Stdout
                out = new Scanner(process.getInputStream());

                this.is_loaded = true;

                System.out.println("[" + this.language + "] Successfully started fastText process with location [" + this.fasttext + "], model [" + this.model + "], labels num [" + this.num_labels + "]");

            } catch (Exception e) {
                throw new ResourceInitializationException("Failed to start fastText process", null, e);
            }
        }

        void exit() {
            if (!isLoaded()) {
                return;
            }

            System.out.println("[" + this.language + "] Exiting fastText process...");

            this.is_loaded = false;

            out.close();
            process.destroy();

            process = null;
            in = null;
            out = null;
        }

        boolean stdin(String text) {
            try {
                in.write(text);
                in.newLine();
                in.flush();
            } catch (IOException e) {
            	System.out.println(e.getMessage());
                return false;
            }
            return true;
        }

        String next() {
            return out.next();
        }

        int getNumLabels() {
            return num_labels;
        }

        boolean isLoaded() {
            return is_loaded;
        }
    }

    // Für jede Sprache eine Liste an Prozessen
    private HashMap<String, ArrayList<FastTextProcess>> fasttext_procs;
    boolean lazy_load;
    int max_loaded;
    Queue<FastTextProcess> currently_loaded;

    // FastText im "Stdin" Modus starten
    public FastTextBridge(String fasttextLocation, String fastTextLanguageModelsLabels, boolean lazyLoad, int maxLoaded) throws ResourceInitializationException {
    	System.out.println("Initializing fastText processes...");

        // TODO Später auslagern in DUCC, der kann Annotatoren einfach starten und eine Zeit laufen lassen
        lazy_load = lazyLoad;
        max_loaded = maxLoaded;
        currently_loaded = new LinkedList<>();

        fasttext_procs = new HashMap<>();

        try {
            String[] ftlml = fastTextLanguageModelsLabels.split(",", -1);
            for (int ind = 0; ind < ftlml.length; ind += 3) {
                String lang = ftlml[ind];
                String model = ftlml[ind + 1];
                int numLables = Integer.parseInt(ftlml[ind + 2]);
                if (!fasttext_procs.containsKey(lang)) {
                    fasttext_procs.put(lang, new ArrayList<>());
                }
                fasttext_procs.get(lang).add(new FastTextProcess(lang, model, fasttextLocation, numLables));
            }
        } catch (Exception ex) {
            throw new ResourceInitializationException("Error initializing fastText Process Handler", null, ex);
        }


        if (!lazyLoad) {
        	System.out.println("Starting fastText processes...");
            for (HashMap.Entry<String, ArrayList<FastTextProcess>> ftps : fasttext_procs.entrySet()) {
                for (FastTextProcess ftp : ftps.getValue()) {
                    ftp.start();
                }
            }
        } else {
        	System.out.println("Not starting fastText processes, lazy loading enabled wit max=" + maxLoaded);
        }
    }

    public void exit() {
    	System.out.println("Exiting fastText processes...");

        for (HashMap.Entry<String, ArrayList<FastTextProcess>> ftps : fasttext_procs.entrySet()) {
            for (FastTextProcess ftp : ftps.getValue()) {
                ftp.exit();
            }
        }

        fasttext_procs.clear();
    }

    public ArrayList<FastTextResult> input(String language, String inputText) throws AnnotatorProcessException {
    	//System.out.println("fastText input");
    	//System.out.println("!!!" + inputText + "!!!");

        // Input Text modifizieren:
        // Wenn der Text weniger als 1000 Zeichen hat alles solange wiederholen bis drüber
        // TODO Als Configparam
        // TODO auch bei Disambiguierung?
        StringBuilder temp = new StringBuilder();
        temp.append(inputText).append(" ");
        int inputTextLength = temp.length();
        while (inputTextLength < 1000) {
            temp.append(inputText).append(" ");
            inputTextLength = temp.length();
        }
        inputText = temp.toString();

        // Sprache  wählen
        ArrayList<FastTextProcess> ftps;
        if (fasttext_procs.containsKey(language)) {
            ftps = fasttext_procs.get(language);
            //System.out.println("  language ok: " + language);
        } else {
            // Keine Sprache, einfach erstes wählen...
            try {
                ftps = fasttext_procs.entrySet().iterator().next().getValue();
                //System.out.println("  language not ok, took first available: " + language);
            } catch (Exception ex) {
                throw new AnnotatorProcessException("fastText could not find language [" + language + "]", null, ex);
            }
        }

        String text = cleanStringForFastText(inputText);

        ArrayList<FastTextResult> results = new ArrayList<>();

        for (FastTextProcess ftp : ftps) {
            if (lazy_load && !ftp.isLoaded()) {
                try {
                    if (currently_loaded.size() >= max_loaded) {
                        currently_loaded.poll().exit();
                    }
                    ftp.start();
                    currently_loaded.add(ftp);
                } catch (ResourceInitializationException e) {
                    throw new AnnotatorProcessException("fastText could not lazy load language [" + language + "], model [" + ftp.model + "]", null, e);
                }
            }

            FastTextResult niceResult = new FastTextResult();

            //System.out.println("processing with model [" + ftp.model + "]...");
            try {
                if (ftp.stdin(text)) {
                    ArrayList<String> result = new ArrayList<>();

                    // Immer genau 2 Ausgaben pro Label
                    for (int i = 0; i < 2 * ftp.getNumLabels(); ++i) {
                        result.add(ftp.next());
                    }

                    niceResult.parseResults(result);
                }
            } catch (Exception ex) {
                throw new AnnotatorProcessException("fastText could not get output", null, ex);
            }
            //System.out.println("processing with model [" + ftp.model + "] done.");

            results.add(niceResult);
        }

        return results;
    }

    private String cleanStringForFastText(String input) {
        // Alles auf eine Zeile reduzieren...
        // Sowie fastText "Satzende" usw Zeichen entfernen
        return input.replaceAll("</s>|<|>", " ").replaceAll("\\r\\n|\\r|\\n", " ");
    }
}