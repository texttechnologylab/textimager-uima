package org.hucompute.textimager.uima.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import jep.JepConfig;
import jep.JepException;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SubInterpreter;

public abstract class JepAnnotator extends JCasAnnotator_ImplBase {
	/**
	 * Directory structure:
	 * 
	 * ~
	 *  - .textimager
	 *    - conda
	 *      - [condaVersion]
	 *        - [installer.sh]
	 *        - miniconda
	 *          - envs
	 *            - [envName]
	 * 
	 */
	
	// TODO add default config
	
	/**
	 * Conda Version
	 */
	public static final String PARAM_CONDA_VERSION = "condaVersion";
	@ConfigurationParameter(name = PARAM_CONDA_VERSION, defaultValue = "py37_4.8.3")
	public String condaVersion ;
	
	/**
	 * Conda Environment Name, should be unique for this Annotator
	 */
	public static final String PARAM_CONDA_ENV_NAME = "envName";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_NAME)
	public String envName;

	/**
	 * Conda Environment Python Version
	 */
	public static final String PARAM_CONDA_ENV_PYTHON_VERSION = "envPythonVersion";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_PYTHON_VERSION)
	public String envPythonVersion;

	/**
	 * Python Dependencies from Conda
	 */
	public static final String PARAM_CONDA_ENV_DEPS_CONDA = "envDepsConda";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_DEPS_CONDA)
	public String envDepsConda;
	
	/**
	 * Python Dependencies from Pip
	 */
	public static final String PARAM_CONDA_ENV_DEPS_PIP = "envDepsPip";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_DEPS_PIP)
	public String envDepsPip;
	
	/**
	 * Script for additional conda setup
	 */
	public static final String PARAM_CONDA_BASH_SCRIPT = "condaBashScript";
	@ConfigurationParameter(name = PARAM_CONDA_BASH_SCRIPT, mandatory = false)
	public String condaBashScript;
	
	// Conda Base Directory
	protected static final Path condaBaseDir = Paths.get(System.getProperty("user.home"), ".textimager", "conda");
	
	protected Path condaDir;
	protected Path condaInstallDir;
	protected Path envDir;
	
	protected SubInterpreter interpreter;
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		System.out.println("Conda Base Dir: " + condaBaseDir.toString());
		
		// set conda base dir
		if (condaVersion == null || condaVersion.isEmpty()) {
			throw new ResourceInitializationException(new IllegalArgumentException("condaVersion ist null or empty!"));
		}
		
		// conda dir with version
		condaDir = condaBaseDir.resolve(condaVersion);
		System.out.println("Conda Dir: " + condaDir.toString());
		
		// path to install conda to
		condaInstallDir = condaDir.resolve("miniconda");
		System.out.println("Conda Install Dir: " + condaInstallDir.toString());
		
		// base path for envs
		envDir = condaInstallDir.resolve("envs").resolve(envName);
		System.out.println("Env Dir: " + envDir.toString());
		
		// create base directory
		try {
			Files.createDirectories(condaDir);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	
		initConda();
		initEnv();
		runBashScript();
		initInterpreter();
	}
	
	@Override
	public void destroy() {
		try {
			interpreter.close();
		} catch (JepException e) {
			e.printStackTrace();
		}
		super.destroy();
	}
	
	private int runCommand(List<String> command) throws ResourceInitializationException {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder(command);
	        processBuilder.inheritIO();
	        Process process = processBuilder.start();
	        return process.waitFor();
		} catch (InterruptedException | IOException e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	// Initializes Conda
	protected void initConda() throws ResourceInitializationException {
		// Check if conda dir is already there
		if (Files.exists(condaInstallDir)) {
			System.out.println("Conda already installed, skipping...");
			return;
		}
		
		// Not installed, continue
		
		// copy install script
		Path condaInstallScript = condaDir.resolve("conda_install.sh");
		try {
			Files.copy(getClass().getClassLoader().getResourceAsStream("conda_install.sh"), condaInstallScript, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		
		// install conda
		List<String> command = new ArrayList<>();
        command.add("bash");
        command.add(condaInstallScript.toString());
        command.add(condaDir.toString());
        command.add(condaVersion);
        command.add(condaInstallDir.toString());
		int status = runCommand(command);
        System.out.println("conda install: " + status);
        if (status != 0) {
        	throw new ResourceInitializationException(new IOException("failed to install conda"));
        }
	}
	
	// Initializes Conda Env with Dependencies
	protected void initEnv() throws ResourceInitializationException {
		// Check if env dir is already there
		if (Files.exists(envDir)) {
			System.out.println("Env already setup, skipping...");
			return;
		}
		
		// Not installed, continue
		
		// copy install script
		Path condaEnvScript = condaDir.resolve("conda_env.sh");
		try {
			Files.copy(getClass().getClassLoader().getResourceAsStream("conda_env.sh"), condaEnvScript, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		
		// install env
		List<String> command = new ArrayList<>();
        command.add("bash");
        command.add(condaEnvScript.toString());
        command.add(condaInstallDir.toString());
        command.add(envName);
        command.add(envPythonVersion);
        command.add(envDepsConda);
        command.add(envDepsPip);
		int status = runCommand(command);
        System.out.println("conda env: " + status);
        if (status != 0) {
        	throw new ResourceInitializationException(new IOException("failed to setup conda env"));
        }
	}
	
	protected void runBashScript() throws ResourceInitializationException {
		if (condaBashScript != null && !condaBashScript.isEmpty()) {
			Path script = envDir.resolve(condaBashScript);
			
			if (Files.exists(script)) {
				System.out.println("bash script already run, skipping...");
				return;
			}
			
			// copy script
			try {
				Files.copy(getClass().getClassLoader().getResourceAsStream(condaBashScript), script, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
			
			// install env
			List<String> command = new ArrayList<>();
	        command.add("bash");
	        command.add(script.toString());
	        command.add(condaInstallDir.toString());
	        command.add(envName);
			int status = runCommand(command);
	        System.out.println("bash script: " + status);
	        if (status != 0) {
	        	throw new ResourceInitializationException(new IOException("failed to run bash script"));
	        }
		}
	}
	
	// Initializes the Python Interpreter
	protected void initInterpreter() throws ResourceInitializationException {
		System.out.println("initializing interpreter in env: " + envDir.toString());

        PyConfig pyConfig = new PyConfig();
        pyConfig.setPythonHome(envDir.toString());
        pyConfig.setIgnoreEnvironmentFlag(1);
        pyConfig.setNoSiteFlag(1);
        pyConfig.setNoUserSiteDirectory(1);
        try {
			MainInterpreter.setInitParams(pyConfig);
		} catch (JepException e) {
			throw new ResourceInitializationException(e);
		}

        Path jepLibPath = Paths.get(envDir.toString(), "/lib", "python" + envPythonVersion, "site-packages", "jep", "libjep.so");
        System.out.println("jepLibPath: " + jepLibPath.toString());
        try {
			MainInterpreter.setJepLibraryPath(jepLibPath.toString());
		} catch (JepException e) {
			throw new ResourceInitializationException(e);
		}

        JepConfig jepConfig = new JepConfig();

        Path includePath = Paths.get(envDir.toString(), "/lib");
        try (Stream<Path> walk = Files.walk(includePath)) {
             String[] includePaths = walk
                     .filter(Files::isDirectory)
                     .map(Path::toString)
                     .toArray(String[]::new);

             jepConfig.addIncludePaths(includePaths);
        } catch (IOException e) {
			throw new ResourceInitializationException(e);
		}

        try {
			interpreter = jepConfig.createSubInterpreter();
		} catch (JepException e) {
			throw new ResourceInitializationException(e);
		}
	}
}
