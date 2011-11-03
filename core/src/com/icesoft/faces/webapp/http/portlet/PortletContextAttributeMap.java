package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.portlet.PortletContext;
import java.util.Enumeration;

public class PortletContextAttributeMap extends AbstractAttributeMap {
    private PortletContext context;

    public PortletContextAttributeMap(PortletContext context) {
        this.context = context;
    }

    protected Object getAttribute(String key) {
        return context.getAttribute(key);
    }

    protected void setAttribute(String key, Object value) {
        context.setAttribute(key, value);
    }

    protected void removeAttribute(String key) {
        context.removeAttribute(key);
    }

    protected Enumeration getAttributeNames() {
        return context.getAttributeNames();
    }
};