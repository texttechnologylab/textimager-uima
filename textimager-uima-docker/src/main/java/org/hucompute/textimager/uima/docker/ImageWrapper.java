package org.hucompute.textimager.uima.docker;

import com.amihaiemil.docker.Image;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.IOException;

/**
 * Abstracts the image away from the used API
 *
 * @author Alexander Leonhardt
 */
public class ImageWrapper {
    /**
     * The underlying image of the library
     */
    private final Image _underlying_image;

    /**
     * The cached description of the image to prevent unnecessary refetching
     */
    private JsonObject _description;

    /**
     * Creates a new wrapper class to improve utility of the images
     *
     * @param image The image to create the utility class from
     * @throws IOException Throws an IOException if the docker socket is unavailable
     */
    public ImageWrapper(Image image) throws IOException {
        _underlying_image = image;
        _description = image.inspect();
    }

    /**
     * Returns a stringified version of the tags, at the moment just for debug purposes
     *
     * @return The String representation of the tags, empty if there are no flags
     */
    public String get_tags() {
        JsonArray arr = _description.getJsonArray("RepoTags");
        if (arr.size() == 0)
            return "";
        return arr.toString();
    }
}
