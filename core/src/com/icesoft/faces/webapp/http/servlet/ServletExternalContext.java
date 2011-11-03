package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.context.BridgeExternalContext;
import com.icesoft.faces.context.BridgeFacesContext;
import com.icesoft.faces.context.View;
import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.env.RequestAttributes;
import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.util.SeamUtilities;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.FacesException;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ServletExternalContext extends BridgeExternalContext {
    private static final Log Log = LogFactory.getLog(ServletExternalContext.class);

    private final ServletContext context;
    private final HttpSession session;
    private RequestAttributes requestAttributes;
    private HttpServletRequest initialRequest;
    private HttpServletResponse response;
    private Dispatcher dispatcher;
    private List locales;

    public ServletExternalContext(String viewIdentifier, final Object req, Object response, View view, Configuration configuration, final SessionDispatcher.Monitor sessionMonitor, Authorization authorization, BridgeFacesContext facesContext) {
        super(viewIdentifier, view, configuration, authorization, facesContext);
        HttpServletRequest request = (HttpServletRequest) req;
        session = new InterceptingServletSession(request.getSession(), sessionMonitor, view);
        context = session.getServletContext();
        initParameterMap = new ServletContextInitParameterMap(context);
        applicationMap = new ServletContextAttributeMap(context);
        sessionMap = new ServletSessionAttributeMap(session);
        requestMap = Collections.EMPTY_MAP;

        updateOnPageLoad(request, response);
        insertNewViewrootToken();
        // #ICE-1722 default to normal mode before the first request
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

    public void update(HttpServletRequest request, HttpServletResponse response) {
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

        Enumeration headerParameterNames = request.getHeaderNames();
        while (headerParameterNames.hasMoreElements()) {
            String name = (String) headerParameterNames.nextElement();
            requestHeaderMap.put(name, request.getHeader(name));
            requestHeaderValuesMap.put(name, request.getHeaders(name));
        }

        ((ServletEnvironmentRequest) initialRequest).setParameters(request);
        if (persistSeamKey) setSeamLifecycleShortcut();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                Cookie cookie = cookies[i];
                requestCookieMap.put(cookie.getName(), cookie);
            }
        }
        requestAttributes = new ServletRequestAttributes(request);
        locales = Collections.list(request.getLocales());
        dispatcher = CannotDispatchOnXMLHTTPRequest;
        this.response = response;
    }

    public void updateOnPageLoad(final Object request, Object response) {
        final HttpServletRequest servletRequest = (HttpServletRequest) request;
        final HttpServletResponse servletResponse = (HttpServletResponse) response;

        detectedAuthorization = detectAuthorization(servletRequest.getUserPrincipal());
        initialRequest = new ServletEnvironmentRequest(request, session, detectedAuthorization) {
            public RequestAttributes requestAttributes() {
                return requestAttributes;
            }

            public Enumeration getLocales() {
                return Collections.enumeration(locales);
            }
        };
        Map previousRequestMap = requestMap;
        requestMap = Collections.synchronizedMap(new ServletRequestAttributeMap(initialRequest));
        //propagate attributes
        requestMap.putAll(previousRequestMap);
        update(servletRequest, servletResponse);
        dispatcher = new Dispatcher() {
            public void dispatch(String path) throws IOException, FacesException {
                try {
                    servletRequest.getRequestDispatcher(path).forward(servletRequest, servletResponse);
                } catch (ServletException e) {
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
        return Collections.list(initialRequest.getLocales()).iterator();
    }

    public String getRequestPathInfo() {
        return convertEmptyStringToNull(requestPathInfo == null ? initialRequest.getPathInfo() : requestPathInfo);
    }

    String contextPath = null;

    public String getRequestContextPath() {
        return contextPath == null ? initialRequest.getContextPath() : contextPath;
    }

    public String getRequestServletPath() {
        return requestServletPath == null ? initialRequest.getServletPath() : requestServletPath;
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
        return url;
    }

    public String encodeResourceURL(String url) {
        try {
            return response.encodeURL(url);
        } catch (Exception e) {
            return url;
        }
    }

    public String encodeNamespace(String name) {
        return name;
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

    public Principal getUserPrincipal() {
        return initialRequest.getUserPrincipal();
    }

    public Writer getWriter(String encoding) throws IOException {
        try {
            return new OutputStreamWriter(response.getOutputStream(), encoding);
        } catch (IllegalStateException e) {
            // getWriter() already called, perhaps because of JSP include
            return response.getWriter();
        }
    }

    /**
     * Switch to normal redirection mode, using the HTTPServletResponse object
     */
    public void switchToNormalMode() {
        redirector = new Redirector() {
            public void redirect(String uri) {
                try {
                    response.sendRedirect(uri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        cookieTransporter = new CookieTransporter() {
            public void send(Cookie cookie) {
                response.addCookie(cookie);
            }
        };
    }

    public void switchToPushMode() {
        redirector = new CommandQueueRedirector();
        cookieTransporter = new CommandQueueCookieTransporter();
    }

    public void release() {
        /**
         * ICE-2990/JBSEAM-3426:- have to save the seam request variables
         * for seam redirection
         */
        String requestServletPath = (String) requestAttributes.getAttribute("org.jboss.seam.web.requestServletPath");
        String requestContextPath = (String) requestAttributes.getAttribute("org.jboss.seam.web.requestContextPath");
        super.release();
        if (SeamUtilities.isSeamEnvironment()) {
            // put them back in
            requestAttributes.setAttribute("org.jboss.seam.web.requestServletPath", requestServletPath);
            requestAttributes.setAttribute("org.jboss.seam.web.requestContextPath", requestContextPath);
            requestAttributes.setAttribute("org.jboss.seam.web.requestPathInfo", "");
        } else {
            //disable any changes on the request once the response was commited
            requestAttributes = NOOPRequestAttributes;
        }
        dispatcher = RequestNotAvailable;
    }

    /**
     * called from PersistentFacesState.execute() as these request attributes give problems
     * to ajax-push with Seam. ICE-2990,JBSEAM-3426
     */
    public void removeSeamAttributes() {
        requestAttributes = NOOPRequestAttributes;
    }


    /**
     * Utility method that returns the original value of the supplied String
     * unless it is emtpy (val.trim().length() == 0).  In that particlar case
     * the value returned is null.
     *
     * @param val
     * @return
     */
    private static String convertEmptyStringToNull(String val) {
        return val == null || val.trim().length() == 0 ? null : val;
    }

}
