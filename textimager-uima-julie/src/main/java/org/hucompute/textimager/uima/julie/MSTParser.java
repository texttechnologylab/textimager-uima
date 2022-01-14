package org.hucompute.textimager.uima.julie;

import de.julielab.jcore.types.DependencyRelation;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.hucompute.textimager.uima.julie.helper.Converter;
import org.hucompute.textimager.uima.julie.reader.JsonReader;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import java.io.IOException;

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

    /**
     * Read Json and update jCas.
     * @param aJCas
     */
    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {
        try {
            JsonReader reader = new JsonReader();
            reader.UpdateJsonToCas(jsonResult, aJCas);

            for (DependencyRelation jdependency : JCasUtil.select(aJCas, DependencyRelation.class)) {
                Dependency ddependency = new Dependency(aJCas, jdependency.getBegin(), jdependency.getEnd());
                if (jdependency.getHead() != null)
                {
                    ddependency.setDependent(JCasUtil.selectAt(aJCas, Token.class, jdependency.getHead().getBegin(), jdependency.getHead().getEnd()).get(0));
                }
                ddependency.setDependencyType(jdependency.getLabel());
                ddependency.addToIndexes();

            }
            Converter conv = new Converter();

            conv.RemoveSentence(aJCas);
            conv.RemoveToken(aJCas);
            conv.RemovePOStag(aJCas);

        } catch (UIMAException | IOException | SAXException ex) {
            throw new AnalysisEngineProcessException(ex);
        }
    }
}
