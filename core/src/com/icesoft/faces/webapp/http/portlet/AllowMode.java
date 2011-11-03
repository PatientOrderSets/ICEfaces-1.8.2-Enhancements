package com.icesoft.faces.webapp.http.portlet;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

public interface AllowMode {

    boolean isPortletModeAllowed(PortletMode portletMode);

    boolean isWindowStateAllowed(WindowState windowState);
}
