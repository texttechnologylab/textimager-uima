package org.hucompute.textimager.uima.base;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;

public abstract class DockerRestAnnotator extends RestAnnotator {
    /**
     * The docker image for the annotator, if a rest endpoint is specified docker is not used
     */
    public static final String PARAM_DOCKER_IMAGE = "dockerImage";
    @ConfigurationParameter(name = PARAM_DOCKER_IMAGE, mandatory = false)
    protected String dockerImage;
    
    /**
     * The min port
     */
    public static final String PARAM_PORT_MIN = "portMin";
    @ConfigurationParameter(name = PARAM_PORT_MIN, mandatory = false, defaultValue = "5000")
    protected int portMin;
    
    /**
     * The max port
     */
    public static final String PARAM_PORT_MAX = "portMax";
    @ConfigurationParameter(name = PARAM_PORT_MAX, mandatory = false, defaultValue = "5100")
    protected int portMax;
    
    /**
     * The docker volumes options, separated by comma
     */
    public static final String PARAM_DOCKER_VOLUMES = "dockerVolumes";
    @ConfigurationParameter(name = PARAM_DOCKER_VOLUMES, mandatory = false)
    protected String dockerVolumes;

	private boolean useDocker = true;
	private String dockerRestEndpointIP = "127.0.0.1";
	private String dockerRestEndpoint;
	private String dockerPidFile = null;
	
	// Default Docker Image, if none is configured
	abstract protected String getDefaultDockerImage();
	
	@Override
	protected String getRestEndpoint() {
		if (useDocker) {
			return dockerRestEndpoint + getRestRoute();
		}		
		return super.getRestEndpoint();
	}

	private boolean isPortFree(String host, int port) {
		Socket s = null;
		try {
			s = new Socket(host, port);
			return false;
		} catch (Exception e) {
			return true;
		} finally {
			if (s != null) {
				try {
					s.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}
	
	private boolean isHTTPOK(String url) {
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection con = (HttpURLConnection) new URL(dockerRestEndpoint).openConnection();
			con.setRequestMethod("HEAD");
			if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
				System.out.println("docker server online");
				return true;
			}
		} catch (Exception e) {
			// ignore
		}
		return false;
	}
	
	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
		
		// use docker if no rest endpoint is specified
		useDocker = (restEndpoint == null);
		
		if (useDocker) {
			if (dockerImage == null) {
				System.out.println("no docker image configured.");
				dockerImage = getDefaultDockerImage();
			}
			
			System.out.println("using docker image: " + dockerImage);
			
			// wait so that instances do not interrupt
			try {
				Thread.sleep((long) (Math.random() * 1000));
			} catch (InterruptedException e1) {
				// ..
			}
	
			try {
				File dockerPidFileTemp = File.createTempFile("textimager_ducc_", "_docker_pid");
				dockerPidFile = dockerPidFileTemp.getAbsolutePath();
				// TODO better solution
				dockerPidFileTemp.delete();
				System.out.println("docker pid file: " + dockerPidFile);
			} catch (Exception ex) {
				throw new ResourceInitializationException(ex);
			}
	
			int portInt = portMin;
			while (!isPortFree(dockerRestEndpointIP, portInt)) {
				System.out.println("port " + portInt + " not available, checking next...");
				portInt++;
				if (portInt > portMax) {
					throw new ResourceInitializationException(new Exception("no free ports found"));
				}
			}
			String port = String.valueOf(portInt);
			System.out.println("using port " + port);
			
			dockerRestEndpoint = "http://" + dockerRestEndpointIP + ":" + port;

			List<String> command = new ArrayList<String>();
			command.add("docker");
			command.add("run");
			command.add("-d");
			command.add("--cidfile");
			command.add(dockerPidFile);
			command.add("--rm");
			command.add("-p");
			command.add(port + ":80");
			if (dockerVolumes != null) {
				for (String m : dockerVolumes.split(",", -1)) {
					command.add("-v");
					command.add(m);
				}
			}
			command.add(dockerImage);
			ProcessBuilder builder = new ProcessBuilder(command);
	        try {
	        	Process dockerProcess = builder.start();
				
				// wait until server is ready
				System.out.println("waiting for docker server...");
				while(!isHTTPOK(dockerRestEndpointIP)) {
					System.out.println("still waiting...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// ignore
					}
				}
				try {
					dockerProcess.waitFor();
				} catch (InterruptedException e) {
					dockerProcess.destroyForcibly();
				}
				int exitValue = dockerProcess.exitValue();
				System.out.println("exit value: " + exitValue);
				if (exitValue != 0) {
					throw new Exception("error starting docker container with docker rest server.");
				}
			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		}
	}
	
	@Override
	public void destroy() {
		if (useDocker) {
			if (dockerPidFile != null) {
				File dockerPidFileTemp = new File(dockerPidFile);
				try {
					String dockerId = FileUtils.readFileToString(dockerPidFileTemp, "UTF-8");
					System.out.println("docker id: " + dockerId);
					new ProcessBuilder("docker", "stop", dockerId).start();
				} catch (IOException e) {
					// ..
				}
				dockerPidFileTemp.delete();
			}
		}

		super.destroy();
	}
}
