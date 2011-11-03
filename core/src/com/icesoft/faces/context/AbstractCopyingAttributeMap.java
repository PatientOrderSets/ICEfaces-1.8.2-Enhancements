package com.icesoft.faces.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractCopyingAttributeMap extends HashMap {

    protected void initialize() {
        Enumeration e = getAttributeNames();
        while (e.hasMoreElements()) {
            String key = String.valueOf(e.nextElement());
            Object value = getAttribute(key);
            super.put(key, value);
        }
    }

    public Object put(Object key, Object value) {
        setAttribute(String.valueOf(key), value);
        return super.put(key, value);
    }

    public void putAll(Map map) {
        Iterator i = map.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            put(entry.getKey(), entry.getValue());
        }
    }

    public Object remove(Object o) {
        removeAttribute((String) o);
        return super.remove(o);
    }

    public void clear() {
        //copy the enumeration to avoid concurrency problems
        Iterator i = new ArrayList(Collections.list(getAttributeNames())).iterator();
        while (i.hasNext()) {
            remove(i.next());
        }
    }

    public abstract Enumeration getAttributeNames();

    public abstract Object getAttribute(String name);

    public abstract void setAttribute(String name, Object value);

    public abstract void removeAttribute(String name);
}
