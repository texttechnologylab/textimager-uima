package org.hucompute.textimager.fasttext.disambig;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.fasttext.BaseAnnotator;
import org.hucompute.textimager.fasttext.FastTextResult;
import org.hucompute.textimager.fasttext.ProbabilityLabel;
import org.hucompute.textimager.uima.type.category.CategoryCoveredTagged;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class Disambig extends BaseAnnotator {
	/**
	 * Write stats file when annotator shuts down
	 */
	public static final String PARAM_OUTPUT_STATS_FILE = "output_stats_file";
	@ConfigurationParameter(name = PARAM_OUTPUT_STATS_FILE, mandatory = false, defaultValue = "")
	protected String outputStatsFile;

	/**
	 * Location of the disambiguable list, one word per line
	 * second column of "wikipedia_dewiki-20160203-pages-meta-current_tagged_fixed2.lemma_map"
	 */
	public static final String PARAM_DISAMBIG_LIST_LOCATION = "disambig_location";
	@ConfigurationParameter(name = PARAM_DISAMBIG_LIST_LOCATION, mandatory = true)
	protected String disambig_location;

	/**
	 * Label prefix (__label__)
	 */
	public static final String PARAM_LABEL_PREFIX = "labelPrefix";
	@ConfigurationParameter(name = PARAM_LABEL_PREFIX, mandatory = true, defaultValue = "__label__")
	protected String labelPrefix;

	/**
	 * Label word regex
	 * BSP: __label__1234_bank => bank
	 */
	public static final String PARAM_LABEL_WORD_REGEX = "labelWordRegex";
	@ConfigurationParameter(name = PARAM_LABEL_WORD_REGEX, mandatory = true, defaultValue = ".*_(.*)")
	protected String labelWordRegex;

	/**
	 * Use Lemma instead of Token for disambiguable check
	 */
	public static final String PARAM_USE_LEMMA_FOR_DISAMBIGUABLE_CHECK = "useLemmaForDisambiguableCheck";
	@ConfigurationParameter(name = PARAM_USE_LEMMA_FOR_DISAMBIGUABLE_CHECK, mandatory = true, defaultValue = "true")
	protected boolean useLemmaForDisambiguableCheck;

	/**
	 * "POS startsWith" check for disambiguable
	 */
	public static final String PARAM_POS_STARTS_WITH = "posStartsWith";
	@ConfigurationParameter(name = PARAM_POS_STARTS_WITH, mandatory = true, defaultValue = "N")
	protected String posStartsWith;

	private HashSet<String> disambiguableWordsList;
	private Pattern labelWordPattern;

	private boolean noDisambigInWholeDoc;
	private long documentsSeen;
	private long documentsWithoutDisambig;
	private long documentsFinished;

	private HashMap<String, Integer> disambiguableWords;
	private HashMap<String, Integer> disambiguatedSenses;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		documentsSeen = 0;
		documentsWithoutDisambig = 0;
		documentsFinished = 0;

		disambiguableWords = new HashMap<>();
		disambiguatedSenses = new HashMap<>();

		disambiguableWordsList = new HashSet<>();

		labelWordPattern = Pattern.compile(labelWordRegex);

		System.out.println("Reading disambiguable words list [" + disambig_location + "]...");
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(disambig_location), Charset.forName("UTF-8")));
			String line;
			while ((line = reader.readLine()) != null) {
				disambiguableWordsList.add(line.trim());
			}
			reader.close();
		} catch (Exception ex) {

			throw new ResourceInitializationException("failed reading disambiguable words list...", null, ex);
		}

		System.out.println("Reading disambiguable words list done.");
	}

	@Override
	public void destroy() {
		if (!outputStatsFile.isEmpty()) {
			try {
				System.out.println("writing stats file [" + outputStatsFile + "]");
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputStatsFile), Charset.forName("UTF-8")));

				writer.println("disambiguable word\tnumber");
				long num = 0;
				for (HashMap.Entry<String, Integer> entry : disambiguableWords.entrySet()) {
					writer.println(entry.getKey() + "\t" + entry.getValue());
					num += entry.getValue();
				}
				writer.println();
				writer.println("disambiguable words num\t" + num);
				writer.println("unique disambiguable words\t" + disambiguableWords.size());

				writer.println();
				writer.println();
				writer.println();

				writer.println("senses\tnumber");
				num = 0;
				for (HashMap.Entry<String, Integer> entry : disambiguatedSenses.entrySet()) {
					writer.println(entry.getKey() + "\t" + entry.getValue());
					num += entry.getValue();
				}
				writer.println();
				writer.println("senses num\t" + num);
				writer.println("unique senses\t" + disambiguatedSenses.size());

				writer.flush();
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		super.destroy();
	}

	private boolean isDisambiguablePOS(String pos) {
		return pos.startsWith(posStartsWith);
	}

	private boolean isDisambiguableWord(String word) {
		return disambiguableWordsList.contains(word);
	}

	@Override
	protected void processCoveredWithFastText(JCas jCas, Annotation ref) {

		// Dismbiguierbare Wörter finden
		HashMap<String, ArrayList<Token>> disambigWords = new HashMap<>();

		// Alle Token durchgehen...
		Collection<Token> allTokens;
		if (ref != null) {
			allTokens = JCasUtil.selectCovered(Token.class, ref);
		} else {
			allTokens = JCasUtil.select(jCas, Token.class);
		}
		for (Token token : allTokens) {
			String posStr = token.getPos().getPosValue();
			String checkDisambiguableStr = useLemmaForDisambiguableCheck ? token.getLemma().getValue() : token.getCoveredText();

			// Wenn das Wort disambiguierbar ist...
			if (isDisambiguablePOS(posStr) && isDisambiguableWord(checkDisambiguableStr)) {
				if (!disambigWords.containsKey(checkDisambiguableStr)) {
					disambigWords.put(checkDisambiguableStr, new ArrayList<>());
				}
				disambigWords.get(checkDisambiguableStr).add(token);
			}
		}

		// Überhaupt Label gefunden?
		if (!disambigWords.isEmpty()) {

			noDisambigInWholeDoc = false;

			// Anfrage String zusammenbauen:
			// __label__A [__label__B __label__...] der ganze Text...
			StringBuilder text = new StringBuilder();
			for (HashMap.Entry<String, ArrayList<Token>> entry : disambigWords.entrySet()) {
				text.append(labelPrefix).append(entry.getKey()).append(" ");

				if (!outputStatsFile.isEmpty()) {
					if (!disambiguableWords.containsKey(entry.getKey())) {
						disambiguableWords.put(entry.getKey(), entry.getValue().size());
					} else {
						disambiguableWords.put(entry.getKey(), disambiguableWords.get(entry.getKey()) + entry.getValue().size());
					}
				}
			}
			text.append(getText(jCas, ref, useLemma, false, true, false, false));

			// Anfrage abschicken
			try {
				// TODO Unterstützt nur 1 Model aktuell!
				FastTextResult ftResult = fasttext.input(jCas.getDocumentLanguage(), text.toString()).get(0);
				ArrayList<ProbabilityLabel> labels = ftResult.getSortedResults(false);

				// Insgesamt nur "fasttextK" Tags ausgeben, aber für jedes Label zählen!
				// intern werden aber immer alle berechnet da die Modell-Ausgabe sonst den Process blockiert.
				HashMap<String, Integer> num = new HashMap<>();
				for (ProbabilityLabel result : labels) {

					// n/a Outputs weglassen
					if (!result.getLabel().equals("n/a")) {

						// Label -> __label__ entfernen und dann ID überspringen
						String label;
						Matcher matcher = labelWordPattern.matcher(result.getLabel());
						if (matcher.matches()) {
							label = matcher.group(1);
						} else {
							throw new AnalysisEngineProcessException("failed to get word from label", null);
						}

						int n = 0;
						if (num.containsKey(label)) {
							n = num.get(label);
						}
						n++;

						if (n > fasttextK) {
							continue;
						}

						num.put(label, n);

						for (Token token : disambigWords.get(label)) {
							if (token != null) {
								CategoryCoveredTagged cat = new CategoryCoveredTagged(jCas, token.getBegin(), token.getEnd());
								cat.setValue(result.getLabel());
								cat.setScore(result.getLogProb());
								cat.setTags(tags);
								cat.setRef(token);
								cat.addToIndexes();

								if (!outputStatsFile.isEmpty()) {
									if (!disambiguatedSenses.containsKey(result.getLabel())) {
										disambiguatedSenses.put(result.getLabel(), 1);
									} else {
										disambiguatedSenses.put(result.getLabel(), disambiguatedSenses.get(result.getLabel()) + 1);
									}
								}
							}
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	@Override
	public void process(JCas jCas) throws AnalysisEngineProcessException {

		noDisambigInWholeDoc = true;
		documentsSeen++;

		super.process(jCas);

		if (noDisambigInWholeDoc) {
			documentsWithoutDisambig++;
		}

		documentsFinished++;

		System.out.println("documents: seen=" + documentsSeen + ", finished=" + documentsFinished + ", no_disambig_words=" + documentsWithoutDisambig);
	}
}
