package org.hucompute.textimager.hfbertdisambiguation;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.semantics.type.WordSense;
import jep.JepException;
import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.JepAnnotator;
import org.springframework.util.FileSystemUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;

@TypeCapability(
        inputs = {"de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token", "de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence", "de.tudarmstadt.ukp.dkpro.core.api.lexmorph.type.pos.POS"},
        outputs = {"de.tudarmstadt.ukp.dkpro.core.api.semantics.type.WordSense"}
)
public class HFBertDisambiguation extends JepAnnotator {

    private final String[] resourceFiles = new String[]{"torch_tagger.py", "model_save/config.json", "model_save/pytorch_model.bin"};
    private Path tempFolder;

    @Override
    public void initialize(UimaContext aContext) throws ResourceInitializationException {
        super.initialize(aContext);

        if (envName == null || envName.isEmpty()) {
            envName = "textimager_flair_py37";
        }
        if (envPythonVersion == null || envPythonVersion.isEmpty()) {
            envPythonVersion = "3.7";
        }
        if (envDepsConda == null || envDepsConda.isEmpty()) {
            envDepsConda = "uarray=0.6.0 -c conda-forge";
        }
        if (envDepsPip == null || envDepsPip.isEmpty()) {
            envDepsPip = "--upgrade torch==1.5.1 transformers==3.1.0";
        }
        if (condaVersion == null || condaVersion.isEmpty()) {
            condaVersion = "py37_4.8.3";
        }

        initConda();

        try {

            tempFolder = Files.createTempDirectory(this.getClass().getSimpleName());
            for (String fileName : resourceFiles) {
                Path outPath = Paths.get(tempFolder.toString(), fileName);
                Files.createDirectories(outPath.getParent());

                try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(fileName)) {
                    FileUtils.copyInputStreamToFile(Objects.requireNonNull(resourceAsStream), outPath.toFile());
                } catch (Exception e) {
                    throw new Exception("resourceAsStream returns null");
                }
            }
            String test = tempFolder.toAbsolutePath().toString();
            System.out.println(test);
            interpreter.exec("import sys, os");
            interpreter.exec("sys.path = ['" + tempFolder.toAbsolutePath().toString() + "/'] + sys.path");
            interpreter.exec("os.chdir('" + test + "')");
            interpreter.exec("from torch_tagger import BertTagger");
            interpreter.exec("tagger = BertTagger()");

        } catch (Exception e) {
            throw new ResourceInitializationException(e);
        }
    }

    @Override
    public void process(JCas jCas) {

        Collection<Sentence> sentences = JCasUtil.select(jCas, Sentence.class);

        for (Sentence sentence : sentences) {

            String coveredText = sentence.getCoveredText();

            for (Token t : JCasUtil.selectCovered(Token.class, sentence)) {
                String tokenText = t.getText();

                //check if token is preposition
                if (t.getPos().getPosValue().equals("IN")) {
                    int tokenBegin = t.getBegin() - sentence.getBegin();
                    int tokenEnd = t.getEnd() - sentence.getBegin();

                    // prepare sentence for the python tagger
                    String adjustedSentence = coveredText.substring(0, tokenBegin) + "<head>" + tokenText + "</head>" + coveredText.substring(tokenEnd, sentence.getEnd() - sentence.getBegin());

                    // get prediction-value and set as WordSense
                    try {
                        String labelId = interpreter.getValue("tagger.tag('" + adjustedSentence + "')", String.class);
                        WordSense wordSense = new WordSense(jCas, tokenBegin, tokenEnd);
                        wordSense.setValue(labelId);
                        wordSense.addToIndexes();
                    } catch (JepException e) {
                        e.printStackTrace();
                    }

                }

            }
        }
    }

    @Override
    public void destroy() {
        try {
            if (tempFolder != null) {
                FileSystemUtils.deleteRecursively(tempFolder.toFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.destroy();
    }
}
