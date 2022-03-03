package org.hucompute.services.uima.database;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public abstract class AbstractWriter extends JCasConsumer_ImplBase {
	Logger logger;

	public static final String PARAM_LOG_FILE_LOCATION = "logFile";
	@ConfigurationParameter(name = PARAM_LOG_FILE_LOCATION,mandatory = false)
	public File logFile;

	public StopWatch stopWatch;
	public int processed = 0;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
		logger = Logger.getLogger(this.getClass());
		if(logFile != null)
			logFile.delete();
		stopWatch = new StopWatch();
		stopWatch.start();
		stopWatch.suspend();
	}

	public void resumeWatch(){
		stopWatch.resume();
	}

	public void suspendWatch(){
		stopWatch.suspend();
	}

	public void log(){
		if(processed++%10==0){
			logger.info("Writer processed documentscount: " + processed +" in " + getSeconds(stopWatch.toString()) +" ms");
			System.out.println("Writer processed documentscount: " + processed +" in " + getSeconds(stopWatch.toString()) +" ms");
			if(logFile != null)
				try {
					FileUtils.writeStringToFile(logFile,"\n"+processed+": " + getSeconds(stopWatch.toString()),"utf-8",true);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}

	private long getSeconds(String iso){
		try {
			DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
			Date reference = dateFormat.parse("00:00:00.000");
			Date date = dateFormat.parse(iso);
			return (date.getTime() - reference.getTime());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}

}
