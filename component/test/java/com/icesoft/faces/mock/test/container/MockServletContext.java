/*
 * ServletContext 2.4 API
 */
package com.icesoft.faces.mock.test.container;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 *
 * @author fye
 */
public class MockServletContext implements ServletContext {

    private Hashtable attributes = new Hashtable();
    private Hashtable parameters = new Hashtable();


    public void InitParameters(String name, String value){
        parameters.put(name, value);
    }



    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames() {
        return attributes.keys();
    }

    public ServletContext getContext(String uripath) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getContextPath() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getInitParameter(String name) {
        return (String)parameters.get(name);
    }

    public Enumeration getInitParameterNames() {
        return parameters.keys();
    }

    public int getMajorVersion() {
        return 2;
    }

    public String getMimeType(String file) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getMinorVersion() {
        return 4;
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getRealPath(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public URL getResource(String path) throws MalformedURLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InputStream getResourceAsStream(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Set getResourcePaths(String path) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getServerInfo() {
        return "MockServletContext";
    }

    public Servlet getServlet(String name) throws ServletException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String getServletContextName() {
        return "MockServletContext";
    }

    public Enumeration getServletNames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Enumeration getServlets() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void log(String msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void log(Exception exception, String msg) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void log(String message, Throwable throwable) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public void setAttribute(String name, Object object) {
        attributes.put(name, object);
    }
}
