package com.icesoft.faces.component.util;

import java.util.HashMap;
import java.util.Map;
/*
 * This class is created to give a java interface to the comma separated values
 * support format: key!value, key!value, ....
 * Along with this class a javascript utility added names as "Ice.delimitedProperties" 
 * which allows to create above formated string via object based API.  
 */
public class DelimitedProperties {
    Map properties = new HashMap();
    public DelimitedProperties(String rawProps) {
        if (null == rawProps || "".equals(rawProps))return;
        String[] props = rawProps.split(",");
        for (int i=0; i < props.length; i++) {
            String prop = props[i];
            String key = prop.substring(0, prop.indexOf("!"));
            String value = prop.substring(prop.indexOf("!")+1); 
            properties.put(key, value);
        }
    }
    
    public String get(String key) {
        return (String)properties.get(key); 
    }
    
    public void set(String key, String value) {
        properties.put(key, value);
    }
    
    public Map getProperties() {
        return properties;
    }
}
