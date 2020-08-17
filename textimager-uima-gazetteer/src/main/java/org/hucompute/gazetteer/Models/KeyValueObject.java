package org.hucompute.gazetteer.Models;

import java.util.Set;

public class KeyValueObject {

    String sKey = "";
    Object sObject = null;

    public KeyValueObject(String sKey, Object sObject){
        this.sKey = sKey;
        this.sObject = sObject;
    }

    public String getKey() {
        return sKey;
    }

    public Object getObject() {
        return sObject;
    }

    public Object getValue(String sKey){

        Object rObject = null;

        if(sObject instanceof Set){
            for (Object o : ((Set) sObject)) {
                if(o instanceof KeyValueObject){
                    if (((KeyValueObject)o).getKey().equalsIgnoreCase(sKey)){
                        rObject = ((KeyValueObject)o).getObject();
                    }
                }
            }
        }

        return rObject;

    }
}
