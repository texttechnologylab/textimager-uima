package org.hucompute.textimager.uima.marmot;

import org.dkpro.core.api.resources.ModelProviderBase;

import java.io.*;
import java.util.zip.GZIPInputStream;

public class ModelProviderTwo
    extends ModelProviderBase<String> {
    String aShortName;
    String aType;
    public ModelProviderTwo(Object aObject, String aShortName, String aType){
        super(aObject,aShortName,aType);
        this.aShortName=aShortName;
        this.aType=aType;
    }
    @Override
    protected String produceResource(InputStream aStream) throws Exception {

        //get Metadata from stream
        String variant = (String) this.getResourceMetaData().get("variant");
        String language = (String) this.getResourceMetaData().get("language");
        String version = (String) this.getResourceMetaData().get("version");
        String location = (String) this.getResourceMetaData().get("location");
        String tool = (String) this.getResourceMetaData().get("tool");
        String extension = (String) this.aShortName;

        //path to cache folder
        String cachePath = "src/main/resources/" + language + "/" + variant + "/" + version;

        //save stream to file
        saveStream(aStream, cachePath, variant, language, version,extension);

        //create new Filestream from model
        File initialFile = new File(cachePath + "/model." + extension);
        InputStream targetStream = new FileInputStream(initialFile);
        ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream(targetStream));
        Object object = stream.readObject();

        stream.close();
        if (object == null) {
            throw new RuntimeException("Object couldn't be deserialized: ");
        }
        //return (MorphTagger) object;
        return cachePath + "/model." + extension;
    }

    //save Filestream to cache Path
    public void saveStream(InputStream taggerStream, String path, String variant, String language, String version,String extension) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            boolean dirCreated = file.mkdirs();
            FileOutputStream fout = new FileOutputStream(path + "/model."+extension);
            copy(taggerStream, fout);
            System.out.println("Finished Stream writing to cache folder");
        } else {
            System.out.println("Model already exists");
        }
    }

    void copy(InputStream source, OutputStream target) throws IOException {
        byte[] buf = new byte[8192];
        int length;
        while ((length = source.read(buf)) > 0) {
            target.write(buf, 0, length);
        }
    }
}

