package org.hucompute.textimager.uima.local;

import de.tudarmstadt.ukp.dkpro.core.tokit.BreakIteratorSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.dkpro.core.api.resources.CompressionMethod;
import org.dkpro.core.io.text.TextReader;
import org.dkpro.core.io.xmi.XmiReader;
import org.dkpro.core.io.xmi.XmiWriter;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotator;
import org.hucompute.textimager.fasttext.labelannotator.LabelAnnotatorDDCMul;
import org.hucompute.textimager.uima.spacy.SpaCyMultiTagger3;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.apache.uima.fit.factory.AnalysisEngineFactory.createEngineDescription;

public class SimpleLocalPipelineSpacyDdcT2W {
    public static void main(String[] args) throws UIMAException, IOException {
        if (args.length != 6) {
            System.out.println("Usage:");
            System.out.println("  fileType language inputDir outputDir dockerPort checkParentDir");
            System.exit(1);
        }

        String fileType = args[0];                                  // txt, xmi
        String language = args[1];                                  // de, en, ...
        Path inputDir = Paths.get(args[2]);                         // path
        Path outputDir = Paths.get(args[3]);                        // path
        int dockerPort = Integer.parseInt(args[4]);                 // 8462
        boolean checkParentDir = Boolean.parseBoolean(args[5]);     // true

        System.out.println("lang: " + language);
        System.out.println("in: " + inputDir);
        System.out.println("out: " + outputDir);
        System.out.println("docker port: " + dockerPort);

        CollectionReader reader;
        if (fileType.equalsIgnoreCase("xmi")) {
            System.out.println("XMI input");
            reader = CollectionReaderFactory.createReader(
                    XmiReader.class
                    , XmiReader.PARAM_SOURCE_LOCATION, inputDir.toString()
                    , XmiReader.PARAM_TARGET_LOCATION, outputDir.toString()
                    , XmiReader.PARAM_PATTERNS, "**/*.xmi*"
                    , XmiReader.PARAM_LENIENT, false
                    , XmiReader.PARAM_ADD_DOCUMENT_METADATA, false
                    , XmiReader.PARAM_OVERRIDE_DOCUMENT_METADATA, false
                    , XmiReader.PARAM_MERGE_TYPE_SYSTEM, false
                    , XmiReader.PARAM_USE_DEFAULT_EXCLUDES, true
                    , XmiReader.PARAM_INCLUDE_HIDDEN, false
                    , XmiReader.PARAM_LOG_FREQ, 1
                    , XmiReader.PARAM_CHECK_PARENT_OF_SOURCE_LOCATION_FOR_SKIP_FILES, checkParentDir
            );
        }
        else { // txt
            System.out.println("TXT input");
            reader = CollectionReaderFactory.createReader(
                    TextReader.class
                    , TextReader.PARAM_SOURCE_LOCATION, inputDir.toString()
                    , TextReader.PARAM_TARGET_LOCATION, outputDir.toString()
                    , TextReader.PARAM_PATTERNS, "**/*.txt"
                    , TextReader.PARAM_SOURCE_ENCODING, "UTF-8"
                    , TextReader.PARAM_USE_DEFAULT_EXCLUDES, true
                    , TextReader.PARAM_INCLUDE_HIDDEN, false
                    , TextReader.PARAM_LOG_FREQ, 1
                    , TextReader.PARAM_LANGUAGE, language
                    , TextReader.PARAM_CHECK_PARENT_OF_SOURCE_LOCATION_FOR_SKIP_FILES, checkParentDir
            );
        }

        AnalysisEngineDescription writer = createEngineDescription(
                XmiWriter.class
                , XmiWriter.PARAM_TARGET_LOCATION, outputDir.toString()
                , XmiWriter.PARAM_VERSION, "1.1"
                , XmiWriter.PARAM_COMPRESSION, CompressionMethod.GZIP
                , XmiWriter.PARAM_PRETTY_PRINT, true
                , XmiWriter.PARAM_OVERWRITE, true
        );

        AnalysisEngineDescription ddc2;
        if (language.equalsIgnoreCase("de")) {
            System.out.println("ddc: de");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "de,/resources/nlp/models/categorization/ddc/ddc_2018/wikipedia_de.v8.lemma.nopunct.pos.no_functionwords_gnd_ddc.v4.with_categories-lr0.2-lrUR150-minC5-dim100-ep10000-vec_vec_token_lemmapos.vec.epoch5000.bin,98"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, true
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
            );
        }
        else if (language.equalsIgnoreCase("en")) {
            System.out.println("ddc: en");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "en,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_en.v8.lemma.nopunct_gnd_ddc.v3.with_wikidata_model_dim100_pretreined-glove.6B.100d.txt_epoch100000.epoch10000.bin,95"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
            );
        }
        else if (language.equalsIgnoreCase("ar") || language.equalsIgnoreCase("es") || language.equalsIgnoreCase("fr") || language.equalsIgnoreCase("tr")) {
            System.out.println("ddc: ar, es, fr, tr");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "ar,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_ar.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.ar.vec.epoch100.bin,96,es,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_es.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.es.vec.bin,95,fr,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_fr.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.fr.vec.epoch5000.bin,95,tr,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_tr.v8.token_gnd_ddc.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.tr.vec.epoch100.bin,93"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, false
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, false
                    , LabelAnnotator.PARAM_LAZY_LOAD, true
                    , LabelAnnotator.PARAM_LAZY_LOAD_MAX, 1
            );
        }
        else {
            System.out.println("ddc: others");
            ddc2 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_2018_03_22_test_every_epoch_for_ducc/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "an,/resources/nlp/models/categorization/ddc/ddc_2018_andere/anwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,bar,/resources/nlp/models/categorization/ddc/ddc_2018_andere/barwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,81,bn,/resources/nlp/models/categorization/ddc/ddc_2018_andere/bnwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,85,bs,/resources/nlp/models/categorization/ddc/ddc_2018_andere/bswiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,87,ckb,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ckbwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,82,da,/resources/nlp/models/categorization/ddc/ddc_2018_andere/dawiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,el,/resources/nlp/models/categorization/ddc/ddc_2018_andere/elwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,fa,/resources/nlp/models/categorization/ddc/ddc_2018_andere/fawiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,94,he,/resources/nlp/models/categorization/ddc/ddc_2018_andere/hewiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,hi,/resources/nlp/models/categorization/ddc/ddc_2018_andere/hiwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,86,hu,/resources/nlp/models/categorization/ddc/ddc_2018_andere/huwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,id,/resources/nlp/models/categorization/ddc/ddc_2018_andere/idwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,it,/resources/nlp/models/categorization/ddc/ddc_2018_andere/itwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,ja,/resources/nlp/models/categorization/ddc/ddc_2018_andere/jawiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,kn,/resources/nlp/models/categorization/ddc/ddc_2018_andere/knwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,79,ko,/resources/nlp/models/categorization/ddc/ddc_2018_andere/kowiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,li,/resources/nlp/models/categorization/ddc/ddc_2018_andere/liwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,lv,/resources/nlp/models/categorization/ddc/ddc_2018_andere/lvwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,89,mk,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mkwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,88,ml,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mlwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,84,mn,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mnwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,74,mr,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mrwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,83,pt,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ptwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,ro,/resources/nlp/models/categorization/ddc/ddc_2018_andere/rowiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,93,ru,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ruwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,96,sh,/resources/nlp/models/categorization/ddc/ddc_2018_andere/shwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,94,simple,/resources/nlp/models/categorization/ddc/ddc_2018_andere/simplewiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,92,si,/resources/nlp/models/categorization/ddc/ddc_2018_andere/siwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,sr,/resources/nlp/models/categorization/ddc/ddc_2018_andere/srwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,te,/resources/nlp/models/categorization/ddc/ddc_2018_andere/tewiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,80,th,/resources/nlp/models/categorization/ddc/ddc_2018_andere/thwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,86,ur,/resources/nlp/models/categorization/ddc/ddc_2018_andere/urwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,82,vi,/resources/nlp/models/categorization/ddc/ddc_2018_andere/viwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,88,zh,/resources/nlp/models/categorization/ddc/ddc_2018_andere/zhwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,91,pl,/resources/nlp/models/categorization/ddc/models_20191001/plwiki.token_gnd_ddc.v5.with_categories-lr0.2-lrUR150-minC5-dim300-ep1000-vec-cc.pl.300.vec.best_epoch.bin,95"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc2"
                    , LabelAnnotator.PARAM_USE_LEMMA, false
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, false
                    , LabelAnnotator.PARAM_LAZY_LOAD, true
                    , LabelAnnotator.PARAM_LAZY_LOAD_MAX, 1
            );
        }

        AnalysisEngineDescription ddc3;
        if (language.equalsIgnoreCase("de")) {
            System.out.println("ddc3: de");
            ddc3 = createEngineDescription(
                    LabelAnnotatorDDCMul.class
                    , LabelAnnotatorDDCMul.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotatorDDCMul.PARAM_LANGUAGE_MODELS_LABELS, "de,/resources/nlp/models/categorization/ddc/ddc_2018/wikipedia_de.v8.lemma.nopunct.pos.no_functionwords_gnd_ddc.v4.with_categories-lr0.2-lrUR150-minC5-dim100-ep10000-vec_vec_token_lemmapos.vec.epoch5000.bin,98"
                    , LabelAnnotatorDDCMul.PARAM_LANGUAGE_MODELS_LABELS_DDC3, "de,/resources/nlp/models/categorization/ddc/ddc_2018/wikipedia_de.v8.lemma.nopunct.pos.no_functionswords_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep10000-vec_vec_token_lemmapos.vec.epoch5000.bin,635"
                    , LabelAnnotatorDDCMul.PARAM_FASTTEXT_K, 1000
                    , LabelAnnotatorDDCMul.PARAM_CUTOFF, false
                    , LabelAnnotatorDDCMul.PARAM_SELECTION, "text"
                    , LabelAnnotatorDDCMul.PARAM_TAGS, "ddc3"
                    , LabelAnnotatorDDCMul.PARAM_USE_LEMMA, true
                    , LabelAnnotatorDDCMul.PARAM_ADD_POS, true
                    , LabelAnnotatorDDCMul.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotatorDDCMul.PARAM_REMOVE_FUNCTIONWORDS, true
                    , LabelAnnotatorDDCMul.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotatorDDCMul.PARAM_REMOVE_OLD_SCORES, true
            );
        }
        else if (language.equalsIgnoreCase("en")) {
            System.out.println("ddc3: en");
            ddc3 = createEngineDescription(
                    LabelAnnotatorDDCMul.class
                    , LabelAnnotatorDDCMul.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotatorDDCMul.PARAM_LANGUAGE_MODELS_LABELS, "en,/resources/nlp/models/categorization/ddc/ddc2_2018/wikipedia_en.v8.lemma.nopunct_gnd_ddc.v3.with_wikidata_model_dim100_pretreined-glove.6B.100d.txt_epoch100000.epoch10000.bin,95"
                    , LabelAnnotatorDDCMul.PARAM_LANGUAGE_MODELS_LABELS_DDC3, "en,/resources/nlp/models/categorization/ddc/ddc3_2018/wikipedia_en.v8.lemma.nopunct_gnd_ddc_full.v5.with_categories_dim300-ep10000-vec_wiki.en.vec.bin,601"
                    , LabelAnnotatorDDCMul.PARAM_FASTTEXT_K, 1000
                    , LabelAnnotatorDDCMul.PARAM_CUTOFF, false
                    , LabelAnnotatorDDCMul.PARAM_SELECTION, "text"
                    , LabelAnnotatorDDCMul.PARAM_TAGS, "ddc3"
                    , LabelAnnotatorDDCMul.PARAM_USE_LEMMA, true
                    , LabelAnnotatorDDCMul.PARAM_ADD_POS, false
                    , LabelAnnotatorDDCMul.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotatorDDCMul.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotatorDDCMul.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotatorDDCMul.PARAM_REMOVE_OLD_SCORES, true
            );
        }
        else if (language.equalsIgnoreCase("ar") || language.equalsIgnoreCase("es") || language.equalsIgnoreCase("fr") || language.equalsIgnoreCase("tr")) {
            System.out.println("ddc3: ar, es, fr, tr");
            ddc3 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_original_for_ducc_annotators/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "fr,/resources/nlp/models/categorization/ddc/ddc3_2018/wikipedia_fr.v8.token_gnd_ddc_full.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.fr.vec.epoch5000.bin,618,ar,/resources/nlp/models/categorization/ddc/ddc3_2018/wikipedia_ar.v8.token_gnd_ddc_full.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.ar.vec.epoch100.bin,589,tr,/resources/nlp/models/categorization/ddc/ddc3_2018/wikipedia_tr.v8.token_gnd_ddc_full.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.tr.vec.bin,566,es,/resources/nlp/models/categorization/ddc/ddc3_2018/wikipedia_es.v8.token_gnd_ddc_full.v4_lr0.1-lrUR150-dim300-ws5-ep10000-minC5-minCL0-neg7-ngrams1-bucket2000000-minn0-maxn0-t0.0001-lossns-vec-wiki.es.vec.epoch500.bin,613"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 1000
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc3"
                    , LabelAnnotator.PARAM_USE_LEMMA, false
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, false
                    , LabelAnnotator.PARAM_LAZY_LOAD, true
                    , LabelAnnotator.PARAM_LAZY_LOAD_MAX, 1
            );
        }
        else {
            System.out.println("ddc3: others");
            ddc3 = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText_2018_03_22_test_every_epoch_for_ducc/fasttext"
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "bn,/resources/nlp/models/categorization/ddc/ddc_2018_andere/bnwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,456,bs,/resources/nlp/models/categorization/ddc/ddc_2018_andere/bswiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,474,da,/resources/nlp/models/categorization/ddc/ddc_2018_andere/dawiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,570,el,/resources/nlp/models/categorization/ddc/ddc_2018_andere/elwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,535,fa,/resources/nlp/models/categorization/ddc/ddc_2018_andere/fawiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,578,he,/resources/nlp/models/categorization/ddc/ddc_2018_andere/hewiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,575,hi,/resources/nlp/models/categorization/ddc/ddc_2018_andere/hiwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,501,hu,/resources/nlp/models/categorization/ddc/ddc_2018_andere/huwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,561,id,/resources/nlp/models/categorization/ddc/ddc_2018_andere/idwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,567,it,/resources/nlp/models/categorization/ddc/ddc_2018_andere/itwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,600,ja,/resources/nlp/models/categorization/ddc/ddc_2018_andere/jawiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,593,ko,/resources/nlp/models/categorization/ddc/ddc_2018_andere/kowiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,577,lv,/resources/nlp/models/categorization/ddc/ddc_2018_andere/lvwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,509,mk,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mkwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,490,ml,/resources/nlp/models/categorization/ddc/ddc_2018_andere/mlwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,471,pt,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ptwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,589,ro,/resources/nlp/models/categorization/ddc/ddc_2018_andere/rowiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,561,ru,/resources/nlp/models/categorization/ddc/ddc_2018_andere/ruwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,613,sh,/resources/nlp/models/categorization/ddc/ddc_2018_andere/shwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,560,simple,/resources/nlp/models/categorization/ddc/ddc_2018_andere/simplewiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,558,sr,/resources/nlp/models/categorization/ddc/ddc_2018_andere/srwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,573,te,/resources/nlp/models/categorization/ddc/ddc_2018_andere/tewiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,409,th,/resources/nlp/models/categorization/ddc/ddc_2018_andere/thwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,517,ur,/resources/nlp/models/categorization/ddc/ddc_2018_andere/urwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,457,vi,/resources/nlp/models/categorization/ddc/ddc_2018_andere/viwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,539,zh,/resources/nlp/models/categorization/ddc/ddc_2018_andere/zhwiki.token_gnd_ddc_full.v5.with_categories-lr0.2-lrUR150-minC5-dim100-ep1000.best_epoch.bin,587"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 1000
                    , LabelAnnotator.PARAM_CUTOFF, false
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_TAGS, "ddc3"
                    , LabelAnnotator.PARAM_USE_LEMMA, false
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, false
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, false
                    , LabelAnnotator.PARAM_LAZY_LOAD, true
                    , LabelAnnotator.PARAM_LAZY_LOAD_MAX, 1
            );
        }

        AnalysisEngineDescription text2wiki_thema;
        AnalysisEngineDescription text2wiki_raum;
        AnalysisEngineDescription text2wiki_zeit;
        if (language.equalsIgnoreCase("de")) {
            System.out.println("text2wiki: de");

            text2wiki_thema = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "de,/resources/nlp/models/categorization/text2cwc/models/de/20190919_thema.no_functionswords.ddc2.top_scorex.train_d100_searchtime360000_vec-sz_lemma_mikolov.bin,3806"
                    , LabelAnnotator.PARAM_TAGS, "text2cwc_thema"
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText-0.1.0_ducc/fasttext"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100000
                    , LabelAnnotator.PARAM_CUTOFF, true
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
                    , LabelAnnotator.PARAM_APPEND_DDC, "ddc2"
                    , LabelAnnotator.PARAM_APPEND_DDC_VARIANT, "top_text_length_x"
                    , LabelAnnotator.PARAM_DDC_CLASS_NAMES_FILENAME, "de:/resources/nlp/models/categorization/text2cwc/ddc-mappings/de-ddc-mapping.txt,en:/resources/nlp/models/categorization/text2cwc/ddc-mappings/en-ddc-mapping.txt"
            );

            text2wiki_raum = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "de,/resources/nlp/models/categorization/text2cwc/models/de/20190925_raum.no_functionswords.ddc2.top_scorex.train_d100_ws5_minc1_neg5_losssoftmax_wng2_bucket664098_minn0_maxn0_lrur100_t0.0001_ep100_vec-sz_lemma_mikolov.bin,208"
                    , LabelAnnotator.PARAM_TAGS, "text2cwc_raum"
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText-0.1.0_ducc/fasttext"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100000
                    , LabelAnnotator.PARAM_CUTOFF, true
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
                    , LabelAnnotator.PARAM_APPEND_DDC, "ddc2"
                    , LabelAnnotator.PARAM_APPEND_DDC_VARIANT, "top_text_length_x"
                    , LabelAnnotator.PARAM_DDC_CLASS_NAMES_FILENAME, "de:/resources/nlp/models/categorization/text2cwc/ddc-mappings/de-ddc-mapping.txt"
            );

            text2wiki_zeit = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "de,/resources/nlp/models/categorization/text2cwc/models/de/20190925_zeit.no_functionswords.ddc2.top_scorex.train_d100_ws5_minc1_neg5_losssoftmax_wng2_bucket664098_minn0_maxn0_lrur100_t0.0001_ep100_vec-sz_lemma_mikolov.bin,358"
                    , LabelAnnotator.PARAM_TAGS, "text2cwc_zeit"
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText-0.1.0_ducc/fasttext"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100000
                    , LabelAnnotator.PARAM_CUTOFF, true
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
                    , LabelAnnotator.PARAM_APPEND_DDC, "ddc2"
                    , LabelAnnotator.PARAM_APPEND_DDC_VARIANT, "top_text_length_x"
                    , LabelAnnotator.PARAM_DDC_CLASS_NAMES_FILENAME, "de:/resources/nlp/models/categorization/text2cwc/ddc-mappings/de-ddc-mapping.txt"
            );
        }
        else {
            System.out.println("text2wiki: en");

            text2wiki_thema = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "en,/resources/nlp/models/categorization/text2cwc/models/en/20191011_thema.no_functionswords.ddc2.top_scorex.en.train_d300_ws5_minc1_neg5_losssoftmax_wng2_bucket664098_minn0_maxn0_lrur100_t0.0001_ep100_vec-cc.en.300.vec.bin,3806"
                    , LabelAnnotator.PARAM_TAGS, "text2cwc_thema"
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText-0.1.0_ducc/fasttext"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100000
                    , LabelAnnotator.PARAM_CUTOFF, true
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
            );

            text2wiki_raum = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "en,/resources/nlp/models/categorization/text2cwc/models/en/20200221_raum.no_functionswords.ddc2.top_scorex.en_d300_ws5_minc1_neg5_losssoftmax_wng2_bucket664098_minn0_maxn0_lrur100_t0.0001_ep100_vec-cc.en.300.vec.bin,208"
                    , LabelAnnotator.PARAM_TAGS, "text2cwc_raum"
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText-0.1.0_ducc/fasttext"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100000
                    , LabelAnnotator.PARAM_CUTOFF, true
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
            );

            text2wiki_zeit = createEngineDescription(
                    LabelAnnotator.class
                    , LabelAnnotator.PARAM_LANGUAGE_MODELS_LABELS, "en,/resources/nlp/models/categorization/text2cwc/models/en/20200221_zeit.no_functionswords.ddc2.top_scorex.en_d300_ws5_minc1_neg5_losssoftmax_wng2_bucket664098_minn0_maxn0_lrur100_t0.0001_ep100_vec-cc.en.300.vec.bin,338"
                    , LabelAnnotator.PARAM_TAGS, "text2cwc_zeit"
                    , LabelAnnotator.PARAM_FASTTEXT_LOCATION, "/resources/nlp/bin/categorization/fastText-0.1.0_ducc/fasttext"
                    , LabelAnnotator.PARAM_FASTTEXT_K, 100000
                    , LabelAnnotator.PARAM_CUTOFF, true
                    , LabelAnnotator.PARAM_SELECTION, "text"
                    , LabelAnnotator.PARAM_USE_LEMMA, true
                    , LabelAnnotator.PARAM_ADD_POS, false
                    , LabelAnnotator.PARAM_POSMAP_LOCATION, "/resources/nlp/models/categorization/am_posmap.txt"
                    , LabelAnnotator.PARAM_REMOVE_PUNCT, true
                    , LabelAnnotator.PARAM_REMOVE_FUNCTIONWORDS, true
            );
        }

        AnalysisEngineDescription segmenter;
        if (language.equalsIgnoreCase("ja")) {
            System.out.println("segmenter: break");
            segmenter = createEngineDescription(
                    BreakIteratorSegmenter.class
            );
        }
        else {
            System.out.println("segmenter: spacy");
            segmenter = createEngineDescription(
                    SpaCyMultiTagger3.class
                    , SpaCyMultiTagger3.PARAM_DOCKER_HOST_PORT, dockerPort
                    , SpaCyMultiTagger3.PARAM_DOCKER_REGISTRY, "141.2.89.20:5000"
                    , SpaCyMultiTagger3.PARAM_DOCKER_IMAGE_TAG, "0.8"
            );
        }

        SimplePipeline.runPipeline(reader, segmenter, ddc2, ddc3, text2wiki_thema, text2wiki_raum, text2wiki_zeit, writer);

        System.out.println("lang: " + language);
        System.out.println("in: " + inputDir);
        System.out.println("out: " + outputDir);
    }
}