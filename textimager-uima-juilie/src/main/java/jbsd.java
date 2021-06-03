import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.Dependency;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.DependencyFlavor;
import de.tudarmstadt.ukp.dkpro.core.api.syntax.type.dependency.ROOT;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class jbsd extends  RestAnnotator {

    @Override
    protected String getRestRoute() {
        return "/jbsd";
    }


    @Override
    public void initialize(UimaContext aContext) throws  ResourceInitializationException
    {
        super.initialize(aContext);
    }

    @Override
    protected JSONObject buildJSON(JCas aJCas) {

        //TODO Create the jsonArray

        JSONObject payload = new JSONObject();
        payload.put("text", aJCas.getDocumentText());
        payload.put("lang", aJCas.getDocumentLanguage());
        return payload;
    }

    @Override
    protected void updateCAS(JCas aJCas, JSONObject jsonResult) throws AnalysisEngineProcessException {

        if (!jsonResult.has("Sentences")) {
            System.out.println("no sentences in response?!");
            System.out.println(jsonResult.toString());
            return;
        }
        //TODO convet to uima
        JSONArray jsonSentences = jsonResult.getJSONArray("Sentences");

        for (Object object : jsonSentences) {
            String sd = "";
            JSONObject jsObj = (JSONObject)object;

            Sentiment sentiment = new Sentiment(aJCas,(Integer)jsObj.get("begin"),(Integer)jsObj.get("end"));

            sentiment.addToIndexes();
            String fdg = "";

        }
        ;
    }

}
