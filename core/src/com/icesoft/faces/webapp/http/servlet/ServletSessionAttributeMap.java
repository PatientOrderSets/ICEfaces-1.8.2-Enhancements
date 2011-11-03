package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;

public class ServletSessionAttributeMap extends AbstractAttributeMap {
    private HttpSession session;

    public ServletSessionAttributeMap(HttpSession session) {
        this.session = session;
    }

    protected Object getAttribute(String key) {
        return session.getAttribute(key);
    }

    protected void setAttribute(String key, Object value) {
        session.setAttribute(key, value);
    }

    protected void removeAttribute(String key) {
        session.removeAttribute(key);
    }

    protected Enumeration getAttributeNames() {
        return session.getAttributeNames();
    }
}
