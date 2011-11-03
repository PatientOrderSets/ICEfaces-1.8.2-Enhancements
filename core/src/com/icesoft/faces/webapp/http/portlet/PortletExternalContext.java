package com.icesoft.faces.webapp.http.portlet;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.View;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.env.RequestAttributes;
import com.icesoft.faces.util.EnumerationIterator;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.servlet.ServletRequestAttributes;
import com.icesoft.faces.webapp.http.servlet.SessionDispatcher;
import com.icesoft.jasper.Constants;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class PortletExternalContext extends BridgeExternalContext {
    private static final Log Log = LogFactory.getLog(BridgeExternalContext.class);
    private static final AllowMode DoNotAllow = new AllowMode() {
        public boolean isPortletModeAllowed(PortletMode portletMode) {
            return false;
        }

        public boolean isWindowStateAllowed(WindowState windowState) {
            return false;
        }
    };
    private static final Redirector NOOPRedirector = new Redirector() {
        public void redirect(String uri) {
        }
    };
    private static final CookieTransporter NOOPCookieTransporter = new CookieTransporter() {
        public void send(Cookie cookie) {
        }
    };
    private final PortletContext context;
    private final PortletConfig config;
    private final PortletSession session;
    private PortletEnvironmentRenderRequest initialRequest;
    private RenderResponse response;
    private AllowMode allowMode;
    private Dispatcher dispatcher;
    private RequestAttributes requestAttributes;
    private Configuration configuration;
    private List locales;

    public static final String INACTIVE_INCREMENT = "inactiveIncrement";
    private boolean adjustPortletSessionInactiveInterval = true;

    public PortletExternalContext(String viewIdentifier, final Object request, Object response, View commandQueue, Configuration configuration, final SessionDispatcher.Monitor monitor, Object portletConfig, Authorization authorization, BridgeFacesContext facesContext) {
        super(viewIdentifier, commandQueue, configuration, authorization, facesContext);
        final RenderRequest renderRequest = (RenderRequest) request;
        final RenderResponse renderResponse = (RenderResponse) response;

        this.configuration = configuration;
        config = (PortletConfig) portletConfig;
        session = new InterceptingPortletSession(renderRequest.getPortletSession(), monitor);
        context = session.getPortletContext();
        initParameterMap = new PortletContextInitParameterMap(context);
        applicationMap = new PortletContextAttributeMap(context);
        sessionMap = new PortletSessionAttributeMap(session);
        requestMap = Collections.EMPTY_MAP;

        //ICE-2846: check for deprecated configuration parameter as well
        adjustPortletSessionInactiveInterval = configuration.getAttributeAsBoolean("portlet.adjustSessionInactiveInterval",
                configuration.getAttributeAsBoolean("adjustPortletSessionInactiveInterval", true));

        updateOnPageLoad(renderRequest, renderResponse);
        insertNewViewrootToken();
        switchToNormalMode();
    }

    public Object getSession(boolean create) {
        return session;
    }

    public Object getContext() {
        return context;
    }

    public Object getRequest() {
        return initialRequest;
    }

    public Object getResponse() {
        return response;
    }

    public void update(final HttpServletRequest request, HttpServletResponse response) {

        //ICE-2519
        if (adjustPortletSessionInactiveInterval) {
            adjustPortletSessionInactiveInterval();
        }

        //update parameters
        boolean persistSeamKey = isSeamLifecycleShortcut();
        recreateParameterAndCookieMaps();

        //#2139 removed call to insert postback key here.
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            requestParameterMap.put(name, request.getParameter(name));
            requestParameterValuesMap.put(name, request.getParameterValues(name));
        }


        Object req = request.getAttribute("javax.portlet.request");

        if ((req != null) && (req instanceof PortletRequest)) {
            PortletRequest portletRequest = (PortletRequest) req;

            Enumeration propertyNames = portletRequest.getPropertyNames();
            while (propertyNames.hasMoreElements()) {
                String name = (String) propertyNames.nextElement();
                requestHeaderMap.put(name, portletRequest.getProperty(name));
                requestHeaderValuesMap.put(name, portletRequest.getProperties(name));
            }
        }

        if (persistSeamKey) setSeamLifecycleShortcut();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                requestCookieMap.put(cookie.getName(), cookie);
            }
        }
        allowMode = DoNotAllow;
        requestAttributes = new ServletRequestAttributes(request);
        dispatcher = CannotDispatchOnXMLHTTPRequest;
    }

    //ICE-2519: Because Ajax requests don't go through the portal container, the portal session's lastAccessed
    //value is not properly updated. To compensate, we synthetically manipulate the maxInactiveInterval
    //to ensure that the session doesn't timeout prematurely.  This is only done for client-initiated
    //Ajax requests. Requests, like reloads, that go through the portal container, will set the lastAccessed
    //time properly.
    private void adjustPortletSessionInactiveInterval() {
        Object inactiveIncObj = session.getAttribute(INACTIVE_INCREMENT);
        long inactiveIncrement;

        if (inactiveIncObj == null) {
            inactiveIncrement = session.getMaxInactiveInterval() * 1000;
            session.setAttribute(INACTIVE_INCREMENT, new Long(inactiveIncrement));
        } else {
            inactiveIncrement = ((Long) inactiveIncObj).longValue();
        }

        long lastAccessed = session.getLastAccessedTime();
        session.setMaxInactiveInterval((int) (((System.currentTimeMillis() - lastAccessed) + inactiveIncrement) / 1000));
        if (Log.isTraceEnabled()) {
            Log.trace("max inactive interval adjust to " + session.getMaxInactiveInterval());
        }
    }


    public void update(final RenderRequest request, final RenderResponse response) {
        //update parameters
        boolean persistSeamKey = isSeamLifecycleShortcut();
        recreateParameterAndCookieMaps();

        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            requestParameterMap.put(name, request.getParameter(name));
            requestParameterValuesMap.put(name, request.getParameterValues(name));
        }

        if (persistSeamKey) setSeamLifecycleShortcut();

        locales = Collections.list(request.getLocales());
        allowMode = new PortletRequestAllowMode(request);
        requestAttributes = new PortletRequestAttributes(request);
        this.response = response;
    }

    public void updateOnPageLoad(Object request, Object response) {
        final RenderRequest renderRequest = (RenderRequest) request;
        final RenderResponse renderResponse = (RenderResponse) response;

        detectedAuthorization = detectAuthorization(renderRequest.getUserPrincipal());
        initialRequest = new PortletEnvironmentRenderRequest(session, renderRequest, configuration, detectedAuthorization) {
            public AllowMode allowMode() {
                return allowMode;
            }

            public RequestAttributes requestAttributes() {
                return requestAttributes;
            }

            public Enumeration getLocales() {
                return Collections.enumeration(locales);
            }
        };
        Map previousRequestMap = requestMap;
        requestMap = Collections.synchronizedMap(new PortletRequestAttributeMap(initialRequest));
        //propagate attributes
        requestMap.putAll(previousRequestMap);
        update(renderRequest, renderResponse);
        dispatcher = new Dispatcher() {
            public void dispatch(String path) throws IOException, FacesException {
                try {
                    context.getRequestDispatcher(path).include(renderRequest, renderResponse);
                } catch (PortletException e) {
                    throw new FacesException(e);
                }
            }
        };
    }

    public Map getRequestHeaderMap() {
        return requestHeaderMap;
    }

    public Map getRequestHeaderValuesMap() {
        return requestHeaderValuesMap;
    }

    public Locale getRequestLocale() {
        return initialRequest.getLocale();
    }

    public Iterator getRequestLocales() {
        return new EnumerationIterator(initialRequest.getLocales());
    }

    public String getRequestPathInfo() {
        return (String) initialRequest.getAttribute(Constants.INC_PATH_INFO);
    }

    String contextPath = null;

    public String getRequestContextPath() {
        if (null == contextPath) {
            contextPath = (String) initialRequest.getAttribute(Constants.INC_CONTEXT_PATH);
        }
        return contextPath;
    }

    public String getRequestServletPath() {
        return (String) initialRequest.getAttribute(Constants.INC_SERVLET_PATH);
    }

    public Set getResourcePaths(String path) {
        return context.getResourcePaths(path);
    }

    public URL getResource(String path) throws MalformedURLException {
        return context.getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return context.getResourceAsStream(path);
    }

    public String encodeActionURL(String url) {
        return encodeResourceURL(url);
    }

    public String encodeResourceURL(String url) {
        try {
            return response.encodeURL(url);
        } catch (Exception e) {
            return url;
        }
    }

    public String encodeNamespace(String name) {
        return response.getNamespace() + name;
    }

    public void dispatch(String path) throws IOException, FacesException {
        dispatcher.dispatch(path);
    }

    public void log(String message) {
        context.log(message);
    }

    public void log(String message, Throwable throwable) {
        context.log(message, throwable);
    }

    public String getAuthType() {
        return initialRequest.getAuthType();
    }

    public String getRemoteUser() {
        return initialRequest.getRemoteUser();
    }

    public java.security.Principal getUserPrincipal() {
        return initialRequest.getUserPrincipal();
    }

    public Writer getWriter(String encoding) throws IOException {
        return response.getWriter();
    }

    public PortletConfig getConfig() {
        return config;
    }

    public void switchToNormalMode() {
        redirector = NOOPRedirector;
        cookieTransporter = NOOPCookieTransporter;
    }

    public void switchToPushMode() {
        redirector = new CommandQueueRedirector();
        cookieTransporter = new CommandQueueCookieTransporter();
        resetRequestMap();
    }

    public void release() {
        /**
         * ICE-2990/JBSEAM-3426:- have to save the seam request variables
         * for seam redirection
         */
        String requestServletPath = (String) requestAttributes.getAttribute("org.jboss.seam.web.requestServletPath");
        String requestContextPath = (String) requestAttributes.getAttribute("org.jboss.seam.web.requestContextPath");
        super.release();
        initialRequest.repopulatePseudoAPIAttributes();
        allowMode = DoNotAllow;
        dispatcher = RequestNotAvailable;
        if (SeamUtilities.isSeamEnvironment()) {
            // put them back in
            requestAttributes.setAttribute("org.jboss.seam.web.requestServletPath", requestServletPath);
            requestAttributes.setAttribute("org.jboss.seam.web.requestContextPath", requestContextPath);
            requestAttributes.setAttribute("org.jboss.seam.web.requestPathInfo", "");
        } else {
            requestAttributes = NOOPRequestAttributes;
        }
    }

    /**
     * called from PersistentFacesState.execute() as these request attributes give problems
     * to ajax-push with Seam.
     */
    public void removeSeamAttributes() {
        requestAttributes = NOOPRequestAttributes;
    }
}
