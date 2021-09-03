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
	 * Port inside the container to use
	 * If left empty uses the default of the annotation class
	 */
	public static final String PARAM_DOCKER_PORT = "dockerPort";
	@ConfigurationParameter(name = PARAM_DOCKER_PORT, mandatory = false)
	protected int dockerPort;

	/**
	 * Optional port on the host to use
	 */
	public static final String PARAM_DOCKER_HOST_PORT = "dockerHostPort";
	@ConfigurationParameter(name = PARAM_DOCKER_HOST_PORT, mandatory = false)
	protected int dockerHostPort;

	/**
	 * Override Docker hostname to use locally
	 */
	public static final String PARAM_DOCKER_HOSTNAME = "dockerHostname";
	@ConfigurationParameter(name = PARAM_DOCKER_HOSTNAME, mandatory = false)
	protected String dockerHostname;

	/**
	 * The Docker network mode
	 * Default is TextImager network, use "bridge" to deploy locally
	 */
	public static final String PARAM_DOCKER_NETWORK = "dockerNetwork";
	@ConfigurationParameter(name = PARAM_DOCKER_NETWORK, mandatory = false, defaultValue = "bridge")
	protected String dockerNetwork;

	/**
	 * Docker API socket path, currently only socket is supported
	 */
	public static final String PARAM_DOCKER_SOCKET = "dockerSocket";
	@ConfigurationParameter(name = PARAM_DOCKER_SOCKET, mandatory = false, defaultValue = "/var/run/docker.sock")
	protected File dockerSocket;

	@Override
	protected String getModelName() {
		return fullDockerImageName;
	}

	@Override
	protected String getModelVersion() {
		return dockerImageTag;
	}

	// Provides default Docker Image, if none is configured
	abstract protected String getDefaultDockerImage();

	// Provides default Docker Image tag, if none is configured
	abstract protected String getDefaultDockerImageTag();

	// Provides default Docker Port, if none is configured
	abstract protected int getDefaultDockerPort();

	// Docker image name with registry info
	protected String fullDockerImageName;

	// Docker/Container API
	protected ContainerWrapper container;

	// Endpoint to check for service readyness
	protected String getRestEndpointTextImagerReady() {
		return restEndpoint + "/textimager/ready";
	}

	// Check if service is ready
	protected boolean isReady() throws IOException {
		try {
			URL url = new URL(getRestEndpointTextImagerReady());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDoInput(true);
			connection.setUseCaches(false);

			String res = IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

			JSONObject status = new JSONObject(res);
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
			try {
				// Get Docker image from annotator class if not specified
				if (dockerImage == null) {
					dockerImage = getDefaultDockerImage();
				}

				// Get Docker image tag from annotator class if not specified
				if (dockerImageTag == null) {
					dockerImageTag = getDefaultDockerImageTag();
				}

				// Build full image name from image and repository
				fullDockerImageName = dockerImage;
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
				DockerAPI docker = new DockerAPI(dockerSocket);
				System.out.println("Connected to Docker API");

				// check if image already exists
				System.out.println("Checking for docker image...");
				if (!docker.check_image_exists(fullDockerImage)) {
					// Pull the docker image to use
					System.out.println("Docker image not found, pulling...");
					docker.get_handle().images().pull(fullDockerImageName, dockerImageTag);
				}

				// Container name based on timestamp
				String name = getClass().getName() + "__textimager" + "." + Instant.now().getEpochSecond() + "." + UUID.randomUUID();
				System.out.println("Starting container \"" + name + "\"");

				// Build container
				ContainerParametersBuilder parametersBuilder = new ContainerParametersBuilder(fullDockerImage);

				// Optionally add port mapping
				int containerPort = dockerPort;

				// if bridge network use default port from container
				if (dockerHostPort == 0 && dockerNetwork.equals("bridge")) {
					dockerHostPort = getDefaultDockerPort();
				}

				// Create port mapping
				if (dockerHostPort != 0) {
					System.out.println("Using Docker port mapping " + dockerPort + " -> " + dockerHostPort);
					parametersBuilder.set_port_mapping(dockerPort, dockerHostPort);
					containerPort = dockerHostPort;
				}

				// Set network
				System.out.println("Using Docker network " + dockerNetwork);
				parametersBuilder.set_network_mode(dockerNetwork);

				// Create container
				JsonObject config = parametersBuilder.get_config();
				container = new ContainerWrapper(
						docker.get_handle()
								.containers()
								.create(name, config)
				);
				System.out.println("Created container with id " + container.get_handle().containerId());

				// Start container
				container.get_handle().start();

				// Wait until container is running
				// TODO add timeout
				try {
					do {
						System.out.println("Waiting for Docker container to start...");
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// ignore
						}
					} while (!container.fetch_is_running());
				} catch (InterruptedException e) {
					// ignore
				}

				// Fetch inspect data
				container.fetch();

				// Update endpoint of RestAnnotator
				if (dockerHostname == null) {
					if (dockerNetwork.equals("bridge")) {
						// use loaclhost if bridge network is being used
						dockerHostname = "localhost";
					}
					else {
						dockerHostname = container.get_hostname();
					}
				}
				restEndpoint = "http://" + dockerHostname + ":" + containerPort;
				System.out.println("Container endpoint is " + restEndpoint);

				// Wait until server is ready
				// TODO add timeout
				do {
					System.out.println("Waiting for service to be ready...");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// ignore
					}
				} while (!isReady());

                //if (container != null) {
                //    System.out.println("Status: " + container.get_status());
                //    System.out.println("Log: " + container.get_log());
                //}

				System.out.println("Docker container should be running now");

			} catch (Exception e) {
				// stop Docker before throwing
				System.out.println("Trying to stop Docker container before throwing...");
				dockerStop();
				throw new ResourceInitializationException(e);
			}
		}
	}

	private void dockerStop() {
		if (container != null) {
			// TODO container is not stopped on DUCC?
			try {
                System.out.println("Status: " + container.get_status());
                System.out.println("Log: " + container.get_log());
                System.out.println("Stopping Docker container " + container.get_name());
                container.get_handle().stop();
			} catch (Exception e) {
                System.out.println(e.getMessage());
				e.printStackTrace();
			} finally {
                try {
                    container.get_handle().kill();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }


            // TODO container removed at stop already?
			try {
				System.out.println("Waiting for Docker to stop...");
				container.get_handle().waitOn("not-running");
			} catch (Exception e) {
                System.out.println(e.getMessage());
				e.printStackTrace();
			}

			try {
				System.out.println("Removing Docker container");
				container.get_handle().remove();
			} catch (Exception e) {
                System.out.println(e.getMessage());
				e.printStackTrace();
			}

			container = null;
		}
	}

	@Override
	public void destroy() {
        System.out.println("Calling Destroy");
		dockerStop();
		System.out.println("Docker annotator destroyed");
		super.destroy();
	}

}
