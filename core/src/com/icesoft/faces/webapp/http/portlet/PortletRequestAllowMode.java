package com.icesoft.faces.webapp.http.portlet;

import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;

public class PortletRequestAllowMode implements AllowMode {
    private final PortletRequest request;


    public PortletRequestAllowMode(PortletRequest request) {
        this.request = request;
    }

    public boolean isPortletModeAllowed(PortletMode portletMode) {
        return request.isPortletModeAllowed(portletMode);
    }

    public boolean isWindowStateAllowed(WindowState windowState) {
        return request.isWindowStateAllowed(windowState);
    }
}
