package com.icesoft.util;

import com.icesoft.faces.webapp.http.common.Configuration;
import com.icesoft.faces.webapp.http.common.ConfigurationException;
import com.icesoft.faces.webapp.http.common.Request;
import com.icesoft.faces.webapp.http.servlet.ServletContextConfiguration;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerUtility {
    private static final Log LOG = LogFactory.getLog(ServerUtility.class);

    private static String localAddress;
    static {
        try {
            localAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException exception) {
            localAddress = "127.0.0.1";
        } catch (NoClassDefFoundError e)  {
            // Google App Engine
            localAddress = "GAE";
        }
    }

    public static String getLocalAddr(
        final HttpServletRequest request, final ServletContext servletContext) {

        if (request == null || servletContext == null) {
            return null;
        }
        String _localAddr = null;
        if (servletContext.getMajorVersion() >= 2 &&
            servletContext.getMinorVersion() >= 4) {

            try {
                // Returns null in a portal environment.
                _localAddr = request.getLocalAddr();
            } catch (UnsupportedOperationException exception) {
                // JBoss Portal 2.6.x environment.
            }
        }
        return
            new ServletContextConfiguration("com.icesoft.faces", servletContext).
                getAttribute(
                    "localAddress",
                    _localAddr != null ? _localAddr : localAddress);
    }

    public static String getLocalAddr(
        final Request request, final ServletContext servletContext) {

        if (request == null || servletContext == null) {
            return null;
        }
        if (servletContext.getMajorVersion() >= 2 &&
            servletContext.getMinorVersion() >= 4) {

            return request.getLocalAddr();
        } else {
            Configuration _configuration =
                new ServletContextConfiguration(
                    "com.icesoft.faces", servletContext);
            return _configuration.getAttribute("localAddress", localAddress);
        }
    }

    public static int getLocalPort(
        final HttpServletRequest request, final ServletContext servletContext) {

        if (request == null || servletContext == null) {
            return -1;
        }
        int _localPort = 0;
        if (servletContext.getMajorVersion() >= 2 &&
            servletContext.getMinorVersion() >= 4) {

            _localPort = request.getLocalPort(); // returns 0 in portal env.
        }
        if (_localPort != 0) {
            return
                new ServletContextConfiguration(
                    "com.icesoft.faces", servletContext
                ).getAttributeAsInteger("localPort", _localPort);
        } else {
            try {
                return
                    new ServletContextConfiguration(
                        "com.icesoft.faces", servletContext
                    ).getAttributeAsInteger("localPort");
            } catch (ConfigurationException exception) {
                String _serverInfo = servletContext.getServerInfo();
                if (
                    // GlassFish
                    _serverInfo.startsWith("Sun Java System Application Server") ||
                    _serverInfo.startsWith("Sun GlassFish Enterprise Server") ||
                    // JBoss 4.2.x and up
                    _serverInfo.startsWith("JBoss") ||
                    // JBoss 4.0.x and Tomcat
                    _serverInfo.startsWith("Apache Tomcat") ||
                    // Jetty
                    _serverInfo.startsWith("jetty")) {

                    return 8080;
                } else if (
                    // WebLogic
                    _serverInfo.startsWith("WebLogic")) {

                    return 7001;
                } else {
                    return 8080;
                }
            }
        }
    }

    public static String getServletContextPath(
        final ServletContext servletContext) {

        if (servletContext == null) {
            return null;
        }
        try {
            String _servletContextPath;
            String _path =
                servletContext.getResource("/WEB-INF/web.xml").getPath();
            String _serverInfo = servletContext.getServerInfo();
            if (_serverInfo.startsWith("jetty")) {
                _servletContextPath =
                    _path.substring(
                        _path.indexOf("__") + 2,
                        _path.lastIndexOf("__"));
            } else if (
                _serverInfo.startsWith("WebLogic") &&
                (
                    _serverInfo.indexOf("9.") != -1 ||
                    _serverInfo.indexOf("10.") != -1
                )) {

                int _index = _path.lastIndexOf("/");
                for (int i = 0; i < 3; i++) {
                    _index = _path.lastIndexOf("/", _index - 1);
                }
                _servletContextPath =
                    _path.substring(
                        _path.lastIndexOf("/", _index - 1) + 1, _index);
            } else {
                int _index = _path.lastIndexOf("/", _path.lastIndexOf("/") - 1);
                _servletContextPath =
                    _path.substring(
                        _path.lastIndexOf("/", _index - 1) + 1, _index);
                if (_serverInfo.startsWith("WebLogic")) {
                    _servletContextPath =
                        _servletContextPath.substring(
                            0, _servletContextPath.indexOf(".war"));
                }
            }
            return _servletContextPath;
        } catch (MalformedURLException exception) {
            return null;
        }
    }
}
