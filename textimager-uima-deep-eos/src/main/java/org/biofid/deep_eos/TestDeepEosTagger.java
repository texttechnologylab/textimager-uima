package org.biofid.deep_eos;


import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

/**
 * Created on 09.10.19.
 */
public class TestDeepEosTagger {
	
	public static void main(String[] args) throws UIMAException {
		JCas jCas = JCasFactory.createText("Psychotria viridis wächst als zwei bis vier Meter hoher Baum mit glatter Borke. Die gegenständigen Blätter sind sitzend oder bis zu acht Millimeter lang gestielt. Die Blattspreite ist elliptisch mit spitzem Ende und keilförmiger Basis. Im getrockneten Zustand werden sie rotbraun bis grünbraun. Die Nebenblätter sind eiförmig, leicht zugespitzt und im Zentrum dunkler. Sie fallen später ab und hinterlassen am Stängel Narben zwischen zwei benachbarten Blättern. Die Blütenstände sind dreifach verzweigte Rispen oder kompakte Zymen, die terminal oder scheinbar achselständig stehen. Dabei sind als charakteristisches Merkmal der Art alle sekundären Achsen außer den jeweils ersten zwei stark verkürzt. Die sitzenden Blüten haben einen becherförmigen Kelch von etwa 0,5 Millimetern Länge. Selten sind fünf Kelchblätter als Einzelblätter erkennbar. Die Blütenkrone ist als weiße, zylindrische Röhre von ein bis 1,5 Millimeter Länge ausgeprägt. Sie ist im Inneren stark behaart und endet in fünf lanzettlichen Spitzen. Fünf Staubgefäße erreichen ebenso wie der Griffel eine Länge von etwa 2,5 Millimetern. Die Frucht ist eine bei Reife rote Steinfrucht, die sich beim Trocknen rotbraun verfärbt. Sie wird vom Kelch gekrönt und weist auf der Oberseite vier bis fünf, auf der Unterseite zwei Furchen auf.");
		AnalysisEngine analysisEngine = AnalysisEngineFactory.createEngine(DeepEosTagger.class,
				DeepEosTagger.PARAM_PYTHON_HOME, "/home/stud_homes/s3676959/anaconda3/envs/keras/",
				DeepEosTagger.PARAM_MODEL_NAME, "de",
				DeepEosTagger.PARAM_VERBOSE, true);
		SimplePipeline.runPipeline(jCas, analysisEngine);
		
	}
}
