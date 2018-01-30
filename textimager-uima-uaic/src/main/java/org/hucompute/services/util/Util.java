package org.hucompute.services.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public class Util {

    public static File getFile(ClassLoader classloader, String resource) {
        File output = new File(System.getProperty("java.io.tmpdir") + "/" + resource);
        if (!output.exists()) {
            URL inputUrl = classloader.getResource(resource);
            try {
                FileUtils.copyURLToFile(inputUrl, output);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return output;
    }

}
