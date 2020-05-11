package org.hucompute.services.uima.database.xmi;

import static org.apache.commons.io.IOUtils.closeQuietly;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.resource.ResourceInitializationException;
import org.dkpro.core.api.io.ResourceCollectionReaderBase;
import org.dkpro.core.api.resources.CompressionUtils;
import org.xml.sax.SAXException;


/**
 * Reader for UIMA XMI files.
 */
@TypeCapability(
		outputs={
		"de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData"})
public class XmiReaderModified
extends ResourceCollectionReaderBase
{
	public static final String PARAM_LOG_FILE_LOCATION = "logFile";
	@ConfigurationParameter(name = PARAM_LOG_FILE_LOCATION,mandatory = false)
	public File logFile; 

	public StopWatch stopWatch;
	int processed = 0;

	@Override
	public void initialize(UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
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
		if(processed++%100==0){
			System.out.println("Reader processed documentscount: " + processed +" in " + getSeconds(stopWatch.toString()) +" ms");
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
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * In lenient mode, unknown types are ignored and do not cause an exception to be thrown.
	 */
	public static final String PARAM_LENIENT = "lenient";
	@ConfigurationParameter(name=PARAM_LENIENT, mandatory=true, defaultValue="false")
	private boolean lenient;

	@Override
	public void getNext(CAS aCAS)
			throws IOException, CollectionException
	{
		//start monitoring process
		resumeWatch();
		try {
		Resource res = nextFile();
		initCas(aCAS, res);
		InputStream is = null;
		try {

			is = CompressionUtils.getInputStream(res.getLocation(), res.getInputStream());
			XmiCasDeserializer.deserialize(is, aCAS, lenient);
			if (getLanguage() != null) {
				aCAS.setDocumentLanguage(getLanguage());
			}
		} catch (SAXException e) {
			suspendWatch();
			getNext(aCAS);
			e.printStackTrace();
			return;
		}		
		finally {
			closeQuietly(is);
		}
		} catch (Exception e) { e.printStackTrace();}
		//stop monitoring process
		suspendWatch();
		log();
	}

}
