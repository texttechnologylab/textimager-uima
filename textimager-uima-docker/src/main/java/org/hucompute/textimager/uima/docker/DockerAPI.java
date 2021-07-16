package org.hucompute.textimager.uima.docker;

import com.amihaiemil.docker.*;

import javax.json.JsonObject;
import java.io.*;
import java.net.URI;
import java.util.Vector;


/**
 * Endpoint to the docker api, wraps the underlying library and provides utility functions
 *
 * @author Alexander Leonhardt
 */
public class DockerAPI {
    /**
     * The underlying handle to the docker library.
     */
    final Docker _docker_connection;

    /**
     * Initialises the docker connection, at the moment only through socket
     */
    public DockerAPI() {
        _docker_connection = (Docker) new UnixDocker(new File("/var/run/docker.sock"));
    }


    /**
     * Acesses the uri for the docker socket
     *
     * @param uri the address of the docker socket in tcp mode
     */
    public DockerAPI(String uri) {
        _docker_connection = (Docker) new TcpDocker(URI.create("tcp://localhost:23750"));
    }

    public DockerAPI(File from_file) throws IOException {
        _docker_connection = (Docker) new UnixDocker(from_file);
    }


    /**
     * Utility get container by name
     *
     * @param name The name to get the container by
     * @return A utility wrapper for the container if there is one named like name
     * @throws IOException Throws exception if the container does not exist
     */
    public ContainerWrapper get_container_by_name(String name) throws IOException {
        return new ContainerWrapper(_docker_connection.containers().get(name));
    }

    /**
     * Returns all container in a Vector and already wrapped in the utility class
     *
     * @return The Vector of wrapped containers
     * @throws IOException Throws an exception if something goes wrong with the socket
     */
    public Vector<ContainerWrapper> get_container() throws IOException {
        Vector<ContainerWrapper> ret = new Vector<>();
        for (Container cont : _docker_connection.containers()) {
            ret.add(new ContainerWrapper(cont));
        }
        return ret;
    }

    /**
     * Returns all images wrapped in the utility classes
     *
     * @return The vector of utility classes wrapping the images.
     * @throws IOException Throws an IO Exception if the socket throws an error
     */
    public Vector<ImageWrapper> get_images() throws IOException {
        Vector<ImageWrapper> ret = new Vector<>();
        for (Image cont : _docker_connection.images()) {
            ret.add(new ImageWrapper(cont));
        }
        return ret;
    }

    /**
     * Returns the wrapped handle to use the native functions of the handle
     *
     * @return
     */
    public Docker get_handle() {
        return _docker_connection;
    }


    /**
     * Demo function
     *
     * @param args This will run the java demo function for the docker
     */
    public static void main(String[] args) throws IOException {
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);

        DockerAPI api = new DockerAPI(new File("./docker.sock"));

        System.out.println("Connected to docker daemon now.");
        while (true) {
            System.out.print("$> ");
            String command = br.readLine().trim();
            String[] part_command = command.split(" ");
            if (command.equals("quit")) {
                break;
            } else if (command.equals("ls images")) {
                for (ImageWrapper img : api.get_images()) {
                    String result = img.get_tags();
                    if (!result.equals(""))
                        System.out.println(result);
                }
            } else if (command.equals("ls containers")) {
                for (ContainerWrapper cont : api.get_container()) {
                    System.out.format("Container name %s and status %s\n", cont.get_name(), cont.get_status());
                }
            } else if (part_command[0].equals("status")) {
                ContainerWrapper cont = api.get_container_by_name(part_command[1]);
                System.out.format("Container name %s and status %s\n", cont.get_name(), cont.get_status());
            } else if (part_command[0].equals("kill")) {
                ContainerWrapper cont = api.get_container_by_name(part_command[1]);
                cont.stop();
            } else if (part_command[0].equals("log")) {
                ContainerWrapper cont = api.get_container_by_name(part_command[1]);
                System.out.println(cont.get_log());
            } else if (part_command[0].equals("run")) {
                System.out.print("Please enter container name: ");
                String name = br.readLine().trim();

                System.out.print("With gpu (yes/no): ");
                String gpu = br.readLine().trim();
                ContainerParametersBuilder params = new ContainerParametersBuilder(part_command[1]);
                if (gpu.equals("yes")) {
                    params.set_gpu_access(true);
                }
                params.set_port_mapping(8000, 8000)
                        .set_environment_mapping("STEPS_PARSER_PORT", "8000");
                JsonObject config = params.get_config();
                ContainerWrapper cont = new ContainerWrapper(api.get_handle().containers().create(name, config));
                cont.get_handle().start();
                boolean is_healthy = false;
                while (true) {
                    if (cont.get_log().contains("PyDockerNotify: Container startup success")) {
                        is_healthy = true;
                        break;
                    }
                }
                if (is_healthy) {
                    System.out.println("Received handshake from child container, startup successful.");
                } else {
                    System.out.println("Handshake timed out, container seems to hang or quit.");
                }
            } else {
                System.out.println("Unkown command please try again!");
            }
        }
    }
}
