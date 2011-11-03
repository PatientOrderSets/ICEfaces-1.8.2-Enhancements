package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.context.AbstractCopyingAttributeMap;

import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

public class PortletRequestAttributeMap extends AbstractCopyingAttributeMap {
    private Collection localAttributes = new HashSet();
    private PortletRequest request;

    public PortletRequestAttributeMap(PortletRequest request) {
        this.request = request;
        initialize();
    }

    public Enumeration getAttributeNames() {
        return request.getAttributeNames();
    }

    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    public void setAttribute(String name, Object value) {
        localAttributes.add(name);
        request.setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        localAttributes.remove(name);
        request.removeAttribute(name);
    }

    public void clear() {
        Iterator i = new ArrayList(localAttributes).iterator();
        while (i.hasNext()) {
            Object name = i.next();
            super.remove(name);
        }
        localAttributes.clear();
    }
}