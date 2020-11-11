package org.hucompute.textimager.uima.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

import jep.JepConfig;
import jep.JepException;
import jep.MainInterpreter;
import jep.PyConfig;
import jep.SharedInterpreter;
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
	 *              - [bashScript]
	 * 
	 */
	
	// TODO add default config
	
	/**
	 * Conda Version
	 */
	public static final String PARAM_CONDA_VERSION = "condaVersion";
	@ConfigurationParameter(name = PARAM_CONDA_VERSION, mandatory = false, defaultValue = "py37_4.8.3")
	public String condaVersion ;
	
	/**
	 * Conda Environment Name, should be unique for this Annotator
	 */
	public static final String PARAM_CONDA_ENV_NAME = "envName";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_NAME, mandatory = false)
	public String envName;

	/**
	 * Conda Environment Python Version
	 */
	public static final String PARAM_CONDA_ENV_PYTHON_VERSION = "envPythonVersion";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_PYTHON_VERSION, mandatory = false)
	public String envPythonVersion;

	/**
	 * Python Dependencies from Conda
	 */
	public static final String PARAM_CONDA_ENV_DEPS_CONDA = "envDepsConda";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_DEPS_CONDA, mandatory = false)
	public String envDepsConda;
	
	/**
	 * Python Dependencies from Pip
	 */
	public static final String PARAM_CONDA_ENV_DEPS_PIP = "envDepsPip";
	@ConfigurationParameter(name = PARAM_CONDA_ENV_DEPS_PIP, mandatory = false)
	public String envDepsPip;
	
	/**
	 * Script for additional conda setup
	 */
	public static final String PARAM_CONDA_BASH_SCRIPT = "condaBashScript";
	@ConfigurationParameter(name = PARAM_CONDA_BASH_SCRIPT, mandatory = false)
	public String condaBashScript;
	
	// Conda Base Directory
	protected static final Path condaBaseDir = Paths.get(System.getProperty("user.home"), ".textimager", "conda");
	
	private int threadSleepTime = 10000;
	
	protected Path condaDir;
	protected Path condaInstallDir;
	protected Path envDir;
	
	protected static SubInterpreter interpreter;
	protected static int interpreterUseCount = 0;
	
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		System.out.println("Conda Base Dir: " + condaBaseDir.toString());
	}
	
	@Override
	public void destroy() {
		interpreterUseCount--;
		try {
			if (interpreterUseCount <= 0) {
				System.out.println("Closing python interpreter...");
				interpreter.close();
			}
			else {
				System.out.println("not closing python interpreter, users left: " + interpreterUseCount);
			}
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
			System.out.println("creating dir: " + condaDir.toString());
			Files.createDirectories(condaDir);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}

		// Lockfile
		Path lockfile = condaDir.resolve("textimager_" + condaInstallDir.getFileName().toString() +".conda_lock");
		System.out.println("lockfile: " + lockfile.toString());
		while (Files.exists(lockfile)) {
			try {
				System.out.println("waiting on lock \"conda install\"...");
				Thread.sleep(threadSleepTime);
			} catch (InterruptedException e) {
				throw new ResourceInitializationException(e);
			}
		}
		try {
			System.out.println("creating lockfile now...");
			FileUtils.touch(lockfile.toFile());
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
				
		// Check if conda dir is already there
		System.out.println("checking for conda isntall dir: " + condaInstallDir.toString());
		if (Files.exists(condaInstallDir)) {
			System.out.println("Conda already installed, skipping...");
		}
		else {
			// Not installed, continue
			System.out.println("not installed, doing now...");
			
			// copy install script
			Path condaInstallScript = condaDir.resolve("conda_install.sh");
			System.out.println("conda install script: " + condaInstallScript.toString());
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
	        System.out.println("running install script now.....");
			int status = runCommand(command);
	        System.out.println("conda install: " + status);
	        if (status != 0) {
	        	throw new ResourceInitializationException(new IOException("failed to install conda"));
	        }
		}
		
		System.out.println("deleting lockfile: " + lockfile.toString());
		try {
			Files.delete(lockfile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
    	
		initEnv();
		runBashScript();
		initInterpreter();
	}
	
	// Initializes Conda Env with Dependencies
	private void initEnv() throws ResourceInitializationException {
		System.out.println("init env");
		
		// Lockfile
		Path lockfile = condaDir.resolve("textimager_" + envDir.getFileName().toString() +".env_lock");
		System.out.println("lockfile: " + lockfile.toString());
		while (Files.exists(lockfile)) {
			try {
				System.out.println("waiting on lock \"conda env\"...");
				Thread.sleep(threadSleepTime);
			} catch (InterruptedException e) {
				throw new ResourceInitializationException(e);
			}
		}
		try {
			System.out.println("creating lockfile now...");
			FileUtils.touch(lockfile.toFile());
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
		
		// Check if env dir is already there
		System.out.println("checking env dir: " + envDir.toString());
		if (Files.exists(envDir)) {
			System.out.println("Env already setup, skipping...");
		}
		else {
		// Not installed, continue
			System.out.println("not installed, doing now...");
		
			// copy install script
			Path condaEnvScript = condaDir.resolve("conda_env.sh");
			System.out.println("conda env script: " + condaEnvScript.toString());
			try {
				Files.copy(getClass().getClassLoader().getResourceAsStream("conda_env.sh"), condaEnvScript, StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
			
			// Get JVM home path
			String javaHome = StringUtils.substringBefore(System.getProperties().getProperty("java.home"), "/jre");
			
			// install env
			List<String> command = new ArrayList<>();
	        command.add("bash");
	        command.add(condaEnvScript.toString());
	        command.add(condaInstallDir.toString());
	        command.add(envName);
	        command.add(envPythonVersion);
	        command.add(envDepsConda);
	        command.add(envDepsPip);
	        command.add(javaHome);
	        System.out.println("installing now.....");
			int status = runCommand(command);
	        System.out.println("conda env: " + status);
	        if (status != 0) {
	        	throw new ResourceInitializationException(new IOException("failed to setup conda env"));
	        }
		}

		System.out.println("deleting lock file: " + lockfile.toString());
		try {
			Files.delete(lockfile);
		} catch (IOException e) {
			throw new ResourceInitializationException(e);
		}
	}
	
	private void runBashScript() throws ResourceInitializationException {
		System.out.println("run bash script...");
		
		if (condaBashScript != null && !condaBashScript.isEmpty()) {
			Path script = envDir.resolve(condaBashScript);
			System.out.println("script: " + script.toString());
			
			// Lockfile
			Path lockfile = condaDir.resolve("textimager_" + script.getFileName().toString() +".script_lock");
			System.out.println("lockfile: " + lockfile.toString());
			while (Files.exists(lockfile)) {
				try {
					System.out.println("waiting on lock \"bash script\"...");
					Thread.sleep(threadSleepTime);
				} catch (InterruptedException e) {
					throw new ResourceInitializationException(e);
				}
			}
			try {
				System.out.println("creating lockfile now...");
				FileUtils.touch(lockfile.toFile());
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
			
			if (Files.exists(script)) {
				System.out.println("bash script already run, skipping...");
			}
			else {
				// copy script
				System.out.println("running script now: " + script.toString());
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
		        System.out.println("running script now start...");
				int status = runCommand(command);
		        System.out.println("bash script: " + status);
		        if (status != 0) {
		        	throw new ResourceInitializationException(new IOException("failed to run bash script"));
		        }
			}
			
			System.out.println("deleting lockfile: " + lockfile.toString());
			try {
				Files.delete(lockfile);
			} catch (IOException e) {
				throw new ResourceInitializationException(e);
			}
		}
	}
	
	// Initializes the Python Interpreter
	private void initInterpreter() throws ResourceInitializationException {
		System.out.println("initializing interpreter in env: " + envDir.toString());
		
		interpreterUseCount++;
		
		if (interpreter != null) {
			System.out.println("python interpreter already set up");
			return;
		}
		
		System.out.println("initializing new python nterpreter...");

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
