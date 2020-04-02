package org.hucompute.textimager.uima.base;

import jep.JepException;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SharedInterpreter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import java.io.File;
import java.io.FileFilter;
import java.nio.file.Paths;

public abstract class JepAnnotator extends JCasAnnotator_ImplBase {
	/**
	 * The Python home directory
	 */
	public static final String PARAM_PYTHON_HOME = "pythonHome";
	@ConfigurationParameter(name = PARAM_PYTHON_HOME, mandatory = false)
	protected String pythonHome;
	
	/**
	 * The path to libjep lib
	 */
	public static final String PARAM_LIBJEP_PATH = "libjepPath";
	@ConfigurationParameter(name = PARAM_LIBJEP_PATH, mandatory = false)
	protected String libjepPath;
	
	protected SharedInterpreter interp;
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		System.out.println("init: " + this.getClass().getName());
		
		try {
			
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
			interp = new SharedInterpreter();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new ResourceInitializationException(ex);
		}
	}
	
	@Override
	public void destroy() {
		try {
			interp.close();
		} catch (JepException e) {
			e.printStackTrace();
		}
		
		super.destroy();
	}
}
