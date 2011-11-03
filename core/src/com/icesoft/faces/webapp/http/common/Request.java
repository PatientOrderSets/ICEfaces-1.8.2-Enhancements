package com.icesoft.faces.webapp.http.common;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;

public interface Request {

    String getMethod();

    URI getURI();

    String[] getHeaderNames();

    String getHeader(String name);

    String[] getHeaderAsStrings(String name);

    Date getHeaderAsDate(String name);

    int getHeaderAsInteger(String name);

    boolean containsParameter(String name);

    String[] getParameterNames();

    String getParameter(String name);

    String[] getParameterAsStrings(String name);

    int getParameterAsInteger(String name);

    boolean getParameterAsBoolean(String name);

    String getParameter(String name, String defaultValue);

    int getParameterAsInteger(String name, int defaultValue);

    boolean getParameterAsBoolean(String name, boolean defaultValue);

    Cookie[] getCookies();

    String getLocalAddr();

    String getLocalName();

    String getRemoteAddr();

    String getRemoteHost();

    String getServerName();

    InputStream readBody() throws IOException;

    void readBodyInto(OutputStream out) throws IOException;

    void respondWith(ResponseHandler handler) throws Exception;

    void detectEnvironment(Environment environment) throws Exception;

    //avoid runtime dependency on Portlet interfaces,
    //and for the symmetry's sake, same for the Servlet interfaces
    interface Environment {

        void servlet(Object request, Object response) throws Exception;

        void portlet(Object request, Object response, Object config) throws Exception;
    }
}
