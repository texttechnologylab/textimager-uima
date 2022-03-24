import com.google.common.base.Charsets;
import com.google.common.io.Files;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.junit.Test;
import org.texttechnologylab.annotation.semaf.semafsr.SrLink;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticRoleLabelingTest {
    final boolean DEBUG = false;

    private JCas getTinyCas() throws UIMAException {
        JCas jCas = getCasFromList(List.of(
                "Als Gregor Samsa eines Morgens aus unruhigen Träumen erwachte, fand er sich in seinem Bett zu einem ungeheuren Ungeziefer verwandelt.",
                "Über dem Atlantik befand sich ein barometrisches Minimum; es wanderte ostwärts, einem über Russland lagernden Maximum zu, und verriet noch nicht die Neigung, diesem nördlich auszuweichen."
        ));
        tokenize(jCas);
        return jCas;
    }

    private JCas getCoNLLCas() throws UIMAException {
        try {
            List<String> strings = Files.readLines(new File(
                            "src/main/resources/CoNLL2009-ST-German-train-filtered-plain.txt"),
                    Charsets.UTF_8
            ).subList(0, 257);

            JCas jCas = getCasFromList(strings);

            if (DEBUG) {
                for (Sentence sentence : JCasUtil.select(jCas, Sentence.class)) {
                    System.out.printf("'%s'\n", sentence.getCoveredText());
                }
            }

            tokenize(jCas);

            return jCas;
        } catch (IOException e) {
            throw new UIMAException(e);
        }
    }

    private JCas getCasFromList(List<String> strings) throws UIMAException {
        int lastEnd = -1;
        ArrayList<Integer> startIndices = new ArrayList<>();
        ArrayList<Integer> endIndices = new ArrayList<>();
        for (String string : strings) {
            startIndices.add(lastEnd + 1);
            endIndices.add(lastEnd + string.length());
            lastEnd = lastEnd + string.length();
        }

        JCas jCas = JCasFactory.createText(String.join(" ", strings));
        for (int i = 0; i < startIndices.size(); i++) {
            Sentence sentence = new Sentence(jCas, startIndices.get(i), endIndices.get(i));
            jCas.addFsToIndexes(sentence);
        }
        return jCas;
    }

    private void tokenize(JCas jCas) {
        Pattern pattern = Pattern.compile("(?=\\p{Blank}|$|((?<= )[^\\P{Punct}\\.\\-]+|[^\\P{Punct}\\-]+(?= )|(?<=^)[\\p{Punct}\\p{Blank}]+|[\\p{Punct}\\p{Blank}]+(?=$)))");
        Matcher matcher = pattern.matcher(jCas.getDocumentText());

        int prevIndex = 0;
        while (matcher.find(prevIndex)) {
            int nextIndex = matcher.start();
            if (prevIndex == nextIndex) {
                prevIndex--;
            }
            Token token = new Token(jCas, prevIndex, nextIndex);
            jCas.addFsToIndexes(token);
            prevIndex = nextIndex + 1;
            if (prevIndex > jCas.getDocumentText().length())
                break;
        }
        if (DEBUG) {
            for (Token token : JCasUtil.select(jCas, Token.class)) {
                System.out.printf("'%s'\n", token.getCoveredText());
            }
        }
    }

    @Test
    public void testSingle() throws UIMAException {
        JCas jCas = getTinyCas();

        AnalysisEngine engine = AnalysisEngineFactory.createEngine(
                SemanticRoleLabeling.class,
                SemanticRoleLabeling.PARAM_ENDPOINTS, new String[]{
                        "http://localhost:5087/srl"
                }
        );

        SimplePipeline.runPipeline(jCas, engine);

        for (SrLink srLink : JCasUtil.select(jCas, SrLink.class)) {
            System.out.printf(
                    "'%s' -[%s]-> '%s'\n",
                    srLink.getFigure().getCoveredText(),
                    srLink.getRel_type(),
                    srLink.getGround().getCoveredText()
            );
            System.out.println(srLink.toString(2));
        }
    }

    @Test
    public void testMulti() throws UIMAException {
        JCas jCas = getTinyCas();

        AnalysisEngine engine = AnalysisEngineFactory.createEngine(
                SemanticRoleLabeling.class,
                SemanticRoleLabeling.PARAM_ENDPOINTS, new String[]{
                        "http://localhost:5087/srl",
                        "http://localhost:5088/srl"
                }
        );

        SimplePipeline.runPipeline(jCas, engine);

        for (SrLink srLink : JCasUtil.select(jCas, SrLink.class)) {
            System.out.printf(
                    "'%s' -[%s]-> '%s'\n",
                    srLink.getFigure().getCoveredText(),
                    srLink.getRel_type(),
                    srLink.getGround().getCoveredText()
            );
            System.out.println(srLink.toString(2));
        }
    }
}
