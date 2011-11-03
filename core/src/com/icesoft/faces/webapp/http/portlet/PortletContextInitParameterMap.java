package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.portlet.PortletContext;
import java.util.Enumeration;

public class PortletContextInitParameterMap extends AbstractAttributeMap {
    private PortletContext context;

    public PortletContextInitParameterMap(PortletContext context) {
        this.context = context;
    }

    protected Object getAttribute(String key) {
        return context.getInitParameter(key);
    }

    protected void setAttribute(String key, Object value) {
        throw new IllegalAccessError("Read only map.");
    }

    protected void removeAttribute(String key) {
        throw new IllegalAccessError("Read only map.");
    }

    protected Enumeration getAttributeNames() {
        return context.getInitParameterNames();
    }
}
