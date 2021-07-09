package org.hucompute.textimager.uima.docker;

import com.amihaiemil.docker.Container;

import javax.json.JsonObject;
import java.io.IOException;

/**
 * Abstracts the Container object from the used API away to enable better utility
 *
 * @author Alexander Leonhardt
 */
public class ContainerWrapper {
    /**
     * The underlying container which is the gate to the api
     */
    private final Container _underlying;

    /**
     * The JSON Object describing the container, has to be fetched since the fetching
     * will only be done in the constructor
     */
    private JsonObject _description;

    /**
     * Creates a new container wrapper from a given handle
     *
     * @param cont The container to wrap by this utility class
     * @throws IOException Throws an IO Exception if the docker socket is unavailable or the container invalid
     */
    public ContainerWrapper(Container cont) throws IOException {
        _underlying = cont;
        _description = cont.inspect();
    }

    /**
     * Refetch the information about the container, this is necessary if the state of the container changes
     *
     * @throws IOException Throws an IOException if the docker socket is unavailable
     */
    public void fetch() throws IOException {
        _description = _underlying.inspect();
    }

    /**
     * Stops the current container
     *
     * @throws IOException Throws an exception if the socket is unavailable
     */
    public void stop() throws IOException {
        _underlying.stop();
    }

    /**
     * Returns a string representation of the log
     *
     * @return The String representation of the log
     * @throws IOException Throws an IO Exception if the docker socket is unavailable
     */
    public String get_log() throws IOException {
        return _underlying.logs().fetch();
    }

    /**
     * Returns the name of the current container, cut the directory '/' from the name
     *
     * @return A string which is the name of the container
     */
    public String get_name() {
        return _description.getString("Name").substring(1);
    }

    /**
     * Returns a native handle to the underlying library representation of a container to enable native calls
     *
     * @return Returns the handle
     */
    public Container get_handle() {
        return _underlying;
    }

    public String get_status() {
        return _description.getJsonObject("State").getString("Status");
    }

    public String get_hostname() {
        return _description.getJsonObject("Config").getString("Hostname");
    }

    public boolean is_running() {
        return this._description.getJsonObject("State").getBoolean("Running");
    }

    public String fetch_status() throws Exception {
        this.fetch();
        return this._description.getJsonObject("State").getString("Status");
    }

    public boolean fetch_is_running() throws Exception {
        this.fetch();
        return this._description.getJsonObject("State").getBoolean("Running");
    }
}
