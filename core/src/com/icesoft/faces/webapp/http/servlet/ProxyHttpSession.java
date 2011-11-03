package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.core.SessionExpiredException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;
import java.util.Enumeration;

public class ProxyHttpSession implements HttpSession {
    protected HttpSession session;

    public ProxyHttpSession(HttpSession session) {
        this.session = session;
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

    public ServletContext getServletContext() {
        try {
            return session.getServletContext();
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

    public int getMaxInactiveInterval() {
        try {
            return session.getMaxInactiveInterval();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public HttpSessionContext getSessionContext() {
        try {
            return session.getSessionContext();
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public Object getAttribute(String string) {
        try {
            return session.getAttribute(string);
        } catch (IllegalStateException e) {
            throw new SessionExpiredException(e);
        }
    }

    public Object getValue(String string) {
        try {
            return session.getValue(string);
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

    public String[] getValueNames() {
        try {
            return session.getValueNames();
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

    public void putValue(String string, Object object) {
        try {
            session.putValue(string, object);
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

    public void removeValue(String string) {
        try {
            session.removeValue(string);
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
}
