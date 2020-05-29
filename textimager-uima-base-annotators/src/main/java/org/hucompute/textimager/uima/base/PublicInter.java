package org.hucompute.textimager.uima.base;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import jep.JepException;
import jep.SharedInterpreter;
import jep.MainInterpreter;
import jep.PyConfig;

public class PublicInter {
	public static String test = "test";
	
	
	public static SharedInterpreter inter;
	
	public static final String PARAM_PYTHON_HOME = "pythonHome";
	@ConfigurationParameter(name = PARAM_PYTHON_HOME, mandatory = false)
	protected static String pythonHome = "C:\\Users\\makra\\AppData\\Local\\Programs\\Python\\Python38" ;
	
	// muss noch außerhalb gesetzt werden können
	
	/**
	 * The path to libjep lib
	 */
	
	public static final String PARAM_LIBJEP_PATH = "libjepPath";
	@ConfigurationParameter(name = PARAM_LIBJEP_PATH, mandatory = false)
	protected static String libjepPath;
	
	
	public static void setUpInter(String path) throws ResourceInitializationException {
		
	try {
			pythonHome = path;
			
			// kopiert aus JepAnnotator
			
			if (!pythonHome.isEmpty()) {
				// Workaround for loading python library files
				File libDir = Paths.get(pythonHome, "lib").toAbsolutePath().toFile();
				FileFilter libpythonFilter = new RegexFileFilter("libpython*");
				for (File file : libDir.listFiles(libpythonFilter)) {
					System.load(file.getAbsolutePath());
				}
				
				PyConfig config = new PyConfig();
				config.setPythonHome(pythonHome);
				try {
					MainInterpreter.setInitParams(config);
				} catch (JepException e) {
				
				}
			}
			
			if (libjepPath != null && !libjepPath.isEmpty()) {
				MainInterpreter.setJepLibraryPath(libjepPath);
			}
			
			inter = new SharedInterpreter();
		
		
		}catch (Exception ex) {
			ex.printStackTrace();
			throw new ResourceInitializationException(ex);
		}
	}
}

