package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.ConfigurationException;

import javax.servlet.ServletConfig;

public class ServletConfigConfiguration
extends Configuration {
    private String name;
    private ServletConfig servletConfig;

    public ServletConfigConfiguration(
        final String prefix, final ServletConfig servletConfig) {

        this.name = prefix;
        this.servletConfig = servletConfig;
    }


    public String getAttribute(final String name)
    throws ConfigurationException {
        String _name = postfixWith(name);
        String _value = servletConfig.getInitParameter(_name);
        if (_value == null) {
            throw new ConfigurationException("Cannot find parameter: " + _name);
        } else {
            return _value;
        }
    }

    public Configuration getChild(final String child)
    throws ConfigurationException {
        String _name = postfixWith(child);
        if (servletConfig.getInitParameter(_name) == null) {
            throw new ConfigurationException("Cannot find parameter: " + _name);
        } else {
            return new ServletConfigConfiguration(_name, servletConfig);
        }
    }

    public Configuration[] getChildren(final String name)
    throws ConfigurationException {
        return new Configuration[] { getChild(name) };
    }

    public String getName() {
        return name;
    }

    public String getValue()
    throws ConfigurationException {
        String _value = servletConfig.getInitParameter(name);
        if (_value == null) {
            throw new ConfigurationException("Cannot find parameter: " + name);
        } else {
            return _value;
        }
    }

    private String postfixWith(final String child) {
        return
            new StringBuffer().
                append(name).append('.').append(child).toString();
    }
}
