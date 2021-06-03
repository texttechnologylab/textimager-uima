
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.base.RestAnnotator;
import org.hucompute.textimager.uima.type.Sentiment;
import org.json.JSONArray;
import org.json.JSONObject;


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

            JSONObject jsObj = (JSONObject)object;

            Sentiment sentiment = new Sentiment(aJCas,(Integer)jsObj.get("begin"),(Integer)jsObj.get("end"));

            sentiment.addToIndexes();

        }
        ;
    }

}
