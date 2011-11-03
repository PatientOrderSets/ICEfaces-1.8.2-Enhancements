package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.webapp.http.core.SessionExpiredException;

import javax.portlet.PortletContext;
import javax.portlet.PortletSession;
import java.util.Enumeration;

public class ProxyPortletSession implements PortletSession {
    private PortletSession session;

    public ProxyPortletSession(PortletSession session) {
        this.session = session;
    }

    public Object getAttribute(String string) {
        try {
            return session.getAttribute(string);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public Object getAttribute(String string, int i) {
        try {
            return session.getAttribute(string, i);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public Enumeration getAttributeNames() {
        try {
            return session.getAttributeNames();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public Enumeration getAttributeNames(int i) {
        try {
            return session.getAttributeNames();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public long getCreationTime() {
        try {
            return session.getCreationTime();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public String getId() {
        try {
            return session.getId();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public long getLastAccessedTime() {
        try {
            return session.getLastAccessedTime();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public int getMaxInactiveInterval() {
        try {
            return session.getMaxInactiveInterval();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public void invalidate() {
        try {
            session.invalidate();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public boolean isNew() {
        try {
            return session.isNew();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public void removeAttribute(String string) {
        try {
            session.removeAttribute(string);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public void removeAttribute(String string, int i) {
        try {
            session.removeAttribute(string, i);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public void setAttribute(String string, Object object) {
        try {
            session.setAttribute(string, object);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public void setAttribute(String string, Object object, int i) {
        try {
            session.setAttribute(string, object, i);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public void setMaxInactiveInterval(int i) {
        try {
            session.setMaxInactiveInterval(i);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public PortletContext getPortletContext() {
        try {
            return session.getPortletContext();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }
}
