package org.hucompute.textimager.uima.base;

import org.apache.commons.io.IOUtils;
import org.apache.uima.UimaContext;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.resource.ResourceInitializationException;
import org.hucompute.textimager.uima.docker.ContainerParametersBuilder;
import org.hucompute.textimager.uima.docker.ContainerWrapper;
import org.hucompute.textimager.uima.docker.DockerAPI;
import org.json.JSONObject;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.UUID;

/**
 * Basic Docker Rest Annotator
 * If a rest endpoint is specified, Docker is not used
 */
public abstract class DockerRestAnnotator extends RestAnnotator {
	/**
	 * The docker registry to use
	 * If left empty uses the default Docker registry
	 */
	public static final String PARAM_DOCKER_REGISTRY = "dockerRegistry";
	@ConfigurationParameter(name = PARAM_DOCKER_REGISTRY, mandatory = false)
	protected String dockerRegistry;

    /**
     * The docker image for the annotator
	 * If left empty uses the default of the annotation class
     */
    public static final String PARAM_DOCKER_IMAGE = "dockerImage";
    @ConfigurationParameter(name = PARAM_DOCKER_IMAGE, mandatory = false)
    protected String dockerImage;

	/**
	 * The tag of the docker image for the annotator
	 * If left empty uses the default of the annotation class
	 */
	public static final String PARAM_DOCKER_IMAGE_TAG = "dockerImageTag";
	@ConfigurationParameter(name = PARAM_DOCKER_IMAGE_TAG, mandatory = false)
	protected String dockerImageTag;

	/**
	 * Port inside the container to map to host
	 * If left empty uses the default of the annotation class
	 */
	public static final String PARAM_DOCKER_PORT = "dockerPort";
	@ConfigurationParameter(name = PARAM_DOCKER_PORT, mandatory = false)
	protected int dockerPort;

	/**
	 * The docker volumes options, separated by comma
	 * TODO currently not implemented
	 */
	public static final String PARAM_DOCKER_VOLUMES = "dockerVolumes";
	@ConfigurationParameter(name = PARAM_DOCKER_VOLUMES, mandatory = false)
	protected String dockerVolumes;
    
    /**
     * The min port to automatically find a free port for container
     */
    public static final String PARAM_PORT_MIN = "portMin";
    @ConfigurationParameter(name = PARAM_PORT_MIN, mandatory = false, defaultValue = "50000")
    protected int portMin;
    
    /**
     * The max port to automatically find a free port for container
     */
    public static final String PARAM_PORT_MAX = "portMax";
    @ConfigurationParameter(name = PARAM_PORT_MAX, mandatory = false, defaultValue = "59999")
    protected int portMax;

	/**
	 * Docker API socket path, currently only socket is supported
	 */
	public static final String PARAM_DOCKER_SOCKET = "dockerSocket";
	@ConfigurationParameter(name = PARAM_DOCKER_SOCKET, mandatory = false, defaultValue = "/var/run/docker.sock")
	protected File dockerSocket;

	// Provides default Docker Image, if none is configured
	abstract protected String getDefaultDockerImage();

	// Provides default Docker Image tag, if none is configured
	abstract protected String getDefaultDockerImageTag();

	// Provides default Docker Port, if none is configured
	abstract protected int getDefaultDockerPort();

	// Docker/Container API
	protected DockerAPI docker;
	protected ContainerWrapper container;

	// Endpoint to check for service readyness
	protected String getRestEndpointTextImagerReady() {
		return restEndpoint + "/textimager/ready";
	}

	// Check if port is in use
	protected boolean isPortFree(String host, int port) {
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

	// Check if service is ready
	protected boolean isReady() throws IOException {
		URL url = new URL(getRestEndpointTextImagerReady());
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("GET");
		connection.setDoInput(true);
		connection.setUseCaches(false);

		String res = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

		JSONObject status = new JSONObject(res);
		try {
			return status.getBoolean("ready");
		}
		catch (Exception ignored) {
		}

		return false;
	}

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);

		// use docker only if no rest endpoint is specified
		if (restEndpoint == null) {
			// Get Docker image from annotator class if not specified
			if (dockerImage == null) {
				dockerImage = getDefaultDockerImage();
			}

			// Get Docker image tag from annotator class if not specified
			if (dockerImageTag == null) {
				dockerImageTag = getDefaultDockerImageTag();
			}

			// Build full image name from image and repository
			String fullDockerImageName = dockerImage;
			if (dockerRegistry != null) {
				fullDockerImageName = dockerRegistry + "/" + fullDockerImageName;
			}

			// Full Docker image name
			String fullDockerImage = fullDockerImageName + ":" + dockerImageTag;
			System.out.println("Using Docker image: " + fullDockerImage);

			// Get Docker port from annotator class if not specified
			if (dockerPort == 0) {
				dockerPort = getDefaultDockerPort();
			}
			System.out.println("Using Docker port: " + dockerPort);

			// Connect to Docker API
			try {
				docker = new DockerAPI(dockerSocket);
				System.out.println("Connected to Docker API");
			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}

			// Pull the docker image to use
			System.out.println("Pulling docker image...");
			try {
				docker.get_handle().images().pull(fullDockerImageName, dockerImageTag);
			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}

			// TODO check if really works in swarm mode
			String dockerRestEndpointIP = "127.0.0.1";

			// Find free port
			int port = portMin;
			while (!isPortFree(dockerRestEndpointIP, port)) {
				System.out.println("Port " + port + " not available, checking next...");
				port++;
				if (port > portMax) {
					throw new ResourceInitializationException(new Exception("No free ports found"));
				}
			}
			System.out.println("Using host port " + port);

			// Update endpoint of RestAnnotator
			restEndpoint = "http://" + dockerRestEndpointIP + ":" + port;
			System.out.println("Container endpoint is " + restEndpoint);

			// Container name based on timestamp
	        String name = getClass().getName() + "__textimager" + "." + Instant.now().getEpochSecond() + "." + UUID.randomUUID();
			System.out.println("Starting container \"" + name + "\"");

	        try {
				// Build container
				ContainerParametersBuilder parametersBuilder = new ContainerParametersBuilder(fullDockerImage);
				parametersBuilder.set_port_mapping(dockerPort, port);

				// TODO add volumes
				/*if (dockerVolumes != null) {
					for (String m : dockerVolumes.split(",", -1)) {
					}
				}*/

				JsonObject config = parametersBuilder.get_config();

				// Start container
				container = new ContainerWrapper(
						docker.get_handle()
								.containers()
								.create(name, config)
				);
				System.out.println("Created container with id " + container.get_handle().containerId());

				container.get_handle().start();

				// Wait until container is running
				do {
					System.out.println("Waiting for Docker container to start...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// ignore
					}
				} while (!container.fetch_is_running());

				// Wait until server is ready
				do {
					System.out.println("Waiting for service to be ready...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// ignore
					}
				} while (!isReady());

				System.out.println("Docker container should be running now");

			} catch (Exception e) {
				throw new ResourceInitializationException(e);
			}
		}
	}
	
	@Override
	public void destroy() {
		if (container != null) {
			try {
				System.out.println("Stopping Docker container");
				container.get_handle().stop();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// TODO container removed at stop already?
			try {
				System.out.println("Waiting for Docker to stop...");
				container.get_handle().waitOn("not-running");
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				System.out.println("Removing Docker container");
				container.get_handle().remove();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		super.destroy();
	}
}
