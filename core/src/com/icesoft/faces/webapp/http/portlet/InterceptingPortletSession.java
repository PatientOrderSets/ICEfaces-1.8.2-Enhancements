package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;

import javax.portlet.PortletSession;

public class InterceptingPortletSession extends ProxyPortletSession {
    private final SessionDispatcher.Monitor monitor;

    public InterceptingPortletSession(PortletSession portletSession, SessionDispatcher.Monitor monitor) {
        super(portletSession);
        this.monitor = monitor;
    }

    public void invalidate() {
        monitor.shutdown();
    }
}