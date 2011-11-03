package com.icesoft.faces.env;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.webflow.context.servlet.ServletExternalContext;
import org.springframework.webflow.executor.FlowExecutor;

public class SpringWebFlowInstantiationServlet extends HttpServlet {
    protected static Log log = LogFactory.getLog(SpringWebFlowInstantiationServlet.class);
    private static String CONFIG_PARAM_NAME = "contextConfigLocation";
    private static FlowExecutor flowExecutor = null;
    private ConfigurableWebApplicationContext container;

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        try {
            initFlowExecutor(servletConfig);
        } catch (Throwable t)  {
            if (log.isErrorEnabled()) {
                log.error("Unable to initialize SpringWebFlowInstantiationServlet ", t );
            }
            throw new ServletException(
                "Unable to initialize SpringWebFlowInstantiationServlet ", t );
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //this servlet is for instantiation SpringWebFlow but does not handle 
        //requests
    }

    public void destroy() {
        container.close();
    }

    public static FlowExecutor getFlowExecutor()  {
        return flowExecutor;
    }

    void initFlowExecutor(ServletConfig config)  {
        container = new XmlWebApplicationContext();
        container.setConfigLocations(getConfigLocations(config));
        container.setServletConfig(config);
        container.setServletContext(config.getServletContext());
        container.refresh();
        flowExecutor = lookupFlowExecutor(container);
    }

    private String[] getConfigLocations(ServletConfig config) {
        String configLocations = config.getInitParameter(CONFIG_PARAM_NAME);
        if (configLocations != null) {
            return StringUtils.tokenizeToStringArray(config.getInitParameter(CONFIG_PARAM_NAME),
                    ConfigurableWebApplicationContext.CONFIG_LOCATION_DELIMITERS);
        } else {
            return new String[] { "/WEB-INF/config/web-application-config.xml" };
        }
    }

    private FlowExecutor lookupFlowExecutor(WebApplicationContext container) {
        String[] beanNames = container.getBeanNamesForType(FlowExecutor.class);
        if (beanNames.length == 0) {
            throw new IllegalStateException("No bean of type FlowExecutor defined in context");
        } else if (beanNames.length > 1) {
            throw new IllegalStateException("More than one bean of type FlowExecutor defined in context.");
        } else {
            return (FlowExecutor) container.getBean(beanNames[0]);
        }
    }


}
