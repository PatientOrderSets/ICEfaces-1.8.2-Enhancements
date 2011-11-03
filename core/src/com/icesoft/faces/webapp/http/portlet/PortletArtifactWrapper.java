package com.icesoft.faces.webapp.http.portlet;

import javax.portlet.PortletConfig;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

/**
 * Because we are using a RequestDispatcher to bridge portlet handling into our servlet
 * based framework, we "lose" some of the characteristics of the Portlet API.  When
 * the dispatched call arrives at the ICEfaces MainServlet, the request and response
 * objects are wrapped as servlet versions and no longer accessible as portlet types.
 *
 * What we currently do, then, is save instances of those things that the portlet
 * developer might want to access and make them accessible on the other side of
 * the dispatched call.  This class is simply the envelope they are carried in.
 */
public class PortletArtifactWrapper {

    public static final String PORTLET_ARTIFACT_KEY =
            "com.icesoft.faces.portlet.artifact";

    private PortletConfig portletConfig;
    private RenderRequest request;
    private RenderResponse response;

    public PortletArtifactWrapper(PortletConfig portletConfig,
                               RenderRequest request, RenderResponse response) {
        this.portletConfig = portletConfig;
        this.request = request;
        this.response = response;
    }

    public RenderRequest getRequest() {
        return this.request;
    }

    public RenderResponse getResponse() {
        return response;
    }

    public PortletConfig getPortletConfig() {
        return (this.portletConfig);
    }
}
