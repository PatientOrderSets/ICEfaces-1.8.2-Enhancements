package com.icesoft.faces.webapp.http.servlet;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.ConfigurationException;

import javax.servlet.ServletContext;

public class ServletContextConfiguration extends Configuration {
    private final String name;
    private ServletContext context;

    public ServletContextConfiguration(String prefix, ServletContext context) {
        this.name = prefix;
        this.context = context;
    }

    public String getName() {
        return name;
    }

    public Configuration getChild(String child) throws ConfigurationException {
        String childName = postfixWith(child);
        String value = context.getInitParameter(childName);
        if (value == null) {
            throw new ConfigurationException("Cannot find parameter: " + childName);
        } else {
            return new ServletContextConfiguration(childName, context);
        }
    }

    public Configuration[] getChildren(String name) throws ConfigurationException {
        return new Configuration[]{getChild(name)};
    }

    public String getAttribute(String paramName) throws ConfigurationException {
        String attributeName = postfixWith(paramName);
        String value = context.getInitParameter(attributeName);
        if (value == null) {
            throw new ConfigurationException("Cannot find parameter: " + attributeName);
        } else {
            return value;
        }
    }

    public String getValue() throws ConfigurationException {
        String value = context.getInitParameter(name);
        if (value == null) {
            throw new ConfigurationException("Cannot find parameter: " + name);
        } else {
            return value;
        }
    }

    private String postfixWith(String child) {
        return name + '.' + child;
    }
}
