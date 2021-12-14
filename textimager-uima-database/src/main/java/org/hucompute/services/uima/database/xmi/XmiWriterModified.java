package org.hucompute.services.uima.database.xmi;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.StopWatch;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.cas.impl.XmiCasSerializer;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.descriptor.TypeCapability;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.TypeSystemUtil;
import org.dkpro.core.api.io.JCasFileWriter_ImplBase;
import org.dkpro.core.api.resources.CompressionUtils;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * UIMA XMI format writer.
 */
@TypeCapability(
        inputs={
                "de.tudarmstadt.ukp.dkpro.core.api.metadata.type.DocumentMetaData"})
public class XmiWriterModified
	extends JCasFileWriter_ImplBase
{
	/**
	 * Location to write the type system to. If this is not set, a file called typesystem.xml will
	 * be written to the XMI output path. If this is set, it is expected to be a file relative
	 * to the current work directory or an absolute file.
	 * <br>
	 * If this parameter is set, the {@link #PARAM_COMPRESSION} parameter has no effect on the
	 * type system. Instead, if the file name ends in ".gz", the file will be compressed,
	 * otherwise not.
	 */
	public static final String PARAM_TYPE_SYSTEM_FILE = "typeSystemFile";
	@ConfigurationParameter(name=PARAM_TYPE_SYSTEM_FILE, mandatory=false)
	private File typeSystemFile;

	private boolean typeSystemWritten;

	public static final String PARAM_LOG_FILE_LOCATION = "logFile";
	@ConfigurationParameter(name = PARAM_LOG_FILE_LOCATION,mandatory = false)
	public File logFile;

	public StopWatch stopWatch;
	int processed = 0;

	@Override
	public void initialize(UimaContext aContext)
		throws ResourceInitializationException
	{
		super.initialize(aContext);

		typeSystemWritten = false;
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
			e.printStackTrace();
		}
		return -1;
	}

    @Override
    public void process(JCas aJCas)
        throws AnalysisEngineProcessException
    {
    	resumeWatch();
        try (OutputStream docOS = getOutputStream(aJCas, ".xmi")) {
            XmiCasSerializer.serialize(aJCas.getCas(), docOS);

            if (!typeSystemWritten || typeSystemFile == null) {
                writeTypeSystem(aJCas);
                typeSystemWritten = true;
            }
        }
        catch (Exception e) {
            throw new AnalysisEngineProcessException(e);
        }
        suspendWatch();
        log();
    }

    private void writeTypeSystem(JCas aJCas)
        throws IOException, CASRuntimeException, SAXException
    {
		@SuppressWarnings("resource")
        OutputStream typeOS = null;

        try {
    		if (typeSystemFile != null) {
    		    typeOS = CompressionUtils.getOutputStream(typeSystemFile);
    		}
    		else {
    		    typeOS = getOutputStream("typesystem", ".xml");
    		}
			TypeSystemUtil.typeSystem2TypeSystemDescription(aJCas.getTypeSystem()).toXML(typeOS);
		}
		finally {
			closeQuietly(typeOS);
		}
	}
}
