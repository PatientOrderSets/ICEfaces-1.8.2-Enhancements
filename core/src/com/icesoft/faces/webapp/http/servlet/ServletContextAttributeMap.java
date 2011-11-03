package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.servlet.ServletContext;
import java.util.Enumeration;

public class ServletContextAttributeMap extends AbstractAttributeMap {
    private ServletContext context;

    public ServletContextAttributeMap(ServletContext context) {
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
}

