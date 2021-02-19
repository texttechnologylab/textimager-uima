package org.hucompute.textimager.uima.biofid.gazetteer.run;

import org.apache.commons.cli.*;
import org.apache.uima.UIMAException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.hucompute.textimager.uima.gazetteer.SingleClassTreeGazetteer;
import org.dkpro.core.io.xmi.XmiReader;
import org.dkpro.core.io.xmi.XmiWriter;

import java.io.IOException;

/**
 * Created on 18.04.2019.
 */
public class TagTaxa {
	public static void main(String[] args) {
		
		Option inputOption = new Option("i", "input", true, "Input root path.");
		
		Option outputOption = new Option("o", "output", true, "Output path.");
		
		Option taxaOption = new Option("t", "taxa", true, "Taxa list path.");
		taxaOption.setArgs(Option.UNLIMITED_VALUES);
		
		Option minLen = new Option("m", "minlength", true, "Taxa minimum length. Default: 5.");
		minLen.setRequired(false);
		
		Options options = new Options();
		options.addOption("h", "help", false, "Print this message.");
		options.addOption(inputOption);
		options.addOption(outputOption);
		options.addOption(taxaOption);
		options.addOption(minLen);
		options.addOption("l", "lowercase", false, "Optional, if true use lowercase.");
		options.addOption("s", "allSkips", false, "Optional, if true use lowercase.");
		
		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine cmd = parser.parse(options, args);
			
			if (cmd.hasOption("h")) {
				printUsage(options);
				return;
			}
			
			String inputLocation = cmd.getOptionValue("i");
			String[] taxaLocations = cmd.getOptionValues("t");
			String outputLocation = cmd.getOptionValue("o");
			Boolean useLowerCase = cmd.hasOption("l");
			Boolean getAllSkips = cmd.hasOption("s");
			Integer minLength = cmd.hasOption("m") ? Integer.valueOf(cmd.getOptionValue("m")) : 5;
			
			CollectionReader collection = CollectionReaderFactory.createReader(
					XmiReader.class,
					XmiReader.PARAM_PATTERNS, "[+]*.xmi",
					XmiReader.PARAM_SOURCE_LOCATION, inputLocation,
					XmiReader.PARAM_LENIENT, true
//						, XmiReader.PARAM_LOG_FREQ, -1
			);
			
			AggregateBuilder ab = new AggregateBuilder();
			ab.add(AnalysisEngineFactory.createEngineDescription(
					AnalysisEngineFactory.createEngineDescription(SingleClassTreeGazetteer.class,
							SingleClassTreeGazetteer.PARAM_SOURCE_LOCATION, taxaLocations,
							SingleClassTreeGazetteer.PARAM_USE_LOWERCASE, useLowerCase,
							SingleClassTreeGazetteer.PARAM_MIN_LENGTH, minLength,
							SingleClassTreeGazetteer.PARAM_GET_ALL_SKIPS, getAllSkips)
			));
			ab.add(AnalysisEngineFactory.createEngineDescription(XmiWriter.class,
					XmiWriter.PARAM_TARGET_LOCATION, outputLocation,
					XmiWriter.PARAM_OVERWRITE, true
			));
			
			SimplePipeline.runPipeline(collection, ab.createAggregate());

			System.out.println("\nDone.");
		} catch (ParseException | UIMAException | IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void printUsage(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -cp $CP org.hucompute.textimager.biofid.TagTaxa",
				"TODO", //TODO
				options,
				"",
				true);
	}
}
