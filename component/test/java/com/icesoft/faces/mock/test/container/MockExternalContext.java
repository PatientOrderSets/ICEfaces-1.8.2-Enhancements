/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.icesoft.faces.mock.test.container;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.context.ExternalContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author fye
 */
public class MockExternalContext extends ExternalContext {


    public MockExternalContext(ServletContext context,
                               ServletRequest request,
                               ServletResponse response) {
        this.context = context;
        this.request = request;
        this.response = response;
    }
    

    private ServletContext context = null;
    private ServletRequest request = null;
    private ServletResponse response = null;


    public Object getSession(boolean create) {
        throw new UnsupportedOperationException();
    }
    

    public Object getContext() {
        return (context);
    }
    
    
    public Object getRequest() {
        return (request);
    }

    public void setRequest(Object request) {
	throw new UnsupportedOperationException();
    }

    public Object getResponse() {
        return (response);
    }

    public void setResponse(Object response) {
	throw new UnsupportedOperationException();
    }

    public void setResponseCharacterEncoding(String encoding) {
	throw new UnsupportedOperationException();
    }

    private Map applicationMap = null;
    public Map getApplicationMap() {
        if (applicationMap == null) {
            applicationMap = new MockApplicationMap(context);
        }
        return (applicationMap);
    }
    

    private Map sessionMap = null;
    public Map getSessionMap() {
        if (sessionMap == null) {
            sessionMap = new MockSessionMap
                (((HttpServletRequest) request).getSession(true));
        }
        return (sessionMap);
    }
    

    private Map requestMap = null;
    public Map getRequestMap() {
        if (requestMap == null) {
            requestMap = new MockRequestMap(request);
        }
        return (requestMap);
    }
    

    private Map requestParameterMap = null;
    public Map getRequestParameterMap() {
        if (requestParameterMap != null) {
            return (requestParameterMap);
        } else {
            throw new UnsupportedOperationException();
        }
    }
    public void setRequestParameterMap(Map requestParameterMap) {
        this.requestParameterMap = requestParameterMap;
    }

    public void setRequestCharacterEncoding(String encoding) throws UnsupportedEncodingException {
        throw new UnsupportedOperationException();
    }
    

    public Map getRequestParameterValuesMap() {
        throw new UnsupportedOperationException();        
    }

    
    public Iterator getRequestParameterNames() {
        throw new UnsupportedOperationException();
    }

    
    public Map getRequestHeaderMap() {
        throw new UnsupportedOperationException();
    }


    public Map getRequestHeaderValuesMap() {
        throw new UnsupportedOperationException();
    }


    public Map getRequestCookieMap() {
        throw new UnsupportedOperationException();
    }


    public Locale getRequestLocale() {
        return (request.getLocale());
    }
    

    public Iterator getRequestLocales() {
        return (new LocalesIterator(request.getLocales()));
    }
    

    public String getRequestPathInfo() {
        throw new UnsupportedOperationException();
    }


    public String getRequestContextPath() {
        throw new UnsupportedOperationException();
    }

    public String getRequestServletPath() {
        throw new UnsupportedOperationException();
    }
    
    public String getRequestCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    
    public String getRequestContentType() {
        throw new UnsupportedOperationException();
    }

    public String getResponseCharacterEncoding() {
        throw new UnsupportedOperationException();
    }
    
    public String getResponseContentType() {
        throw new UnsupportedOperationException();
    }


    public String getInitParameter(String name) {
	if (name.equals(javax.faces.application.StateManager.STATE_SAVING_METHOD_PARAM_NAME)) {
	    return null;
	}
	if (name.equals(javax.faces.webapp.FacesServlet.LIFECYCLE_ID_ATTR)) {
	    return null;
	}
        return new String("0");
    }


    public Map getInitParameterMap() {
        throw new UnsupportedOperationException();
    }


    public Set getResourcePaths(String path) {
        throw new UnsupportedOperationException();
    }


    public URL getResource(String path) throws MalformedURLException {
        throw new UnsupportedOperationException();
    }


    public InputStream getResourceAsStream(String path) {
        throw new UnsupportedOperationException();
    }


    public String encodeActionURL(String sb) {
        throw new UnsupportedOperationException();
    }


    public String encodeResourceURL(String sb) {
        throw new UnsupportedOperationException();
    }


    public String encodeNamespace(String aValue) {
        throw new UnsupportedOperationException();
    }


    public void dispatch(String requestURI)
        throws IOException, FacesException {
        throw new UnsupportedOperationException();
    }

    
    public void redirect(String requestURI)
        throws IOException {
        throw new UnsupportedOperationException();
    }

    
    public void log(String message) {
        context.log(message);
    }


    public void log(String message, Throwable throwable) {
        context.log(message, throwable);
    }


    public String getAuthType() {
        return (((HttpServletRequest) request).getAuthType());
    }

    public String getRemoteUser() {
        return (((HttpServletRequest) request).getRemoteUser());
    }

    public java.security.Principal getUserPrincipal() {
        return (((HttpServletRequest) request).getUserPrincipal());
    }

    public boolean isUserInRole(String role) {
        return (((HttpServletRequest) request).isUserInRole(role));
    }


    private class LocalesIterator implements Iterator {

	public LocalesIterator(Enumeration locales) {
	    this.locales = locales;
	}

	private Enumeration locales;

	public boolean hasNext() { return locales.hasMoreElements(); }

	public Object next() { return locales.nextElement(); }

	public void remove() { throw new UnsupportedOperationException(); }

    }


}
