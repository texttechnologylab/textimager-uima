package org.hucompute.textimager.uima.io.mediawiki;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.uima.jcas.JCas;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

public class SemiographHelper {
    EmotionHelper eh;
	public SemiographHelper(JCas cas) {
        eh = new EmotionHelper(cas);

    }
    public String mergeStaticSemiographString(String embedding_id) throws IOException{
        StringBuilder html_divs = new StringBuilder();
        StringBuilder res = new StringBuilder();
        JSONArray jsonArray = new JSONArray();
        int i = 0;
        if (eh.disgustSet.size()>0){
            JSONObject to = getEmbeddingJSONObject(embedding_id, eh.disgustSet);
            if(to!=null){
                jsonArray.put(to);
                html_divs.append(buildStaticSemiographHTMLDiv(embedding_id, "disgust", i));
                i = i+1;
            }
        }
        if (eh.contemptSet.size()>0){
            JSONObject to = getEmbeddingJSONObject(embedding_id, eh.contemptSet);
            if(to!=null){
                jsonArray.put(to);
                html_divs.append(buildStaticSemiographHTMLDiv(embedding_id, "contempt", i));
                i = i+1;
            }
        }
        if (eh.surpriseSet.size()>0){
            JSONObject to = getEmbeddingJSONObject(embedding_id, eh.surpriseSet);
            if(to!=null){
                jsonArray.put(to);
                html_divs.append(buildStaticSemiographHTMLDiv(embedding_id, "surprise", i));
                i = i+1;
            }
        }
        if (eh.fearSet.size()>0){
            JSONObject to = getEmbeddingJSONObject(embedding_id, eh.fearSet);
            if(to!=null){
                jsonArray.put(to);
                html_divs.append(buildStaticSemiographHTMLDiv(embedding_id, "fear", i));
                i = i+1;
            }
        }
        if (eh.mourningSet.size()>0){
            JSONObject to = getEmbeddingJSONObject(embedding_id, eh.mourningSet);
            if(to!=null){
                jsonArray.put(to);
                html_divs.append(buildStaticSemiographHTMLDiv(embedding_id, "mourning", i));
                i = i+1;
            }
        }
        if (eh.angerSet.size()>0){
            JSONObject to = getEmbeddingJSONObject(embedding_id, eh.angerSet);
            if(to!=null){
                jsonArray.put(to);
                html_divs.append(buildStaticSemiographHTMLDiv(embedding_id, "anger", i));
                i = i+1;
            }
        }
        if (eh.joySet.size()>0){
            JSONObject to = getEmbeddingJSONObject(embedding_id, eh.joySet);
            if(to!=null){
                jsonArray.put(to);
                html_divs.append(buildStaticSemiographHTMLDiv(embedding_id, "joy", i));
                i = i+1;
            }
        }
        res.append("<html><script>embeds=");
        res.append(jsonArray.toString());
        res.append("</script></html>");
        res.append(html_divs);

        return res.toString();
    }
	public String buildStaticSemiographHTMLDiv(String embedding_id, String emotion_name, int embedding_index){
		StringBuilder res = new StringBuilder();

        res.append("\n== ").append("Semiograph ").append(emotion_name).append(" ==\n");
		res.append("<html>");
        res.append("<div id='embedding_" + emotion_name + "' class=\"embeddingviz\" ");
        res.append("data-width=\"" + "1000" + "\" ");
        res.append("data-height=\"" + "500" + "\" ");
        res.append("data-embedindex=\"" + Integer.toString(embedding_index) + "\"");
        res.append("></div></html>");
        res.append("\n");
		return res.toString();
	}
    public JSONObject getEmbeddingJSONObject(String embedding_id, Set<Token> emotionToken) throws IOException{
        JSONObject payload = new JSONObject();

        JSONArray embeddings = new JSONArray();
        embeddings.put(embedding_id);
        payload.put("embeddings", embeddings);
        JSONArray words = eh.getEmotionJSONArray(emotionToken);
        payload.put("words", words);
        payload.put("maxn", "8");
        payload.put("add_ddc_nodes", "true");

        JSONObject result = null;
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        try {
            HttpPost request = new HttpPost("http://semiograph.texttechnologylab.org/api/nn");
            StringEntity params = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);
            request.setEntity(params);
            HttpResponse resp = httpClient.execute(request);
            String json = EntityUtils.toString(resp.getEntity());
            System.out.println("-------------------CALLING SEMIOGRAPH-API FOR EMBEDDINGS-------------------");
            System.out.println(resp.getStatusLine());
            System.out.println(eh.getEmotionHTMLDataAttribute(emotionToken));
            System.out.println("----------------------------------------------------");
            JSONObject tres = new JSONObject(json);
            result = tres.getJSONObject(embedding_id);

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            httpClient.close();
        }
        return result;
    }


}

