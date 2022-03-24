import com.google.common.collect.Lists;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SemanticRoleLabeling extends JCasAnnotator_ImplBase {

    public static final String PARAM_ENDPOINTS = "endpoints";
    @ConfigurationParameter(
            name = PARAM_ENDPOINTS,
            description = "An array of trans-nlp-api endpoints, like 'https://localhost:5087/srl'."
    )
    protected String[] endpoints;
    private final ConcurrentLinkedQueue<String> endpointQueue = new ConcurrentLinkedQueue<>();

    public static final String PARAM_LANGUAGE = "language";
    @ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = false, defaultValue = "de", description = "The source " +
            "language. For currently supported languages, see the apidocs of the endpoints.")
    protected String language;

    public static final String PARAM_ALIGNMENT_METHOD = "alignmentMethod";
    @ConfigurationParameter(name = PARAM_ALIGNMENT_METHOD, mandatory = false, defaultValue = "itermax")
    protected String alignmentMethod;

    public static final String PARAM_TOKEN_TYPE = "alignmentTokenType";
    @ConfigurationParameter(
            name = PARAM_TOKEN_TYPE, mandatory = false, defaultValue = "word",
            description = "Choose 'bpe' for languages where SentencePiece (or other) segmentation makes sense, " +
                    "otherwise choose 'word' (default)."
    )
    protected String alignmentTokenType;

    public static final String PARAM_BATCH_SIZE = "batchSize";
    @ConfigurationParameter(
            name = PARAM_BATCH_SIZE,
            defaultValue = "64",
            description = "While larger batch sizes generally improve performance by a tiny fraction, " +
                    "the improvement is negligible in comparison to the increased time that the APIs are blocked " +
                    "by a request. Keeping this to a reasonable value is recommended, default is 64."
    )
    protected Integer batchSize;
    private ExecutorService executorService;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        executorService = Executors.newFixedThreadPool(endpoints.length);
        endpointQueue.addAll(List.of(endpoints));

        if (!List.of("fwd", "rev", "inter", "itermax").contains(alignmentMethod))
            throw new ResourceInitializationException(
                    new InvalidParameterException(
                            String.format("Invalid alignment method '%s', valid choices are: fwd, rev, inter, itermax.",
                                    alignmentMethod)
                    )
            );

        if (!List.of("word", "bpe").contains(alignmentTokenType))
            throw new ResourceInitializationException(
                    new InvalidParameterException(
                            String.format("Invalid alignment method '%s', valid choices are: word, bpe.",
                                    alignmentTokenType)
                    )
            );
    }

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {
        ArrayList<Sentence> sentences = new ArrayList<>(JCasUtil.select(aJCas, Sentence.class));
        List<String> sentenceStrings = sentences.stream()
                .map(Annotation::getCoveredText)
                .map(String::strip)
                .collect(Collectors.toList());

        List<JSONObject> response = processWithEndpoints(sentenceStrings);

        annotate(aJCas, sentences, response);
    }

    /**
     * @param sentenceStrings The sentences to process.
     * @return A list of JSONObjects, as returned by the API
     * @throws AnalysisEngineProcessException
     */
    private List<JSONObject> processWithEndpoints(List<String> sentenceStrings) throws AnalysisEngineProcessException {
        // Split the list into ⌈#sentences/#endpoints⌉ sized chunks (last chunk may be only partially filled)
        List<List<String>> chunkedSentences = Lists.partition(sentenceStrings, batchSize);

        // Create Callables for each sentence chunk, wrap them in a future and start them in their own thread
        ArrayList<Future<String>> tasks = new ArrayList<>();
        for (List<String> chunk : chunkedSentences) {
            tasks.add(executorService.submit(new SRLCallable(chunk)));
        }

        // Collect all response strings, which are serialized JSON arrays, in a single list
        ArrayList<String> responses = new ArrayList<>(tasks.size());
        try {
            for (Future<String> task : tasks) {
                responses.add(task.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new AnalysisEngineProcessException(e);
        }

        // Using streams: map the response strings to JSONArrays (of JSONObjects),
        // then extract the contained JSONObjects and flatten the result into a single list of JSONObjects
        return responses.stream()
                .map(JSONArray::new)
                .map(array -> {
                    ArrayList<JSONObject> chunkResponse = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        chunkResponse.add(array.getJSONObject(i));
                    }
                    return chunkResponse;
                })
                .flatMap(List::stream)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Process the API responses and create the corresponding annotations in the JCas.
     *
     * @param aJCas     The current JCas.
     * @param sentences The list of all sentences in the current JCas.
     * @param responses The list of JSONObjects in the response from the API.
     */
    private void annotate(JCas aJCas, ArrayList<Sentence> sentences, List<JSONObject> responses) {
        Map<Integer, Token> tokenBeginMap = JCasUtil.select(aJCas, Token.class).stream().collect(Collectors.toMap(
                Token::getBegin,
                Function.identity(),
                (a, b) -> a
        ));

        for (int sentIdx = 0; sentIdx < sentences.size(); sentIdx++) {
            int sentenceOffset = sentences.get(sentIdx).getBegin();

            JSONObject sentenceResults = responses.get(sentIdx);
            JSONArray srlResults = sentenceResults.getJSONArray("srl");
            for (int predIdx = 0; predIdx < srlResults.length(); predIdx++) {
                JSONObject predicateJSON = srlResults.getJSONObject(predIdx);
                JSONArray character_aligned_tags = predicateJSON.getJSONArray("character_aligned_tags");

                // Find the starting index of the predicate
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
                // If we could not find a predicate or the predicate is not present in the token mapping,
                // skip this result
                if (predBegin < 0 || !tokenBeginMap.containsKey(predBegin + sentenceOffset)) {
                    continue;
                }

                Token predicateToken = tokenBeginMap.get(predBegin + sentenceOffset);
                Entity predicateEntity = new Entity(aJCas, predicateToken.getBegin(), predicateToken.getEnd());
                aJCas.addFsToIndexes(predicateEntity);

                // Transfer annotations using the character-aligned tags
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
                    srLink.setFigure(predicateEntity);
                    srLink.setGround(tagEntity);
                    srLink.setRel_type(tagValue);
                    aJCas.addFsToIndexes(srLink);
                }
            }
        }
    }

    class SRLCallable implements Callable<String> {

        private final JSONObject requestJSON;

        public SRLCallable(List<String> sentences) {
            requestJSON = new JSONObject();
            requestJSON.put("sentences", new JSONArray(sentences));
            requestJSON.put("lang", language);
            requestJSON.put("alignment_method", alignmentMethod);
            requestJSON.put("alignment_token_type", alignmentTokenType);
        }

        @Override
        public String call() throws Exception {
            String endpoint = pollEndpoint();
            String responseString = Request.Post(endpoint)
                    .bodyString(requestJSON.toString(), ContentType.APPLICATION_JSON)
                    .execute()
                    .returnContent()
                    .toString();
            freeEndpoint(endpoint);
            return responseString;
        }

        /**
         * Get a free endpoint from the endpoint queue. Thread will wait if necessary.
         *
         * @return The URL of the free endpoint in a thread-safe manner.
         */
        private String pollEndpoint() {
            synchronized (endpointQueue) {
                String endpoint = endpointQueue.poll();
                while (endpoint == null) {
                    try {
                        endpointQueue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    endpoint = endpointQueue.poll();
                }
                return endpoint;
            }
        }

        /**
         * Free the endpoint again, appending it to the endpoint queue in a thread-safe manner.
         *
         * @param endpoint The endpoint that has been used by this thread.
         */
        private void freeEndpoint(String endpoint) {
            synchronized (endpointQueue) {
                endpointQueue.offer(endpoint);
                endpointQueue.notify();
            }
        }
    }
}
