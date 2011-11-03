package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.servlet.ServletContext;
import java.util.Enumeration;

public class ServletContextInitParameterMap extends AbstractAttributeMap {
    private ServletContext context;

    public ServletContextInitParameterMap(ServletContext context) {
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