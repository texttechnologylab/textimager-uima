package org.hucompute.textimager.uima.docker;


import javax.json.*;

/**
 * This is a builder for container creation flags
 *
 * @author Alexander Leonhardt
 */
public class ContainerParametersBuilder {
    /**
     * Saves the environment of the created container
     */
    private final JsonArrayBuilder _env;
    /**
     * Saves the ports which are mapped for the container to be created
     */
    private final JsonObjectBuilder _ports;

    /**
     * Wraps the host ports which are mapped for the container to be created
     */
    private final JsonObjectBuilder _hostPorts;

    /**
     * The requests for specific devices like GPU are tracked by this.
     */
    private final JsonArrayBuilder _deviceRequests;

    /**
     * The image name to start the container from.
     */
    private final String _image_name;

    /**
     * The network name, "bridge" by default
     */
    private String _network_mode;

    /**
     * Build the empty special requests.
     *
     * @param image_name The container image name
     */
    public ContainerParametersBuilder(String image_name) {
        _image_name = image_name;
        _env = Json.createArrayBuilder();
        _ports = Json.createObjectBuilder();
        _hostPorts = Json.createObjectBuilder();
        _deviceRequests = Json.createArrayBuilder();
        _network_mode = "bridge";
    }

    /**
     * Sets a new port mapping, exposes the port and maps it to the host port.
     *
     * @param container The container port to map
     * @param host      The host port to map
     * @param protocol  The protocol to map "tcp","udp" and "sctp" are valid
     * @return Returns a reference to the container to chain calls
     */
    public ContainerParametersBuilder set_port_mapping(int container, int host, String protocol) {
        _ports.add("" + container + "/" + protocol, Json.createObjectBuilder().build());
        _hostPorts.add("" + container + "/" + protocol, Json.createArrayBuilder().add(
                Json.createObjectBuilder().add("HostPort", "" + host).build()
        ).build());
        return this;
    }

    /**
     * Sets a new port mapping with the default of a tcp mapping
     *
     * @param container The container port to map
     * @param host      The host port to map
     * @return Returns a reference to this container to enable chaining calls
     */
    public ContainerParametersBuilder set_port_mapping(int container, int host) {
        return set_port_mapping(container, host, "tcp");
    }

    /**
     * Sets a new environment wrapping of the type variable=value
     *
     * @param variable The variable to name the key in the child environment
     * @param value    The value of the environment variable
     * @return Returns a reference to this container to chain calls
     */
    public ContainerParametersBuilder set_environment_mapping(String variable, String value) {
        _env.add(variable + "=" + value);
        return this;
    }

    /**
     * Sets the network mode
     *
     * @param mode The network mode: bridge, host, none, and container:<name|id> or name of network
     * @return Returns a reference to this container to enable chaining calls
     */
    public ContainerParametersBuilder set_network_mode(String mode) {
        _network_mode = mode;
        return this;
    }

    /**
     * Consumes the ContainerParameterBuilder and returns a json with the current config
     *
     * @return The Json with the container config, can be given directly to the docker socket
     */
    public JsonObject get_config() {
        JsonObjectBuilder _params = Json.createObjectBuilder().add("Image", _image_name);
        _params.add("ExposedPorts", _ports.build())
                .add("Env", _env.build())
                .add("HostConfig", Json.createObjectBuilder()
                        .add("DeviceRequests", _deviceRequests.build())
                        .add("AutoRemove", true)
                        .add("PortBindings", _hostPorts.build())
                        .add("NetworkMode", _network_mode).build())
                .add("Cmd", Json.createArrayBuilder().build());
        return _params.build();
    }

    /**
     * Sets the gpu access, at the moment maps all available gpus and only nivdia gpus
     *
     * @param access Map all gpus or none?
     * @return Returns a reference to this class to enable call chaining
     */
    public ContainerParametersBuilder set_gpu_access(boolean access) {
        if (access) {
            JsonArray caps = Json.createArrayBuilder().add("gpu").add("compute").add("utility").build();
            JsonObject device_request = Json.createObjectBuilder().add("Driver", "nvidia").add("Capabilities", Json.createArrayBuilder().add(caps).build()).build();
            _deviceRequests.add(device_request);
        }
        return this;
    }
}
