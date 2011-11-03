/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * "The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations under
 * the License.
 *
 * The Original Code is ICEfaces 1.5 open source software code, released
 * November 5, 2006. The Initial Developer of the Original Code is ICEsoft
 * Technologies Canada, Corp. Portions created by ICEsoft are Copyright (C)
 * 2004-2006 ICEsoft Technologies Canada, Corp. All Rights Reserved.
 *
 * Contributor(s): _____________________.
 *
 * Alternatively, the contents of this file may be used under the terms of
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"
 * License), in which case the provisions of the LGPL License are
 * applicable instead of those above. If you wish to allow use of your
 * version of this file only under the terms of the LGPL License and not to
 * allow others to use your version of this file under the MPL, indicate
 * your decision by deleting the provisions above and replace them with
 * the notice and other provisions required by the LGPL License. If you do
 * not delete the provisions above, a recipient may use your version of
 * this file under either the MPL or the LGPL License."
 *
 */

package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.env.Authorization;
import com.icesoft.faces.env.CommonEnvironmentRequest;
import com.icesoft.faces.env.RequestAttributes;
import com.icesoft.jasper.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * A wrapper for HttpServletRequest.
 * <p/>
 * It is up to the user to ensure that casts to this specific type and use the
 * specific methods if you are running in the appropriate environment.  Also,
 * since we wrap real requests, the state of those requests can get changed by
 * the application server, so it's possible that certain calls may result in
 * exceptions being thrown.
 * <p/>
 */
public abstract class ServletEnvironmentRequest extends CommonEnvironmentRequest
        implements HttpServletRequest {
    private final static Log log = LogFactory.getLog(ServletEnvironmentRequest.class);
    private Map headers;
    private Cookie[] cookies;
    private String method;
    private String pathInfo;
    private String pathTranslated;
    private String queryString;
    private String requestURI;
    private StringBuffer requestURL;
    private String servletPath;
    private HttpSession servletSession;
    private boolean isRequestedSessionIdFromCookie;
    private boolean isRequestedSessionIdFromURL;
    private String characterEncoding;
    private int contentLength;
    private String contentType;
    private String protocol;
    private String remoteAddr;
    private int remotePort;
    private String remoteHost;
    private String localName;
    private String localAddr;
    private int localPort;
    private HttpSession session;
    private Authorization authorization;

    public ServletEnvironmentRequest(Object request, HttpSession session, Authorization authorization) {
        HttpServletRequest initialRequest = (HttpServletRequest) request;
        this.session = session;
        this.authorization = authorization;
        //Copy common data
        authType = initialRequest.getAuthType();
        contextPath = initialRequest.getContextPath();
        remoteUser = initialRequest.getRemoteUser();
        userPrincipal = initialRequest.getUserPrincipal();
        requestedSessionId = initialRequest.getRequestedSessionId();
        requestedSessionIdValid = initialRequest.isRequestedSessionIdValid();

        attributes = new HashMap();
        Enumeration attributeNames = initialRequest.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = (String) attributeNames.nextElement();
            Object attribute = initialRequest.getAttribute(name);
            if ((null != name) && (null != attribute)) {
                attributes.put(name, attribute);
            }
        }

        // Warning:  For some reason, the various javax.include.* attributes are
        // not available via the getAttributeNames() call.  This may be limited
        // to a Liferay issue but when the MainPortlet dispatches the call to
        // the MainServlet, all of the javax.include.* attributes can be
        // retrieved using this.request.getAttribute() but they do NOT appear in
        // the Enumeration of names returned by getAttributeNames().  So here
        // we manually add them to our map to ensure we can find them later.
        String[] incAttrKeys = Constants.INC_CONSTANTS;
        for (int index = 0; index < incAttrKeys.length; index++) {
            String incAttrKey = incAttrKeys[index];
            Object incAttrVal = initialRequest.getAttribute(incAttrKey);
            if (incAttrVal != null) {
                attributes.put(incAttrKey, initialRequest.getAttribute(incAttrKey));
            }
        }

        headers = new HashMap();
        Enumeration headerNames = initialRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            Enumeration values = initialRequest.getHeaders(name);
            headers.put(name, Collections.list(values));
        }

        parameters = new HashMap();
        Enumeration parameterNames = initialRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            parameters.put(name, initialRequest.getParameterValues(name));
        }

        scheme = initialRequest.getScheme();
        serverName = initialRequest.getServerName();
        serverPort = initialRequest.getServerPort();
        secure = initialRequest.isSecure();

        //Copy servlet specific data
        cookies = initialRequest.getCookies();
        method = initialRequest.getMethod();
        pathInfo = initialRequest.getPathInfo();
        pathTranslated = initialRequest.getPathTranslated();
        queryString = initialRequest.getQueryString();
        requestURI = initialRequest.getRequestURI();
        try {
            requestURL = initialRequest.getRequestURL();
        } catch (NullPointerException e) {
            //TODO remove this catch block when GlassFish bug is addressed
            if (log.isErrorEnabled()) {
                log.error("Null Protocol Scheme in request", e);
            }
            HttpServletRequest req = initialRequest;
            requestURL = new StringBuffer("http://" + req.getServerName()
                    + ":" + req.getServerPort() + req.getRequestURI());
        }
        servletPath = initialRequest.getServletPath();
        servletSession = initialRequest.getSession();
        isRequestedSessionIdFromCookie = initialRequest.isRequestedSessionIdFromCookie();
        isRequestedSessionIdFromURL = initialRequest.isRequestedSessionIdFromURL();
        characterEncoding = initialRequest.getCharacterEncoding();
        contentLength = initialRequest.getContentLength();
        contentType = initialRequest.getContentType();
        protocol = initialRequest.getProtocol();
        remoteAddr = initialRequest.getRemoteAddr();
        remoteHost = initialRequest.getRemoteHost();
        initializeServlet2point4Properties(initialRequest);
    }

    private void initializeServlet2point4Properties(HttpServletRequest servletRequest) {
        ServletContext context = servletRequest.getSession().getServletContext();
        if (context.getMajorVersion() > 1 && context.getMinorVersion() > 3) {
            remotePort = servletRequest.getRemotePort();
            localName = servletRequest.getLocalName();
            localAddr = servletRequest.getLocalAddr();
            localPort = servletRequest.getLocalPort();
        }
    }

    public void setPathInfo(String pathInfo) {
        this.pathInfo = pathInfo;
    }

    public void setParameters(HttpServletRequest request) {
        parameters = new HashMap();
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            parameters.put(name, request.getParameterValues(name));
        }
    }

    public boolean isUserInRole(String role) {
        return authorization.isUserInRole(role);
    }

    public Cookie[] getCookies() {
        return cookies;
    }

    public void setAttribute(String name, Object value) {
        super.setAttribute(name, value);
        requestAttributes().setAttribute(name, value);
    }

    public void removeAttribute(String name) {
        super.removeAttribute(name);
        requestAttributes().removeAttribute(name);
    }

    public long getDateHeader(String name) {
        String header = getHeader(name);
        if (header == null) {
            return -1;
        }
        //TODO
        //Convert header string to a date
        return -1;
    }

    public String getHeader(String name) {
        List values = (List) headers.get(name);
        return values == null || values.isEmpty() ?
                null : (String) values.get(0);
    }

    public Enumeration getHeaders(String name) {
        List values = (List) headers.get(name);
        return Collections.enumeration(values);
    }

    public Enumeration getHeaderNames() {
        return Collections.enumeration(headers.keySet());
    }

    public int getIntHeader(String name) {
        String header = getHeader(name);
        if (header == null) {
            return -1;
        }
        return Integer.parseInt(name, -1);
    }

    public String getMethod() {
        return method;
    }

    public String getPathInfo() {
        return pathInfo;
    }

    public String getPathTranslated() {
        return pathTranslated;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public StringBuffer getRequestURL() {
        return requestURL;
    }

    public String getServletPath() {
        return servletPath;
    }

    public HttpSession getSession(boolean create) {
        return session;
    }

    public HttpSession getSession() {
        return servletSession;
    }

    public boolean isRequestedSessionIdFromCookie() {
        return isRequestedSessionIdFromCookie;
    }

    public boolean isRequestedSessionIdFromURL() {
        return isRequestedSessionIdFromURL;
    }

    public boolean isRequestedSessionIdFromUrl() {
        return isRequestedSessionIdFromURL();
    }

    public String getCharacterEncoding() {
        return characterEncoding;
    }

    public void setCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        characterEncoding = encoding;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentType() {
        return contentType;
    }

    public ServletInputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException("Use ResponseWriter instead");
    }

    public String getProtocol() {
        return protocol;
    }

    public BufferedReader getReader() throws IOException {
        throw new UnsupportedOperationException();
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public RequestDispatcher getRequestDispatcher(String name) {
        throw new UnsupportedOperationException("Use navigation rules instead");
    }

    public String getRealPath(String path) {
        return session.getServletContext().getRealPath(path);
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getLocalName() {
        return localName;
    }

    public String getLocalAddr() {
        return localAddr;
    }

    public int getLocalPort() {
        return localPort;
    }

    public abstract RequestAttributes requestAttributes();
}
