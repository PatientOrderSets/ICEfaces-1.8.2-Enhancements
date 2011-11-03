/*
 * ServletConfig 2.4 API
 */

package com.icesoft.faces.mock.test.container;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

/**
 *
 * @author fye
 */
public class MockServletConfig implements ServletConfig{

    private ServletContext servletContext;
    private Hashtable parameters;
    public MockServletConfig(){

    }

    public MockServletConfig(ServletContext servletContext){
        this.servletContext = servletContext;
    }

    public void addInitParameter(String name, String value){
        parameters.put(name, value);
    }


    public String getInitParameter(String name) {
        return (String)parameters.get(name);
    }

    public Enumeration getInitParameterNames() {
        return parameters.keys();
    }

    public ServletContext getServletContext() {
        return servletContext;
    }

    public String getServletName() {
        return "MockServletConfig";
    }

}
