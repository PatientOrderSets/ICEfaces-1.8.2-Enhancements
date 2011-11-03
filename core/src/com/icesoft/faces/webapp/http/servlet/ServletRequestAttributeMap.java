package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractCopyingAttributeMap;

import javax.servlet.ServletRequest;
import java.util.Enumeration;

public class ServletRequestAttributeMap extends AbstractCopyingAttributeMap {
    private ServletRequest request;

    public ServletRequestAttributeMap(ServletRequest request) {
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
        request.setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        request.removeAttribute(name);
    }
}