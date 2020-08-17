package org.hucompute.gazetteer.Models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ObjectCollection {

    String sPath = "";
    List<String> values = new ArrayList<>(0);

    Set<KeyValueObject> kvo = new HashSet<>(0);

    public ObjectCollection(String sPath, String sValues){

        this.sPath = sPath;

        for (String s : sValues.split(",")) {
            values.add(s);
        }


        init();
    }

    void init(){

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(
                    this.sPath));
            String line = reader.readLine();
            while (line != null) {
                // read next line
                line = reader.readLine();

                if (line==null){
                    continue;
                }
                String[] split = line.split("\t");

                Set<KeyValueObject> tempObject = new HashSet<>(0);

                for (int i = 0; i < values.size(); i++) {
                    tempObject.add(new KeyValueObject(values.get(i), split[i]));
                }

                kvo.add(new KeyValueObject(split[0], tempObject));

            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public Set<KeyValueObject> getObjects(){
        return this.kvo;
    }



}
