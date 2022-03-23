package org.hucompute.textimager.srl;

import de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.texttechnologylab.annotation.semaf.isobase.Entity;
import org.texttechnologylab.annotation.semaf.semafsr.SrLink;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SemanticRoleLabeling extends JCasAnnotator_ImplBase {

    public static final String PARAM_HOST = "host";
    @ConfigurationParameter(name = PARAM_HOST, mandatory = false, defaultValue = "localhost")
    protected String host;

    public static final String PARAM_PORT = "port";
    @ConfigurationParameter(name = PARAM_PORT, mandatory = false, defaultValue = "5087")
    protected Integer port;

    public static final String PARAM_LANGUAGE = "language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false, defaultValue = "de")
    protected String language;

    protected String endpoint;

    public static final String PARAM_MAX_TEXT_WINDOW = "iMaxTextWindow";
    @ConfigurationParameter(name = PARAM_MAX_TEXT_WINDOW, defaultValue = "100", mandatory = false)
    protected int iMaxTextWindow;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        endpoint = String.format("http://%s:%d/srl", host, port);
    }

    public void internalProcess(JCas aJCas, int iBegin, int iEnd) throws AnalysisEngineProcessException {

        try {
            ArrayList<Sentence> sentences = new ArrayList<>(JCasUtil.select(aJCas, Sentence.class));
            sentences = new ArrayList<>(sentences.subList(iBegin, iEnd));

            List<String> sentenceStrings = sentences.stream()
                    .map(Annotation::getCoveredText)
                    .map(String::strip)
                    .collect(Collectors.toList());

            JSONObject requestJSON = new JSONObject();
            requestJSON.put("sentences", new JSONArray(sentenceStrings));
            requestJSON.put("lang", this.language);
            JSONArray responseJSON = new JSONArray();
            try {
                Content content = Request.Post(endpoint)
                        .bodyString(requestJSON.toString(), ContentType.APPLICATION_JSON)
                        .execute()
                        .returnContent();
                responseJSON = new JSONArray(content.toString());
            } catch (Exception e) {
                DocumentMetaData dmd = DocumentMetaData.get(aJCas);
                System.out.println(dmd.getDocumentId() + "\t" + dmd.getDocumentTitle());
                e.printStackTrace();
            }
            Map<Integer, Token> tokenBeginMap = JCasUtil.select(aJCas, Token.class).stream().collect(Collectors.toMap(
                    Token::getBegin,
                    Function.identity(),
                    (a, b) -> a
            ));

            if (responseJSON.length() > 0) {

                for (int sentIdx = 0; sentIdx < sentences.size(); sentIdx++) {
                    int sentenceOffset = sentences.get(sentIdx).getBegin();

                    JSONObject sentenceResults = responseJSON.getJSONObject(sentIdx);
                    JSONArray srlResults = sentenceResults.getJSONArray("srl");
                    for (int predIdx = 0; predIdx < srlResults.length(); predIdx++) {
                        JSONObject predicateJSON = srlResults.getJSONObject(predIdx);

                        JSONArray character_aligned_tags = predicateJSON.getJSONArray("character_aligned_tags");
                        int predBegin = -1;
                        for (int tagIdx = 0; tagIdx < character_aligned_tags.length(); tagIdx++) {
                            JSONArray tagArray = character_aligned_tags.getJSONArray(tagIdx);
                            int tagBegin = tagArray.getInt(0);
                            String tagValue = tagArray.getString(2);

                            if (tagValue.equals("V")) {
                                predBegin = tagBegin;
                                break;
                            }
                        }
                        if (predBegin < 0 || !tokenBeginMap.containsKey(predBegin + sentenceOffset)) {
                            continue;
                        }

                        Token predicateToken = tokenBeginMap.get(predBegin + sentenceOffset);
                        Entity predciateEntity = new Entity(aJCas, predicateToken.getBegin(), predicateToken.getEnd());
                        aJCas.addFsToIndexes(predciateEntity);

                        for (int tagIdx = 0; tagIdx < character_aligned_tags.length(); tagIdx++) {
                            JSONArray tagArray = character_aligned_tags.getJSONArray(tagIdx);
                            int tagBegin = tagArray.getInt(0);
                            int tagEnd = tagArray.getInt(1);
                            String tagValue = tagArray.getString(2);

                            if (tagValue.equals("V") || tagValue.equals("O")) {
                                continue;
                            }

                            Entity tagEntity = new Entity(aJCas, tagBegin + sentenceOffset, tagEnd + sentenceOffset);
                            aJCas.addFsToIndexes(tagEntity);

                            SrLink srLink = new SrLink(aJCas);
                            srLink.setFigure(predciateEntity);
                            srLink.setGround(tagEntity);
                            srLink.setRel_type(tagValue);
                            aJCas.addFsToIndexes(srLink);
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AnalysisEngineProcessException(e);
        }


    }


    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

        ArrayList<Sentence> sentences = new ArrayList<>(JCasUtil.select(aJCas, Sentence.class));

        if (iMaxTextWindow < 0) {
            internalProcess(aJCas, 0, sentences.size());
        } else {

            int iCount = 0;

            boolean finish = false;
            while (!finish) {
                int iFrom = iMaxTextWindow * iCount;
                int iTo = (iCount * iMaxTextWindow) + iMaxTextWindow;

                if (((iCount * iMaxTextWindow) + iMaxTextWindow) < sentences.size() - 1) {
                    System.out.println(iFrom + " --> " + iTo);
                    internalProcess(aJCas, iFrom, iTo);
                } else {
                    internalProcess(aJCas, iFrom, sentences.size());
                    finish = true;
                }

                iCount++;


            }

        }


    }
}
