package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.context.AbstractAttributeMap;

import javax.portlet.PortletSession;
import java.util.Enumeration;

public class PortletSessionAttributeMap extends AbstractAttributeMap {
    private PortletSession session;

    public PortletSessionAttributeMap(PortletSession session) {
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